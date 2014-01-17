package com.ar.classes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.ar.classes.Beer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by ariviere on 06/01/2014.
 */
public class BeersJSON {
    private static String TAG = "BeersJSON";
    private static String API_KEY = "AIzaSyBHUrn2uOlBBOtnfsF7V4kHSa6BnPmQjUg";
    private ArrayList<String> pagesCursors = new ArrayList<String>();

    public ArrayList<Beer> getBeers(String url, String page){
        try{
            Boolean goFindBeers = false;

            InputStream is;
            String result;
            Log.d(TAG, "testFreebase");
            HttpClient httpclient = new DefaultHttpClient();

            if(page.isEmpty() || page.equals("first")){
                goFindBeers = true;
                pagesCursors.add("");
            }

            if(page.equals("next") && !pagesCursors.get(pagesCursors.size()-1).equals("FALSE")){
                goFindBeers = true;
                Log.d(TAG, "next cursor: " + pagesCursors.get(pagesCursors.size()-1));
                url += pagesCursors.get(pagesCursors.size()-1);
            }
            else if(page.equals("previous") && pagesCursors.size() >= 2){
                goFindBeers = true;
                url += pagesCursors.get(pagesCursors.size()-3);
                Log.d(TAG, "previous cursor: " + pagesCursors.get(pagesCursors.size()-3));
            }

            if(goFindBeers){
                HttpGet httpget = new HttpGet(url);
                httpget.setHeader("Accept", "application/json");
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                result=sb.toString();

                JSONObject jsonObjectdatas = new JSONObject(result);

                if(page.equals("next") || page.equals("first")){
                    pagesCursors.add(jsonObjectdatas.getString("cursor"));
                }else if(page.equals("previous")){
                    pagesCursors.remove(pagesCursors.size()-1);
                }

                JSONArray jsonResults = (JSONArray)jsonObjectdatas.get("result");

                ArrayList<Beer> beers = new ArrayList<Beer>();
                for(int i = 0 ; i < jsonResults.length() ; i++){
                    Beer beer = new Beer();
                    JSONObject currentBeerObject = (JSONObject)jsonResults.get(i);
                    if(!currentBeerObject.isNull("id"))
                        beer.setId(currentBeerObject.getString("id"));
                    if(!currentBeerObject.isNull("name"))
                        beer.setName(currentBeerObject.getString("name"));
                    if(!currentBeerObject.isNull("beer_style"))
                        beer.setBeer_style(currentBeerObject.getString("beer_style"));
                    if(!currentBeerObject.isNull("country"))
                        beer.setCountry(currentBeerObject.getString("country"));
                    if(!currentBeerObject.isNull("from_region"))
                        beer.setFrom_region(currentBeerObject.getString("from_region"));
                    if(!currentBeerObject.isNull("first_bewed"))
                        beer.setFirst_bewed(currentBeerObject.getString("first_bewed"));
                    if(!currentBeerObject.isNull("ibu_scale"))
                        beer.setIbu_scale(currentBeerObject.getString("ibu_scale"));
                    if(!currentBeerObject.isNull("original_gravity"))
                        beer.setOriginal_gravity(currentBeerObject.getString("original_gravity"));
                    if(!currentBeerObject.isNull("final_gravity"))
                        beer.setFinal_gravity(currentBeerObject.getString("final_gravity"));
                    if(!currentBeerObject.isNull("color_srm"))
                        beer.setColor_srm(currentBeerObject.getString("color_srm"));
                    if(!currentBeerObject.isNull("alcohol_content"))
                        beer.setAlcohol_content(currentBeerObject.getString("alcohol_content"));
                    if(!currentBeerObject.isNull("containers"))
                        beer.setContainers(currentBeerObject.getString("containers"));

                    beers.add(beer);
                }

                return beers;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap getBeerImg(String id){
        String url = "https://www.googleapis.com/freebase/v1/image" + id + "?maxwidth=225&maxheight=225&mode=fillcropmid";
        Log.d(TAG, "image url: " + url);
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Bitmap image = BitmapFactory.decodeStream(is);
            Log.d(TAG, "image: " + image.toString());
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getBeerDescription(String id){
//        String url = "https://www.googleapis.com/freebase/v1/text" + beer.getId();
        String formattedName = id.replace(" ", "_");
        String  url = "http://en.wikipedia.org/w/api.php?action=query&prop=extracts&titles=" + formattedName + "&format=json";

        try{
            InputStream is;
            String result;
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(url);
            httpget.setHeader("Accept", "application/json");
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result=sb.toString();

            JSONObject jsonObjectdatas = new JSONObject(result);

            jsonObjectdatas = (JSONObject)jsonObjectdatas.get("query");
            Log.d(TAG, "pages to string: " + jsonObjectdatas.get("pages").toString());

            jsonObjectdatas = (JSONObject)jsonObjectdatas.get("pages");
            String keyId = null;

            Iterator keys = jsonObjectdatas.keys();
            while(keys.hasNext()) {
                keyId = (String) keys.next();
            }

            jsonObjectdatas = (JSONObject)jsonObjectdatas.get(keyId);
            if(!jsonObjectdatas.isNull("extract")){
                String description = jsonObjectdatas.getString("extract");
                if(description.startsWith("<ol><li>REDIRECT")){
                    String redirectBeer = description.substring(17);
                    String[] array = redirectBeer.split("<");
                    id = array[0];
                    return getBeerDescription(id);
                }
                else{
                    return jsonObjectdatas.getString("extract");
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
