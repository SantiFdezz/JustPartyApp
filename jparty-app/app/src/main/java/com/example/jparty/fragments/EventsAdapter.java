package com.example.jparty.fragments;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jparty.DetailActivity;
import com.example.jparty.R;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsViewHolder> {
    // Lista de elementos recomendados y fragmento que contiene el RecyclerView
    private List<EventsData> dataset;
    private Fragment fragment;
    private Context context;

    // Constructor del adaptador
    public EventsAdapter(List<EventsData> dataSet, Fragment fragment, Context context) {
        this.dataset = dataSet;
        this.fragment = fragment;
        this.context = context;
    }

    // Método para crear un nuevo ViewHolder
    @NonNull
    @Override
    public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar la vista de la celda del RecyclerView
        View eventsView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_cell, parent, false);
        // Crear y retornar un nuevo ViewHolder
        return new EventsViewHolder(eventsView, this.dataset, this);
    }

    // Método para vincular los datos con el ViewHolder
    @Override
    public void onBindViewHolder(@NonNull EventsViewHolder holder, int position) {
        // Obtener los datos para esta celda
        EventsData dataForThisCell = dataset.get(position);
        String link = dataForThisCell.getLink();
        String secretkey = dataForThisCell.getSecretKey();
        // Mostrar los datos en el ViewHolder o no
        if (secretkey != "null") {
            holder.key_button.setVisibility(View.VISIBLE);
        } else {
            holder.key_button.setVisibility(View.GONE);
        }
        if (link != "null") {
            holder.link_button.setVisibility(View.VISIBLE);
        } else {
            holder.link_button.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("event_id", dataForThisCell.getEvent_Id().intValue());
                context.startActivity(intent);
            }
        });
        holder.showData(dataForThisCell);
    }

    // Método para obtener el número de elementos en el dataset
    @Override
    public int getItemCount() {
        return dataset.size();
    }
}