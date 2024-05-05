package com.example.jparty.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.example.jparty.R;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsViewHolder>{
    // Lista de elementos recomendados y fragmento que contiene el RecyclerView
    private List<EventsData> dataset;
    private Fragment fragment;

    // Constructor del adaptador
    public EventsAdapter(List<EventsData> dataSet, Fragment fragment){
        this.dataset=dataSet;
        this.fragment=fragment;
    }

    // Método para crear un nuevo ViewHolder
    @NonNull
    @Override
    public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        // Inflar la vista de la celda del RecyclerView
        View eventsView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_cell, parent, false);
        // Crear y retornar un nuevo ViewHolder
        return new EventsViewHolder(eventsView, this.dataset, this);
    }

    // Método para vincular los datos con el ViewHolder
    @Override
    public void onBindViewHolder(@NonNull EventsViewHolder holder, int position){
        // Obtener los datos para esta celda
        EventsData dataForThisCell = dataset.get(position);
        String link = dataForThisCell.getLink();
        String secretkey = dataForThisCell.getSecretKey();
        // Mostrar los datos en el ViewHolder o no
        if (secretkey != null) {
            holder.key_button.setVisibility(View.VISIBLE);
        } else {
            holder.key_button.setVisibility(View.GONE);
        }
        if (link != null) {
            holder.link_button.setVisibility(View.VISIBLE);
        } else {
            holder.link_button.setVisibility(View.GONE);
        }
        holder.showData(dataForThisCell);
    }

    // Método para obtener el número de elementos en el dataset
    @Override
    public int getItemCount(){ return dataset.size(); }
}