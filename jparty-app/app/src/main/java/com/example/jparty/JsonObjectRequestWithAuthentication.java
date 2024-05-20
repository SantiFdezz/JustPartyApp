package com.example.jparty;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.jparty.Server;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JsonObjectRequestWithAuthentication extends JsonObjectRequest {
    // Añade el token a headers en la petición
    private Context context;
    private String url;

    public JsonObjectRequestWithAuthentication(int method, String url, @Nullable JSONObject jsonRequest, Response.Listener<JSONObject> listener, @Nullable Response.ErrorListener errorListener, Context context) {
        super(method, Server.name + url, jsonRequest, listener, errorListener);
        this.context = context;
        this.url = Server.name + url;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        SharedPreferences preferences = context.getSharedPreferences("JPARTY_APP_PREFS", Context.MODE_PRIVATE);
        String sessionToken = preferences.getString("VALID_TOKEN", null);
        if (sessionToken == null) {
            throw new AuthFailureError();
        }
        HashMap<String, String> myHeaders = new HashMap<>();
        myHeaders.put("SessionToken", sessionToken);
        return myHeaders;
    }
}
