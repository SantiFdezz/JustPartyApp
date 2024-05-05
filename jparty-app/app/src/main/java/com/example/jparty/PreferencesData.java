package com.example.jparty;

import org.json.JSONException;
import org.json.JSONObject;

public class PreferencesData {

    // Variables para almacenar los datos de cada elemento
    private String place_name;
    private Integer musicId;
    private Boolean musicSelected;
    private String image_url;

    // Métodos getter para cada variable
    public String getPlace_Name() { return place_name; }
    public Integer getMusicId() { return musicId; }
    public Boolean getMusicSelected() { return musicSelected; }
    public String getImage_url() { return image_url; }

    // Constructor que toma los datos como parámetros
    public PreferencesData(String place_name, Integer musicId, Boolean musicSelected, String image_url){

        this.place_name=place_name;
        this.musicId=musicId;
        this.musicSelected=musicSelected;
        this.image_url=image_url;
    }

    // Constructor que toma un objeto JSON y extrae los datos
    public PreferencesData(JSONObject json){
        try{
            this.place_name = json.getString("name");
            this.musicId = json.getInt("id");
            this.image_url = json.getString("image");
            this.musicSelected = json.getBoolean("selected");
        }catch (JSONException e){ e.printStackTrace(); }
    }
}