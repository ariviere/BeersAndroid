package com.ar.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ar.beerkipedia.R;
import com.ar.classes.Beer;

import java.util.ArrayList;

/**
 * Created by ariviere on 06/01/2014.
 */
public class BeerAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<Beer> beers;
    private static LayoutInflater inflater = null;
    private ViewHolder holder;

    public BeerAdapter(Context context, ArrayList<Beer> beers){
        this.context = context;
        this.beers = beers;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount(){
        return beers.size();
    }

    public Object getItem(int position){
        return null;
    }

    public long getItemId(int position){
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = inflater.inflate(R.layout.grid_content, null);
            holder = new ViewHolder();
            holder.name = (TextView)convertView.findViewById(R.id.name);
            holder.type = (TextView)convertView.findViewById(R.id.type);
            holder.thumb = (ImageView)convertView.findViewById(R.id.smile);
            convertView.setTag(holder);
        }else
            holder = (ViewHolder)convertView.getTag();

        holder.name.setText(beers.get(position).getName());
        Typeface type = Typeface.createFromAsset(context.getAssets(), "fonts/quando.ttf");
        holder.name.setTypeface(type);

        if(!beers.get(position).getBeer_style().equals("[]")){
            String beerStyles = beers.get(position).getBeer_style();
            beerStyles = beerStyles.replace("[", "");
            beerStyles = beerStyles.replace("]", "");
            beerStyles = beerStyles.replace("\"", "");
            String[] beerStylesArray = beerStyles.split(",");
            holder.type.setText(beerStylesArray[0]);
        }else
            holder.type.setVisibility(View.GONE);

        RelativeLayout gridButton = (RelativeLayout) convertView.findViewById(R.id.layout_grid_button);

        if(beers.get(position).getThumbup() != null){
            if(beers.get(position).getThumbup())
                holder.thumb.setImageDrawable(context.getResources().getDrawable(R.drawable.smile));
            else
                holder.thumb.setImageDrawable(context.getResources().getDrawable(R.drawable.sad));

            holder.thumb.setVisibility(View.VISIBLE);
        }

        if(position%4 == 0)
            gridButton.setBackground(context.getResources().getDrawable(R.drawable.grid_button_1));
        else if(position%4 == 1)
            gridButton.setBackground(context.getResources().getDrawable(R.drawable.grid_button_2));
        else if(position%4 == 2)
            gridButton.setBackground(context.getResources().getDrawable(R.drawable.grid_button_3));
        else if(position%4 == 3)
            gridButton.setBackground(context.getResources().getDrawable(R.drawable.grid_button_4));

        return convertView;
    }

    public static class ViewHolder {
        public TextView name;
        public TextView type;
        public ImageView thumb;
    }
}
