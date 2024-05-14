package com.example.jparty.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;

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
    private ProgressBar pb1;

    private Context mainActivityContext;

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
        applyFilterButton = view.findViewById(R.id.apply_filter_button);
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
                        Server.name + "/events",
                        null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                pb1.setVisibility(View.GONE);
                                for(int i=0; i<response.length(); i++) {
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
        this.requestQueue.add(request);
        filter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterLayout.setVisibility(View.VISIBLE);
            }
        });

        applyFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí puedes enviar la petición con los filtros seleccionados
                // ...

                // Hacer el layout del filtro invisible
                filterLayout.setVisibility(View.GONE);
            }
        });

        // Establecer un TouchListener en la vista raíz
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Verificar si el filterLayout es visible
                if (filterLayout.getVisibility() == View.VISIBLE) {
                    // Obtener las coordenadas del clic
                    float x = event.getX();
                    float y = event.getY();

                    // Obtener la ubicación y las dimensiones del filterLayout
                    int[] location = new int[2];
                    filterLayout.getLocationOnScreen(location);
                    int left = location[0];
                    int top = location[1];
                    int right = left + filterLayout.getWidth();
                    int bottom = top + filterLayout.getHeight();

                    // Verificar si el clic fue fuera del filterLayout
                    if (x < left || x > right || y < top || y > bottom) {
                        // Si el clic fue fuera del filterLayout, hacerlo invisible
                        filterLayout.setVisibility(View.GONE);
                        return true;  // Consumir el evento de toque
                    }
                }

                // Si el filterLayout no es visible o el clic fue dentro del filterLayout, no consumir el evento de toque
                return false;
            }
        });
    }
    private void showFilterDialog() {
        // Inflar el layout del filtro
        LayoutInflater inflater = getLayoutInflater();
        View filterView = inflater.inflate(R.layout.filter_layout, null);

        // Calcular el ancho y alto para el PopupWindow
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.5);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.3);

        // Crear el PopupWindow
        PopupWindow filterPopup = new PopupWindow(filterView, width, height, true);

        // Configurar los elementos del PopupWindow
        CheckBox checkBox = filterView.findViewById(R.id.checkBox);
        Spinner orderSpinner = filterView.findViewById(R.id.order_spinner);
        LinearLayout applyFilterButton = filterView.findViewById(R.id.apply_filter_button);

        // Configurar el botón de aplicar filtro
        applyFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí puedes enviar la petición con los filtros seleccionados
                // ...

                // Cerrar el PopupWindow
                filterPopup.dismiss();
            }
        });

        // Mostrar el PopupWindow justo encima del botón de filtro
        filterPopup.showAsDropDown(filter_button, 0, -height);
    }
}