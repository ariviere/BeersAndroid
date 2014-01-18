package com.ar.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ar.adapter.BeerAdapter;
import com.ar.beerkipedia.R;
import com.ar.classes.BackEditText;
import com.ar.classes.Beer;
import com.ar.classes.BeersJSON;
import com.ar.classes.BeersManipulation;

import java.util.ArrayList;

public class ListActivity extends Activity implements NfcAdapter.CreateNdefMessageCallback{
    private static String TAG = "ListActivity";
    private static String allBeers = "https://www.googleapis.com/freebase/v1/mqlread/?lang=%2Flang%2Fen&query=%5B%7B+%22name%22%3A+null%2C+%22id%22%3A+null%2C+%22mid%22%3A+null%2C+%22type%22%3A+%22%2Ffood%2Fbeer%22%2C+%22sort%22%3A+%22name%22%2C+%22alcohol_content%22%3A+null%2C+%22alcohol_content!%3D%22%3A+0%2C+%22beer_style%22%3A+%5B%5D%2C+%22beer_style!%3D%22%3A+%22%5B%5D%22%2C+%22country%22%3A+null%2C+%22from_region%22%3A+%5B%5D%2C+%22first_brewed%22%3A+null%2C+%22ibu_scale%22%3A+null%2C+%22original_gravity%22%3A+null%2C+%22final_gravity%22%3A+null%2C+%22color_srm%22%3A+null%2C+%22containers%22%3A+%5B%5D%2C+%22limit%22%3A+60+%7D%5D&cursor=";
    private static String searchedBeers;
    private static String type = "all";
    private Context context;
    private BeersJSON beerJSON = new BeersJSON();
    private ArrayList<Beer> beers = new ArrayList<Beer>();

    private boolean retrievedBeers = true;
    private boolean searchFormVisible = false;

    private ProgressBar loadingBar;
    private GridView gridView;
    private BackEditText searchView;

    private BeerAdapter beerAdapter;


    private Menu menu;
    private BeersManipulation bm = new BeersManipulation();
    private NfcAdapter mNfcAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.context = this;


        loadingBar  = (ProgressBar) findViewById(R.id.loading);
        gridView    = (GridView)    findViewById(R.id.gridview);
        searchView  = (BackEditText)findViewById(R.id.search);

        startLoading();
        new getAsyncBeers().execute("first");
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mNfcAdapter.setNdefPushMessageCallback(this, this);

        initSearchView();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(searchFormVisible)
            hideSearchForm(true);

        switch (item.getItemId()){
            case android.R.id.home:
                type = "all";
                startLoading();
                new getAsyncBeers().execute("first");
                break;
            case R.id.item_previous:
                startLoading();
                new getAsyncBeers().execute("previous");
                break;
            case R.id.item_next:
                startLoading();
                new getAsyncBeers().execute("next");
                break;
            case R.id.item_search:
                showSearchForm();
                break;
            case R.id.item_custom:
                type = "file";
                startLoading();
                new getAsyncBeers().execute("");
                break;
        }
        return true;
    }

    private void startLoading(){
        loadingBar.setVisibility(View.VISIBLE);
    }

    private void stopLoading(){
        loadingBar.setVisibility(View.GONE);
    }

    private void showSearchForm(){
        searchFormVisible = true;
        menu.findItem(R.id.item_search).setVisible(false);
        menu.findItem(R.id.item_close_search).setVisible(true);
        searchView.setVisibility(View.VISIBLE);
        gridView.setAlpha(0);
        searchView.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
    }

    private void hideSearchForm(Boolean keyboardShown){
        menu.findItem(R.id.item_close_search).setVisible(false);
        menu.findItem(R.id.item_search).setVisible(true);
        if(keyboardShown){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        }

        searchFormVisible = false;
        searchView.setVisibility(View.GONE);
        gridView.setAlpha(256);
    }

    private void initSearchView(){
        searchView.setKeyImeChangeListener(new BackEditText.KeyImeChange() {
            @Override
            public void onKeyIme(int keyCode, KeyEvent event) {
                hideSearchForm(false);
            }
        });

        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    type = "search";
                    String searchName = searchView.getText().toString();
                    searchedBeers = "https://www.googleapis.com/freebase/v1/mqlread/?lang=%2Flang%2Fen&query=%5B%7B+%22name%22%3A+null%2C+%22id%22%3A+null%2C+%22mid%22%3A+null%2C+%22beer_style%22%3A+%5B%5D%2C+%22country%22%3A+null%2C+%22from_region%22%3A+%5B%5D%2C+%22first_brewed%22%3A+null%2C+%22ibu_scale%22%3A+null%2C+%22original_gravity%22%3A+null%2C+%22final_gravity%22%3A+null%2C+%22color_srm%22%3A+null%2C+%22alcohol_content%22%3A+null%2C+%22containers%22%3A+%5B%5D%2C+%22name~%3D%22%3A+%22*" + searchName + "*%22%2C+%22type%22%3A+%22%2Ffood%2Fbeer%22%2C+%22limit%22%3A+50%2C+%22sort%22%3A+%22name%22+%7D%5D";
                    startLoading();
                    new getAsyncBeers().execute("");
                    hideSearchForm(true);

//                    Log.d(TAG, "search: " + url);
//                    Intent intent = new Intent(context, BeersSearchActivity.class);
//                    intent.putExtra("url", url);
//                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }
    public class getAsyncBeers extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... page) {
            ArrayList<Beer> beersJson = null;
            if(type.equals("all"))
                beersJson = beerJSON.getBeers(allBeers, page[0]);
            else if(type.equals("search"))
                beersJson = beerJSON.getBeers(searchedBeers, page[0]);
            else if(type.equals("file"))
                beersJson = new BeersManipulation().getBeersFromFile(context);
            if(beersJson != null){
                beers.clear();
                beers.addAll(beersJson);
            }else
                retrievedBeers = false;

            return page[0];
        }

        @Override
        protected void onPostExecute(String page) {
            if(retrievedBeers){
                beerAdapter = new BeerAdapter(context, beers);
                gridView.invalidateViews();
                gridView.setAdapter(beerAdapter);

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        Intent intent = new Intent(context, SingleActivity.class);
                        intent.putExtra("beer", beers.get(position));
                        startActivityForResult(intent, 0);
                    }
                });
            }else{
                retrievedBeers = true;
                Toast.makeText(context, getResources().getString(R.string.beer_error), Toast.LENGTH_LONG).show();
            }
            stopLoading();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0){
            if(resultCode == RESULT_OK){
                Log.d(TAG, "result type: " + type);
                if(type.equals("file")){
                    startLoading();
                    new getAsyncBeers().execute("");
                }
            }

        }
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        ArrayList<Beer> beers = new BeersManipulation().getBeersFromFile(context);
        byte[] beersBytes = bm.beersArrayListToBytes(beers);

        NdefMessage msg = new NdefMessage(new NdefRecord[] { NdefRecord.createMime("application/com.ar.activities.beamactivity", beersBytes)});

        return msg;
    }
}
