package com.example.jparty.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class AssistancesAdapter extends RecyclerView.Adapter<AssistancesViewHolder> {
    // Lista de elementos recomendados y fragmento que contiene el RecyclerView
    private List<AssistancesData> dataset;
    private Fragment fragment;
    private Context context;
    private RequestQueue requestQueue;

    // Constructor del adaptador
    public AssistancesAdapter(List<AssistancesData> dataSet, Fragment fragment, Context context) {
        this.dataset = dataSet;
        this.fragment = fragment;
        this.context = context;
    }

    // Método para crear un nuevo ViewHolder
    @NonNull
    @Override
    public AssistancesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar la vista de la celda del RecyclerView
        View eventsView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_assistanceevents, parent, false);
        // Crear y retornar un nuevo ViewHolder
        return new AssistancesViewHolder(eventsView);
    }

    @Override
    public void onBindViewHolder(@NonNull AssistancesViewHolder holder, int position) {
        requestQueue = Volley.newRequestQueue(context);
        // Obtener los datos para esta celda
        AssistancesData dataForThisCell = dataset.get(position);
        String link = dataForThisCell.getLink();
        String secretkey = dataForThisCell.getSecretKey();
        // Mostrar los datos en el ViewHolder o no
        if (secretkey != "null") {
            holder.sk.setVisibility(View.VISIBLE);
        } else {
            holder.sk.setVisibility(View.GONE);
        }
        if (link != "null") {
            holder.link_button.setVisibility(View.VISIBLE);
        } else {
            holder.link_button.setVisibility(View.GONE);
        }
        holder.info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("event_id", dataForThisCell.getEventId());
                context.startActivity(intent);
            }
        });
        holder.like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isLiked = dataForThisCell.getUserLiked();
                String url = Server.name + "/user/likedevent/" + dataForThisCell.getEventId();
                int method = isLiked ? Request.Method.DELETE : Request.Method.POST;
                JsonObjectRequestWithAuthentication request = new JsonObjectRequestWithAuthentication(
                        method, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                dataForThisCell.setUserLiked(!isLiked);
                                holder.like_icon.setImageResource(isLiked ? R.drawable.like_selected : R.drawable.like_unselected);
                                notifyItemChanged(holder.getAdapterPosition());
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Manejar el error
                            }
                        },
                        holder.itemView.getContext()
                );
                requestQueue.add(request);
            }
        });

        holder.link_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = dataForThisCell.getLink();
                if (url != null && !url.isEmpty()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    holder.itemView.getContext().startActivity(browserIntent);
                }
            }
        });
        holder.unassist_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showUnassistConfirmationDialog(dataForThisCell, holder.getAdapterPosition());
            }
        });
        String pageNumberText = (holder.getAdapterPosition() + 1) + "/" + getItemCount();
        holder.page_number.setText(pageNumberText);
        holder.showData(dataForThisCell);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
    private void showUnassistConfirmationDialog(AssistancesData dataForThisCell, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de que quieres cerrar la sesión?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Realizar la solicitud DELETE a user/session
                        unAssist(dataForThisCell, position);
                    }
                })
                .setNegativeButton("No", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    private void unAssist(AssistancesData dataForThisCell, int position){
        String url = Server.name + "/user/assistevent/" + dataForThisCell.getEventId();
        JsonObjectRequestWithAuthentication request = new JsonObjectRequestWithAuthentication(
                Request.Method.DELETE, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dataset.remove(dataForThisCell);
                        notifyItemRemoved(position);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar el error
                    }
                },
                context
        );
        requestQueue.add(request);
    }
}