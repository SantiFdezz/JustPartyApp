package com.example.jparty.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

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
import com.example.jparty.JsonArrayRequestWithAuthentication;
import com.example.jparty.R;
import com.example.jparty.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    // Variables para la lista de recomendaciones, el adaptador y la cola de solicitudes
    private RecyclerView recyclerView;
    private EventsAdapter adapter;
    private Context context;
    private List<EventsData> eventsList;
    private RequestQueue requestQueue;
    private ImageView filter_button;
    private View filterLayout;
    private LinearLayout applyFilterButton;
    private Spinner provinceSpinner;
    private CheckBox priceCheckBox, dateCheckBox;
    private ProgressBar pb1;
    private String params;

    // Método que se llama para crear la vista del fragmento
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_recycler_main, container, false);
        eventsList = new ArrayList<>();
        pb1 = view.findViewById(R.id.loadingScreen);
        adapter = new EventsAdapter(eventsList, this, getActivity());
        recyclerView = view.findViewById(R.id.recycler_view_item);
        filter_button = view.findViewById(R.id.filter_button);
        filterLayout = view.findViewById(R.id.filter_layout);
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
        getEvents("");
        filter_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });
    }

    private void getEvents(String params) {
        // Construir la URL de la solicitud
        String url = Server.name + "/events";
        if (!params.isEmpty()) {
            url += params;
        }
        JsonArrayRequestWithAuthentication request = new JsonArrayRequestWithAuthentication
                (Request.Method.GET,
                        url,
                        null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                pb1.setVisibility(View.GONE);
                                eventsList.clear();
                                for (int i = 0; i < response.length(); i++) {
                                    try {
                                        JSONObject events = response.getJSONObject(i);
                                        EventsData u_event = new EventsData(events);
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
        requestQueue.add(request);
    }

    private void showFilterDialog() {
        // Inflar el layout del filtro
        LayoutInflater inflater = getLayoutInflater();
        View filterView = inflater.inflate(R.layout.filter_layout, null);

        // Configurar los elementos del filtro
        provinceSpinner = filterView.findViewById(R.id.provinceSpinner);
        priceCheckBox = filterView.findViewById(R.id.priceCheckBox);
        dateCheckBox = filterView.findViewById(R.id.dateCheckBox);
        applyFilterButton = filterView.findViewById(R.id.apply_filter_button);

        String selectedProvince = provinceSpinner.getSelectedItem().toString();
        boolean isPriceChecked = priceCheckBox.isChecked();
        boolean isDateChecked = dateCheckBox.isChecked();
        // Crear el AlertDialog
        AlertDialog filterDialog = new AlertDialog.Builder(getContext())
                .setView(filterView)
                .create();
        priceCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Si priceCheckBox está marcado, deshabilitar dateCheckBox
                if (isChecked) {
                    dateCheckBox.setEnabled(false);
                } else {
                    dateCheckBox.setEnabled(true);
                }
            }
        });

        dateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Si dateCheckBox está marcado, deshabilitar priceCheckBox
                if (isChecked) {
                    priceCheckBox.setEnabled(false);
                } else {
                    priceCheckBox.setEnabled(true);
                }
            }
        });
        // Configurar el botón de aplicar filtro
        applyFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Construir los parámetros de la URL
                StringBuilder params = new StringBuilder("?");
                String selectedProvince = provinceSpinner.getSelectedItem().toString();
                boolean isPriceChecked = priceCheckBox.isChecked();
                boolean isDateChecked = dateCheckBox.isChecked();

                if (!selectedProvince.equals("0") && !selectedProvince.equals("Seleccione provincia")) {
                    params.append("province=").append(selectedProvince);
                }

                if (isPriceChecked) {
                    if (params.length() > 1) {
                        params.append("&");
                    }
                    params.append("order_by=price");
                }

                if (isDateChecked) {
                    if (params.length() > 1) {
                        params.append("&");
                    }
                    params.append("order_by=date");
                }

                // Si no se seleccionó ningún filtro, establecer params como null
                if (params.length() == 1) {
                    params = new StringBuilder();
                }

                // Llamar a getEvents con los parámetros
                getEvents(params.toString());
                filterDialog.dismiss();
            }
        });

        // Mostrar el AlertDialog
        filterDialog.show();
    }
}