package com.example.jparty.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AssistancesData {

    // Variables para almacenar los datos de cada elemento
    private String title;
    private int eventId;
    private String street;
    private String price;
    private int assistants;
    private String date;
    private String secretkey;
    private String link;
    private String hour;
    private Boolean userLiked;

    // Métodos getter para cada variable
    public String getTitle() { return title; }
    public String getStreet() { return street; }
    public String getPrice() { return price; }
    public int getAssistants() { return assistants; }
    public String getDate() { return date; }
    public String getHour() { return hour; }
    public String getSecretKey() { return secretkey; }
    public String getLink() { return link; }
    public int getEventId() { return eventId; }
    public void setUserLiked(boolean userLiked) { this.userLiked= userLiked; }
    public boolean getUserLiked() { return userLiked; }

    // Constructor que toma los datos como parámetros
    public AssistancesData(int eventId, String title, String link, String street, String price, int assistants, String hour ,String date, String secretkey, boolean userLiked){
        this.title = title;
        this.eventId = eventId;
        this.street = street;
        this.price = price;
        this.assistants = assistants;
        this.date = date;
        this.hour = hour;
        this.secretkey = secretkey;
        this.userLiked = userLiked;
        this.link = link;
    }

    // Constructor que toma un objeto JSON y extrae los datos
    public AssistancesData(JSONObject json){
        try{
            this.eventId = json.getInt("id");
            this.title = json.getString("title");
            this.street = json.getString("street");
            this.price = json.getString("price");
            this.assistants = json.getInt("assistants");
            this.link = json.getString("link");
            //recogemos la fechay la dividimos en fecha y hora
            String dateStr = json.getString("date");
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US);
            Date fullDate = format.parse(dateStr);
            this.hour = new SimpleDateFormat("HH:mm", Locale.US).format(fullDate);
            this.date = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(fullDate);
            this.secretkey = json.getString("secretkey");
            this.userLiked = json.getBoolean("userLiked");
        }catch (JSONException | ParseException e){ e.printStackTrace(); }
    }
}
