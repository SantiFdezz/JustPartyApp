package com.example.jparty.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.jparty.DetailActivity;
import com.example.jparty.JsonObjectRequestWithAuthentication;
import com.example.jparty.R;
import com.example.jparty.Server;

import org.json.JSONObject;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsViewHolder> {
    // Lista de elementos recomendados y fragmento que contiene el RecyclerView
    private List<EventsData> dataset;
    private Fragment fragment;
    private RequestQueue requestQueue;

    // Constructor del adaptador
    public EventsAdapter(List<EventsData> dataSet, Fragment fragment) {
        this.dataset = dataSet;
        this.fragment = fragment;

    }

    // Método para crear un nuevo ViewHolder
    @NonNull
    @Override
    public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar la vista de la celda del RecyclerView
        View eventsView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_cell, parent, false);
        // Crear y retornar un nuevo ViewHolder
        return new EventsViewHolder(eventsView);
    }

    // Método para vincular los datos con el ViewHolder
    @Override
    public void onBindViewHolder(@NonNull EventsViewHolder holder, int position) {
        // Obtener los datos para esta celda
        this.requestQueue = Volley.newRequestQueue(fragment.getContext());
        EventsData dataForThisCell = dataset.get(position);
        String link = dataForThisCell.getLink();
        String secretkey = dataForThisCell.getSecretKey();
        // Mostrar los datos en el ViewHolder o no
        if (secretkey.equals("null")) {
            holder.key_button.setVisibility(View.GONE);
        } else {
            holder.key_button.setVisibility(View.VISIBLE);
        }
        if (link.equals("null")) {
            holder.link_button.setVisibility(View.GONE);
        } else {
            holder.link_button.setVisibility(View.VISIBLE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fragment.getContext(), DetailActivity.class);
                intent.putExtra("event_id", dataForThisCell.getEvent_Id().intValue());
                fragment.getContext().startActivity(intent);
            }
        });
        holder.assist_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EventsData currentItem = dataset.get(holder.getAdapterPosition());
                boolean isAssisted = currentItem.getUserAssist();
                int method = isAssisted ? Request.Method.DELETE : Request.Method.POST;
                JsonObjectRequestWithAuthentication request = new JsonObjectRequestWithAuthentication(
                        method, "/user/assistevent/" + currentItem.getEvent_Id(), null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                currentItem.setUserAssist(!isAssisted);
                                currentItem.setAssistances(!isAssisted ? (currentItem.getAssistances() + 1) : (currentItem.getAssistances() - 1));
                                holder.assist_icon.setImageResource(isAssisted ? R.drawable.balloon_selected : R.drawable.balloon_unselected);
                                holder.assist_count.setText(String.valueOf(currentItem.getAssistances()));
                                notifyItemChanged(holder.getAdapterPosition()); // Notificar al adaptador para que actualice la vista
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (error.networkResponse == null) {
                                    Toast.makeText(fragment.getContext(), "La conexión no se ha establecido", Toast.LENGTH_LONG).show();
                                } else {
                                    int serverCode = error.networkResponse.statusCode;
                                    Toast.makeText(fragment.getContext(), "Estado de respuesta " + serverCode, Toast.LENGTH_LONG).show();
                                }
                                error.printStackTrace();                            }
                        },
                        fragment.getContext()
                );
               holder. requestQueue.add(request);
            }
        });
        holder.like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EventsData currentItem = dataset.get(holder.getAdapterPosition());
                boolean isLiked = currentItem.getUserLike();
                int method = isLiked ? Request.Method.DELETE : Request.Method.POST;
                JsonObjectRequestWithAuthentication request = new JsonObjectRequestWithAuthentication(
                        method, "/user/likedevent/" + currentItem.getEvent_Id(), null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                currentItem.setUserLike(!isLiked);
                                holder.like_icon.setImageResource(isLiked ? R.drawable.like_selected : R.drawable.like_unselected);
                                notifyItemChanged(holder.getAdapterPosition()); // Notificar al adaptador para que actualice la vista
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (error.networkResponse == null) {
                                    Toast.makeText(fragment.getContext(), "La conexión no se ha establecido", Toast.LENGTH_LONG).show();
                                } else {
                                    int serverCode = error.networkResponse.statusCode;
                                    Toast.makeText(fragment.getContext(), "Estado de respuesta " + serverCode, Toast.LENGTH_LONG).show();
                                }
                                error.printStackTrace();
                            }
                        },
                        fragment.getContext()
                );
                requestQueue.add(request);
            }
        });

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EventsData currentItem = dataset.get(holder.getAdapterPosition());
                String url = currentItem.getLink();
                if (url != null && !url.isEmpty()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    fragment.getContext().startActivity(browserIntent);
                }
            }
        };

        holder.link_button.setOnClickListener(clickListener);
        holder.link_icon.setOnClickListener(clickListener);

        holder.showData(dataForThisCell);
    }

    // Método para obtener el número de elementos en el dataset
    @Override
    public int getItemCount() {
        return dataset.size();
    }
}