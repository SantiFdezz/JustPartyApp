package com.example.jparty.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.jparty.JsonArrayRequestWithAuthentication;
import com.example.jparty.R;
import com.example.jparty.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AssistancesFragment extends Fragment {

    private RecyclerView recyclerView;
    private AssistancesAdapter adapter;
    private Context context;
    private List<AssistancesData> eventsList;
    private RequestQueue requestQueue;
    private ProgressBar pb1;



    // Método que se llama para crear la vista del fragmento
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_recycler_assistances, container, false);
        eventsList = new ArrayList<>();
        pb1 = view.findViewById(R.id.loadingScreen);
        adapter = new AssistancesAdapter(eventsList, this, getActivity());
        recyclerView = view.findViewById(R.id.recycler_view_item);
        recyclerView.setAdapter(adapter);
        // Activamos el snapHelper para que el recyclerView se desplace de uno en uno
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        pb1.setVisibility(View.VISIBLE);
        return view;
    }

    // Método que se llama después de que la vista del fragmento ha sido creada
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.requestQueue = Volley.newRequestQueue(getContext());
        JsonArrayRequestWithAuthentication request = new JsonArrayRequestWithAuthentication
                (Request.Method.GET,
                        Server.name + "/user/assistevents",
                        null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                pb1.setVisibility(View.GONE);
                                for(int i=0; i<response.length(); i++) {
                                    try {
                                        JSONObject events = response.getJSONObject(i);
                                        AssistancesData u_event = new AssistancesData(events);
                                        eventsList.add(u_event);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pb1.setVisibility(View.GONE); // Alternamos entre la visibilidad de la barra de progresión a nuestra conveniencia.
                        error.printStackTrace();
                    }
                }, getContext());
        this.requestQueue.add(request);
    }
}