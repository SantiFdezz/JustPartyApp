package com.example.jparty;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PreferencesAdapter extends RecyclerView.Adapter<PreferencesViewHolder> {
    // Lista de elementos recomendados y fragmento que contiene el RecyclerView
    private List<PreferencesData> dataset;



    // Constructor del adaptador
    public PreferencesAdapter(List<PreferencesData> dataSet) {
        this.dataset = dataSet;
    }

    // Método para obtener los IDs de los checkbox seleccionados
    public JSONObject getCheckedIds() {
        JSONArray checkedIds = new JSONArray();
        for (PreferencesData data : dataset) {
            if (data.getMusicSelected()) {
                checkedIds.put(data.getMusicId());
            }
        }
        JSONObject result = new JSONObject();
        try {
            result.put("selected", checkedIds);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }


    public void onItemClicked(int position, boolean isChecked) {
        PreferencesData currentItem = dataset.get(position);
        currentItem.setMusicSelected(isChecked);
    }

    // Método para crear un nuevo ViewHolder
    @NonNull
    @Override
    public PreferencesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar la vista de la celda del RecyclerView
        View preferencesView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_cell_music, parent, false);
        // Crear y retornar un nuevo ViewHolder
        return new PreferencesViewHolder(preferencesView, this);
    }

    // Método para vincular los datos con el ViewHolder
    @Override
    public void onBindViewHolder(@NonNull PreferencesViewHolder holder, int position) {
        // Obtener los datos para esta celda
        PreferencesData dataForThisCell = dataset.get(position);
        // Mostrar los datos en el ViewHolder
        holder.showData(dataForThisCell);
    }

    // Método para obtener el número de elementos en el dataset
    @Override
    public int getItemCount() {
        return dataset.size();
    }
}