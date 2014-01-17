package com.ar.classes;

import android.graphics.Bitmap;
import java.io.Serializable;

/**
 * Created by ariviere on 06/01/2014.
 */

public class Beer implements Serializable{
    static final long serialVersionUID =4209360273818925922L;
    private String id;
    private String name;
    private String beer_style;
    private String country;
    private String from_region;
    private String first_bewed;
    private String ibu_scale;
    private String original_gravity;
    private String final_gravity;
    private String color_srm;
    private String alcohol_content;
    private String containers;
//    private Bitmap image;
    private Boolean thumbup;
    private String description;

    public Beer() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeer_style() {
        return beer_style;
    }

    public void setBeer_style(String beer_style) {
        this.beer_style = beer_style;
    }

    public String getContainers() {
        return containers;
    }

    public void setContainers(String containers) {
        this.containers = containers;
    }

    public String getFinal_gravity() {
        return final_gravity;
    }

    public void setFinal_gravity(String final_gravity) {
        this.final_gravity = final_gravity;
    }

    public String getIbu_scale() {
        return ibu_scale;
    }

    public void setIbu_scale(String ibu_scale) {
        this.ibu_scale = ibu_scale;
    }

    public String getOriginal_gravity() {
        return original_gravity;
    }

    public void setOriginal_gravity(String original_gravity) {
        this.original_gravity = original_gravity;
    }

    public String getColor_srm() {
        return color_srm;
    }

    public void setColor_srm(String color_srm) {
        this.color_srm = color_srm;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFrom_region() {
        return from_region;
    }

    public void setFrom_region(String from_region) {
        this.from_region = from_region;
    }

    public String getFirst_bewed() {
        return first_bewed;
    }

    public void setFirst_bewed(String first_bewed) {
        this.first_bewed = first_bewed;
    }

    public String getAlcohol_content() {
        return alcohol_content;
    }

    public void setAlcohol_content(String alcohol_content) {
        this.alcohol_content = alcohol_content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getThumbup() {
        return thumbup;
    }

    public void setThumbup(Boolean thumbup) {
        this.thumbup = thumbup;
    }
}
