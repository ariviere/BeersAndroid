package com.ar.classes;

import android.content.Context;

import com.ar.classes.Beer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by ariviere on 12/01/2014.
 */
public class BeersManipulation {
    public ArrayList<Beer> getBeersFromFile(Context context){
        try{
            try{
                FileInputStream fis = context.openFileInput("Beers5");
                ObjectInputStream ois = new ObjectInputStream(fis);
                return (ArrayList<Beer>) ois.readObject();
            }
            catch(FileNotFoundException e){
                return new ArrayList<Beer>();
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Boolean saveBeersInFile(Context context, ArrayList<Beer> beers){
        try{
            FileOutputStream fos = context.openFileOutput("Beers5", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(beers);
            oos.close();
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public int calculateBeerKarma(Context context, ArrayList<Beer> friendBeers){
        int score = 50;

        ArrayList<Beer> myBeers = getBeersFromFile(context);

        for(Beer friendBeer : friendBeers){
            for(Beer myBeer : myBeers){
                if(friendBeer.getName().equals(myBeer.getName())){
                    if(friendBeer.getThumbup() != null && myBeer.getThumbup() != null){
                        if((friendBeer.getThumbup() && myBeer.getThumbup()) || (!friendBeer.getThumbup() && !myBeer.getThumbup()))
                            score+=10;
                        else if((!friendBeer.getThumbup() && myBeer.getThumbup()) || (friendBeer.getThumbup() && !myBeer.getThumbup()))
                            score-=10;
                    }
                }
            }
        }

        return score;

    }

    public byte[] beersArrayListToBytes(ArrayList<Beer> beers){
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            ObjectOutputStream o = new ObjectOutputStream(b);
            o.writeObject(beers);
            return b.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Beer> bytesToBeersArrayList(byte[] bytes){
        try {
            ByteArrayInputStream b = new ByteArrayInputStream(bytes);
            ObjectInputStream o = new ObjectInputStream(b);
            return (ArrayList<Beer>)o.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
