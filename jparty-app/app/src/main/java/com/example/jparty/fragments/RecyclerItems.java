package com.example.jparty.fragments;

import org.json.JSONException;
import org.json.JSONObject;

public class RecyclerItems {
    private String title;
    private String street;
    private String province;
    private String music_genre;
    private String secretkey;
    private String link;
    private String date;
    private String image;
    private String description;

    // Métodos getter para cada variable
    public String getTitle() {
        return title;
    }

    public String getStreet() {
        return street;
    }

    public String getProvince() {
        return province;
    }

    public String getMusic_genre() {
        return music_genre;
    }

    public String getSecretkey() {
        return secretkey;
    }

    public String getDate() {
        return date;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }


    // Constructor que toma los datos como parámetros
    public RecyclerItems(String place_name, String description, String location, String tag, String image_url) {
        this.title = title;
        this.street = street;
        this.province = province;
        this.music_genre = music_genre;
        this.secretkey = secretkey;
        this.date = date;
        this.image = image;
        this.description = description;

    }

    // Constructor que toma un objeto JSON y extrae los datos
    public RecyclerItems(JSONObject json) {
        try {
            this.title = json.getString("title");
            this.street = json.getString("street");
            this.province = json.getString("province");
            this.music_genre = json.getString("music_genre");
            this.secretkey = json.getString("secretkey");
            this.date = json.getString("date");
            this.image = json.getString("image");
            this.description = json.getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
