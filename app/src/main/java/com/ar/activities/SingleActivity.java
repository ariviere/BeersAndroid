package com.ar.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ar.beerkipedia.R;
import com.ar.classes.Beer;
import com.ar.classes.BeersJSON;
import com.ar.classes.BeersManipulation;

import java.util.ArrayList;

public class SingleActivity extends ActionBarActivity {
    private static String TAG = "BeerArticle";

    private Context context;
    private Beer beer;

    Bitmap image;
    Button thumbupButton, thumbdownButton;
    ArrayList<Beer> savedBeers;
    BeersManipulation fileManager = new BeersManipulation();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_beer);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        this.context = this;

        thumbupButton = (Button)findViewById(R.id.thumbup);
        thumbdownButton = (Button)findViewById(R.id.thumbdown);

        Bundle bundle = getIntent().getExtras();
        beer = (Beer)bundle.getSerializable("beer");

        savedBeers = fileManager.getBeersFromFile(context);
        for(Beer iterBeer : savedBeers){
            if(iterBeer.getName().equals(beer.getName())){
                if(iterBeer.getThumbup() != null){
                    if(iterBeer.getThumbup())
                        thumbupButton.setSelected(true);
                    else
                        thumbdownButton.setSelected(true);
                }

            }
        }

        setUI();

        new getWikipediaInfos().execute(beer);
    }

    public void onButtonPressed(View view){
        Button touchedButton = (Button)view;
        boolean saveBeer = true;
        if(touchedButton.isSelected()){
            saveBeer = false;
            touchedButton.setSelected(false);
        }
        else{
            if(((Button)view).getId() == thumbupButton.getId()){
                thumbdownButton.setSelected(false);
                thumbupButton.setSelected(true);
                beer.setThumbup(true);
            }else if(((Button)view).getId() == thumbdownButton.getId()){
                thumbdownButton.setSelected(true);
                thumbupButton.setSelected(false);
                beer.setThumbup(false);
            }
        }

        for(Beer iterBeer : savedBeers){
            if(iterBeer.getName().equals(beer.getName())){
                savedBeers.remove(iterBeer);
                break;
            }
        }
        if(saveBeer){
            savedBeers.add(beer);
        }
        fileManager.saveBeersInFile(context, savedBeers);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent returnIntent = new Intent();
            setResult(RESULT_OK, returnIntent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class getWikipediaInfos extends AsyncTask<Beer, Void, Beer> {

        @Override
        protected Beer doInBackground(Beer... beers) {
            image = new BeersJSON().getBeerImg(beer.getId());
            beer.setDescription(new BeersJSON().getBeerDescription(context, beer.getName()));
            return beer;
        }

        @Override
        protected void onPostExecute(Beer beer) {
            ImageView imageView = (ImageView) findViewById(R.id.beer_image);
            imageView.setImageDrawable(new BitmapDrawable(getResources(),image));
            imageView.setVisibility(View.VISIBLE);

            TextView wikiTitle = (TextView)findViewById(R.id.wikipedia);
            TextView descriptionView = (TextView) findViewById(R.id.beer_description);
            if(beer.getDescription() != null){
                wikiTitle.setVisibility(View.VISIBLE);
                descriptionView.setVisibility(View.VISIBLE);
                descriptionView.setText(Html.fromHtml(beer.getDescription()));
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void setUI(){
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/quando.ttf");

        TextView nameView = (TextView)findViewById(R.id.beer_name);
        nameView.setText(beer.getName());
        nameView.setTypeface(type);


        TextView typesView = (TextView)findViewById(R.id.beer_types);
        String beerStyles = beer.getBeer_style();
        if(!beerStyles.equals("[]")){
            beerStyles = beerStyles.replace("[", "");
            beerStyles = beerStyles.replace("]", "");
            beerStyles = beerStyles.replace("\"", "");
            beerStyles = beerStyles.replace(",", " - ");
            typesView.setText(beerStyles);
            typesView.setVisibility(View.VISIBLE);
        }

        LinearLayout regionLayout = (LinearLayout)findViewById(R.id.region_layout);
        TextView region = (TextView)findViewById(R.id.region_value);

        String beerRegions = beer.getFrom_region();
        if(!beerRegions.equals("[]")){
            beerRegions = beerRegions.replace("[", "");
            beerRegions = beerRegions.replace("]", "");
            beerRegions = beerRegions.replace("\"", "");
            beerRegions = beerRegions.replace(",", " - ");
            region.setText(" " + beerRegions);
            regionLayout.setVisibility(View.VISIBLE);
        }else if(beer.getCountry() != null){
            region.setText(" " + beer.getCountry());
            regionLayout.setVisibility(View.VISIBLE);
        }

        LinearLayout firstBrewedLayout = (LinearLayout)findViewById(R.id.first_bewed_layout);
        TextView firstBrewed = (TextView)findViewById(R.id.first_bewed_value);
        if(beer.getFirst_bewed() != null){
            firstBrewedLayout.setVisibility(View.VISIBLE);
            firstBrewed.setText(" " + beer.getFirst_bewed());
        }

        LinearLayout alcohol_contentLayout = (LinearLayout)findViewById(R.id.alcohol_content_layout);
        TextView alcohol_content = (TextView)findViewById(R.id.alcohol_content_value);
        if(beer.getAlcohol_content() != null){
            alcohol_contentLayout.setVisibility(View.VISIBLE);
            alcohol_content.setText(" " + beer.getAlcohol_content());
        }

        LinearLayout ibu_scaleLayout = (LinearLayout)findViewById(R.id.ibu_scale_layout);
        TextView ibu_scale = (TextView)findViewById(R.id.ibu_scale_value);
        if(beer.getIbu_scale() != null){
            ibu_scaleLayout.setVisibility(View.VISIBLE);
            ibu_scale.setText(" " + beer.getIbu_scale());
        }

        LinearLayout original_gravityLayout = (LinearLayout)findViewById(R.id.original_gravity_layout);
        TextView original_gravity = (TextView)findViewById(R.id.original_gravity_value);
        if(beer.getOriginal_gravity() != null){
            original_gravityLayout.setVisibility(View.VISIBLE);
            original_gravity.setText(" " + beer.getOriginal_gravity());
        }

        LinearLayout final_gravityLayout = (LinearLayout)findViewById(R.id.final_gravity_layout);
        TextView final_gravity = (TextView)findViewById(R.id.final_gravity_value);
        if(beer.getFinal_gravity() != null){
            final_gravityLayout.setVisibility(View.VISIBLE);
            final_gravity.setText(" " + beer.getFinal_gravity());
        }

        LinearLayout color_srmLayout = (LinearLayout)findViewById(R.id.color_srm_layout);
        TextView color_srm = (TextView)findViewById(R.id.color_srm_value);
        if(beer.getColor_srm() != null){
            color_srmLayout.setVisibility(View.VISIBLE);
            color_srm.setText(" " + beer.getColor_srm());
        }
    }
}
