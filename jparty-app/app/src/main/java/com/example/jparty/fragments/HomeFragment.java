package com.example.jparty.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.jparty.JsonArrayRequestWithAuthentication;
import com.example.jparty.R;
import com.example.jparty.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private RequestQueue queue;

    private ProgressBar pb1;

    private List<RecyclerItems> allTheEventsData;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_main, container, false);
        pb1 = view.findViewById(R.id.loadingScreen);
        queue = Volley.newRequestQueue(getContext());
        recyclerView = view.findViewById(R.id.recycler_view_item);

        JsonArrayRequest request = new JsonArrayRequest
                (Request.Method.GET,
                        Server.name + "/events",
                        null,
                        new Response.Listener<JSONArray>(){
                            @Override
                            public void onResponse(JSONArray response) {
                                // Se crea una lista para almacenar los datos.
                                List<RecyclerItems> allTheEvents = new ArrayList<>();

                                // Se analiza el JSON y se agrega cada oferta especial a la lista
                                for(int i=0; i<response.length(); i++) {
                                    try {
                                        JSONObject places = response.getJSONObject(i);
                                        // Aqui function que lee los parametros added y los añade al json
                                        //SpecialOffersData data = new SpecialOffersData(places);
                                        //SpecialOffersDataList.add(data);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                pb1.setVisibility(View.GONE); // Alternamos entre la visibilidad de la barra de progresión a nuestra conveniencia.
                               // adapter.notifyDataSetChanged();
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        pb1.setVisibility(View.GONE); // Alternamos entre la visibilidad de la barra de progresión a nuestra conveniencia.
                        error.printStackTrace();
                    }
                });
        queue.add(request);
        return view;
    }
}