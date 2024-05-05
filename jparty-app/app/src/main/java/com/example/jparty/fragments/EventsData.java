package com.example.jparty.fragments;

import org.json.JSONException;
import org.json.JSONObject;

public class EventsData {

    // Variables para almacenar los datos de cada elemento
    private String place_name;
    private String description;
    private String tag_name;
    private String secretkey;
    private String event_date;
    private String link;
    private Boolean userassist;
    private Boolean userlike;
    private Integer event_id;;
    private Integer assistances;
    private String image_url;

    // Métodos getter para cada variable
    public String getPlace_Name() { return place_name; }
    public Integer getEvent_Id() { return event_id; }
    public String getDescription() { return description; }
    public String getSecretKey() { return secretkey; }
    public String getLink() { return link; }
    public Boolean getUserAssist() { return userassist; }
    public Boolean getUserLike() { return userlike; }
    public String getTag_Name() { return tag_name; }
    public String getEvent_Date() { return event_date; }
    public Integer getAssistances() { return assistances; }
    public String getImage_url() { return image_url; }

    // Constructor que toma los datos como parámetros
    public EventsData(String place_name,String link,Integer event_id, String secretkey, String description, Boolean userassist, Boolean userlike, String event_date,Integer assistances, String image_url){
        this.place_name=place_name;
        this.description=description;
        this.assistances=assistances;
        this.secretkey=secretkey;
        this.link=link;
        this.event_date=event_date;
        this.image_url=image_url;
        this.userassist=userassist;
        this.userlike=userlike;
        this.event_id=event_id;
    }

    // Constructor que toma un objeto JSON y extrae los datos
    public EventsData(JSONObject json){
        try{
            this.place_name = json.getString("title");
            this.description = json.getString("description");
            this.secretkey = json.getString("secretkey");
            this.link = json.getString("link");
            this.userassist = json.getBoolean("userAssist");
            this.userlike = json.getBoolean("userLiked");
            this.tag_name = json.getString("music_genre");
            this.event_date = json.getString("date");
            this.image_url = json.getString("image");
            this.assistances = json.getInt("assistants");
            this.event_id = json.getInt("id");
        }catch (JSONException e){ e.printStackTrace(); }
    }
}