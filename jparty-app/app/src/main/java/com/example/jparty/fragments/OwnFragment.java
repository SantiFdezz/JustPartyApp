package com.example.jparty.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.jparty.EditEventActivity;
import com.example.jparty.JsonArrayRequestWithAuthentication;
import com.example.jparty.R;
import com.example.jparty.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class OwnFragment extends Fragment {

    private RecyclerView recyclerView;
    private OwnAdapter adapter;
    private Context context;
    private List<OwnData> eventsList;
    private RequestQueue requestQueue;
    private ProgressBar pb1;
    private ImageButton circle_button;


    // Método que se llama para crear la vista del fragmento
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_recycler_own, container, false);
        eventsList = new ArrayList<>();
        pb1 = view.findViewById(R.id.loadingScreen);
        circle_button = view.findViewById(R.id.add_button);
        adapter = new OwnAdapter(eventsList, this, getActivity());
        recyclerView = view.findViewById(R.id.recycler_view_item);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
                        Server.name + "/events?mine=true",
                        null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                pb1.setVisibility(View.GONE);
                                for (int i = 0; i < response.length(); i++) {
                                    try {
                                        JSONObject events = response.getJSONObject(i);
                                        OwnData u_event = new OwnData(events);
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

        circle_button.setOnClickListener(v -> {
            // Código para enviar la solicitud de creación de evento
            Intent intent = new Intent(getContext(), EditEventActivity.class);
            intent.putExtra("event_id", -1);
            startActivityForResult(intent, 1);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) { // El mismo código de solicitud que pasaste a startActivityForResult()
            if (resultCode == Activity.RESULT_OK) {
                // Aquí puedes refrescar tus datos
                // Por ejemplo, si estás usando un RecyclerView, puedes llamar a adapter.notifyDataSetChanged()
                // Pero en tu caso, necesitas volver a cargar los datos desde el servidor
                // Puedes llamar a la función que carga los datos en el RecyclerView aquí
                adapter.notifyDataSetChanged();
            }
        }
    }

}