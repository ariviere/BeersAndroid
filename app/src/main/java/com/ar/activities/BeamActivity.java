package com.ar.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ar.beerkipedia.R;
import com.ar.classes.Beer;
import com.ar.classes.BeersManipulation;

import java.util.ArrayList;

/**
 * Created by ariviere on 12/01/2014.
 */
public class BeamActivity extends Activity implements NfcAdapter.CreateNdefMessageCallback {

    private static String TAG = "BEAM";
    NfcAdapter mNfcAdapter;
    TextView scoreText;
    ImageView scoreImg;
    Context context;
    BeersManipulation bm = new BeersManipulation();

    int score;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beam);

        this.context = this;
        scoreText = (TextView) findViewById(R.id.beam_text);
        scoreImg = (ImageView) findViewById(R.id.beam_image);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(mNfcAdapter == null){
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        mNfcAdapter.setNdefPushMessageCallback(this, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("score", score);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null){
            score = savedInstanceState.getInt("score");
            modifyUIwithScore();
        }
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        ArrayList<Beer> beers = new BeersManipulation().getBeersFromFile(context);
        byte[] beersBytes = bm.beersArrayListToBytes(beers);

        NdefMessage msg = new NdefMessage(new NdefRecord[] { NdefRecord.createMime("application/com.ar.activities.beamactivity", beersBytes)});

        return msg;
    }

    @Override
    public void onResume(){
        super.onResume();

        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())){
            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    void processIntent(Intent intent){
        scoreText = (TextView) findViewById(R.id.beam_text);
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        NdefMessage msg = (NdefMessage) rawMsgs[0];
        ArrayList<Beer> beers = bm.bytesToBeersArrayList(msg.getRecords()[0].getPayload());

        score = new BeersManipulation().calculateBeerKarma(context, beers);

        modifyUIwithScore();
    }

    private void modifyUIwithScore(){
        if(score <= 50){
            scoreImg.setImageDrawable(getResources().getDrawable(R.drawable.red_alert));
            scoreText.setTextColor(getResources().getColor(R.color.red));
        }else{
            scoreImg.setImageDrawable(getResources().getDrawable(R.drawable.green_alert));
            scoreText.setTextColor(getResources().getColor(R.color.green));
        }

        scoreText.setText(score + "%");
    }
}
