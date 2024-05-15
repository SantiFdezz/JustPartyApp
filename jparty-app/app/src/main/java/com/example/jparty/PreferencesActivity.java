package com.example.jparty;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PreferencesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PreferencesAdapter adapter;
    private List<PreferencesData> preferencesList;
    private RequestQueue requestQueue;
    private ProgressBar pb1;
    private ImageView checkbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        preferencesList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view_item);
        adapter = new PreferencesAdapter(preferencesList, this, recyclerView);

        pb1 = findViewById(R.id.loadingScreen);
        checkbutton = findViewById(R.id.check_button);
        recyclerView.setAdapter(adapter);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        pb1.setVisibility(View.VISIBLE);

        this.requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequestWithAuthentication request = new JsonArrayRequestWithAuthentication
                (Request.Method.GET,
                        Server.name + "/user/preferences",
                        null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                pb1.setVisibility(View.GONE);
                                for (int i = 0; i < response.length(); i++) {
                                    try {
                                        JSONObject preferences = response.getJSONObject(i);
                                        PreferencesData preference = new PreferencesData(preferences);
                                        preferencesList.add(preference);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pb1.setVisibility(View.GONE);
                        error.printStackTrace();
                    }
                }, this);
        this.requestQueue.add(request);

        checkbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb1.setVisibility(View.VISIBLE);
                JSONObject checkedIds = adapter.getCheckedIds();
                JsonObjectRequestWithAuthentication request = new JsonObjectRequestWithAuthentication
                        (Request.Method.PUT,
                                Server.name + "/user/preferences",
                                checkedIds,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        pb1.setVisibility(View.GONE);
                                        if (MainActivity.isRunning) {
                                            finish();
                                        } else {
                                            Intent intent = new Intent(PreferencesActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        finish();
                                    }
                                }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                pb1.setVisibility(View.GONE);
                                Toast.makeText(PreferencesActivity.this, "Error al guardar las preferencias", Toast.LENGTH_SHORT).show();
                                error.printStackTrace();
                            }
                        }, PreferencesActivity.this);
                requestQueue.add(request);
            }
        });
    }
}
