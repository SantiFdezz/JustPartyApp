package com.example.jparty.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.jparty.EditEventActivity;
import com.example.jparty.JsonObjectRequestWithAuthentication;
import com.example.jparty.R;
import com.example.jparty.Server;

import org.json.JSONObject;

import java.util.List;

public class OwnAdapter extends RecyclerView.Adapter<OwnViewHolder> {
    // Lista de elementos recomendados y fragmento que contiene el RecyclerView
    private List<OwnData> dataset;
    private Fragment fragment;
    private Context context;
    private RequestQueue requestQueue;

    // Constructor del adaptador
    public OwnAdapter(List<OwnData> dataSet, Fragment fragment, Context context) {
        this.dataset = dataSet;
        this.fragment = fragment;
        this.context = context;
    }

    // Método para crear un nuevo ViewHolder
    @NonNull
    @Override
    public OwnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar la vista de la celda del RecyclerView
        View eventsView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_cell_own, parent, false);
        // Crear y retornar un nuevo ViewHolder
        return new OwnViewHolder(eventsView, this.dataset, this);
    }

    // Método para vincular los datos con el ViewHolder
    @Override
    public void onBindViewHolder(@NonNull OwnViewHolder holder, int position) {
        // Obtener los datos para esta celda
        OwnData dataForThisCell = dataset.get(position);
        String link = dataForThisCell.getLink();
        String secretkey = dataForThisCell.getSecretKey();
        requestQueue = Volley.newRequestQueue(context);
        int eventId = dataForThisCell.getEvent_Id().intValue();
        // Mostrar los datos en el ViewHolder o no
        if (secretkey != "null") {
            holder.key_button.setVisibility(View.VISIBLE);
        } else {
            holder.key_button.setVisibility(View.GONE);
            holder.key_text.setText(secretkey);
        }
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = holder.getAdapterPosition();
                showDeleteEventDialog(position, eventId, holder);
            }
        });

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = holder.getAdapterPosition();
                editEvent(position, holder, eventId);
            }
        });

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
    private void showDeleteEventDialog(final int position, final int eventId, final RecyclerView.ViewHolder holder) {
        new AlertDialog.Builder(holder.itemView.getContext())
                .setTitle("Eliminar Evento")
                .setMessage("¿Estás seguro de que quieres eliminar este evento?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteEvent(position, holder, eventId);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
    private void showEditEventDialog(final int position, final int eventId, final RecyclerView.ViewHolder holder) {
        new AlertDialog.Builder(holder.itemView.getContext())
                .setTitle("Editar Evento")
                .setMessage("¿Estás seguro de que quieres editar este evento?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editEvent(position, holder, eventId);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
    private void deleteEvent(final int position, final RecyclerView.ViewHolder holder, final int eventId) {
        String url = Server.name + "/event/" + eventId;
        JsonObjectRequestWithAuthentication request = new JsonObjectRequestWithAuthentication(
                Request.Method.DELETE,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dataset.remove(position);
                        notifyItemRemoved(position);
                        // Mostrar un Toast confirmando la eliminación del evento
                        Toast.makeText(holder.itemView.getContext(), "Evento eliminado", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejo de errores de la solicitud
                        error.printStackTrace();
                        // Mostrar un Toast en caso de error
                        Toast.makeText(holder.itemView.getContext(), "Error al eliminar evento", Toast.LENGTH_SHORT).show();
                    }
                },
                holder.itemView.getContext()
        );
        // Añadir la solicitud a la cola de solicitudes
        requestQueue.add(request);
    }

    private void editEvent(final int position, final RecyclerView.ViewHolder holder, final int eventId) {
        String url = Server.name + "/event/" + eventId;
        Intent intent = new Intent(holder.itemView.getContext(), EditEventActivity.class);
        intent.putExtra("event_id", eventId);
        holder.itemView.getContext().startActivity(intent);
    }

}