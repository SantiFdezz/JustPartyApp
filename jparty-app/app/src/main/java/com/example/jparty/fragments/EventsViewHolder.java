package com.example.jparty.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.jparty.JsonArrayRequestWithAuthentication;
import com.example.jparty.JsonObjectRequestWithAuthentication;
import com.example.jparty.R;
import com.example.jparty.Server;
import com.example.jparty.fragments.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventsViewHolder extends RecyclerView.ViewHolder {
    // Variables para los elementos de la vista
    private TextView event_name;
    private TextView description;
    private TextView music_name;
    private TextView date;
    private TextView assist_count;
    public LinearLayout key_button;
    private ImageView key_icon;
    public LinearLayout link_button;
    public ConstraintLayout recycler_view;
    private ImageView link_icon;
    private ImageView image_view;
    private ImageView assist_icon;
    private ImageView like_icon;
    private ImageButton assist_button;
    private ImageButton like_button;
    private RequestQueue requestQueue;
    private List<EventsData> dataset;

    // Constructor del ViewHolder
    public EventsViewHolder(@NonNull View ivi, List<EventsData> dataset, EventsAdapter adapter){
        super(ivi);
        // Encontrar los elementos de la vista
        event_name = ivi.findViewById(R.id.event_name);
        description = ivi.findViewById(R.id.description);
        date = ivi.findViewById(R.id.date);
        assist_count = ivi.findViewById(R.id.assist_count);
        music_name = ivi.findViewById(R.id.music_name);
        key_button = ivi.findViewById(R.id.key_button);
        key_icon = ivi.findViewById(R.id.key_icon);
        link_button = ivi.findViewById(R.id.link_button);
        link_icon = ivi.findViewById(R.id.link_icon);
        image_view = ivi.findViewById(R.id.image_view);
        assist_icon = ivi.findViewById(R.id.assist_icon);
        like_icon = ivi.findViewById(R.id.like_icon);
        assist_button = ivi.findViewById(R.id.assist_button);
        like_button = ivi.findViewById(R.id.like_button);
        recycler_view = ivi.findViewById(R.id.recycler_view);
        this.requestQueue = Volley.newRequestQueue(itemView.getContext());
        this.dataset = dataset;

        assist_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EventsData currentItem = dataset.get(getAdapterPosition());
                boolean isAssisted = currentItem.getUserAssist();
                String url = Server.name+"/user/assistevent/"+currentItem.getEvent_Id();
                int method = isAssisted ? Request.Method.DELETE : Request.Method.POST;
                JsonObjectRequestWithAuthentication request = new JsonObjectRequestWithAuthentication(
                        method, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                currentItem.setUserAssist(!isAssisted);
                                currentItem.setAssistances(!isAssisted ? (currentItem.getAssistances() + 1) : (currentItem.getAssistances() - 1));
                                assist_icon.setImageResource(isAssisted ? R.drawable.balloon_selected : R.drawable.balloon_unselected);
                                adapter.notifyItemChanged(getAdapterPosition()); // Notificar al adaptador para que actualice la vista
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Manejar el error
                            }
                        },
                        itemView.getContext()
                );
                requestQueue.add(request);
            }
        });
        like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EventsData currentItem = dataset.get(getAdapterPosition());
                boolean isLiked = currentItem.getUserLike();
                String url = Server.name+"/user/likedevent/"+currentItem.getEvent_Id();
                int method = isLiked ? Request.Method.DELETE : Request.Method.POST;
                JsonObjectRequestWithAuthentication request = new JsonObjectRequestWithAuthentication(
                        method, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                currentItem.setUserLike(!isLiked);
                                like_icon.setImageResource(isLiked ? R.drawable.like_selected : R.drawable.like_unselected);
                                adapter.notifyItemChanged(getAdapterPosition()); // Notificar al adaptador para que actualice la vista
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Manejar el error
                            }
                        },
                        itemView.getContext()
                );
                requestQueue.add(request);
            }
        });

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EventsData currentItem = dataset.get(getAdapterPosition());
                String url = currentItem.getLink();
                if (url != null && !url.isEmpty()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    itemView.getContext().startActivity(browserIntent);
                }
            }
        };

        link_button.setOnClickListener(clickListener);
        link_icon.setOnClickListener(clickListener);
    }

    // Método para mostrar los datos en los elementos de la vista
    public void showData(EventsData items) {
        // Establecer el texto de los TextViews y la imagen del ImageView
        event_name.setText(items.getPlace_Name());
        description.setText(truncateDescription(items.getDescription()));
        music_name.setText(items.getTag_Name());
        date.setText(editdateLabel(items));
        assist_count.setText(items.getAssistances().toString());
        if (items.getUserAssist()) {
            this.assist_icon.setImageResource(R.drawable.balloon_selected);
        }else {
            this.assist_icon.setImageResource(R.drawable.balloon_unselected);
        }
        if (items.getUserLike()) {
            this.like_icon.setImageResource(R.drawable.like_selected);
        }else {
            this.like_icon.setImageResource(R.drawable.like_unselected);
        }
        try {
            Util.downloadBitmapToImageView(items.getImage_url(), this.image_view);
        } catch (Exception e) {
            this.image_view.setImageResource(R.drawable.ic_launcher_background); // Reemplaza 'default_image' con tu imagen predeterminada
        }

    }

    public String truncateDescription(String description) {
        if (description.length() > 50) {
            return description.substring(0, 47) + "...";
        } else {
            return description;
        }
    }

    public String editdateLabel(EventsData items) {
        String formattedDate = ""; // Initialize the variable

        try {
            // Parsear la fecha desde la cadena
            SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            Date date = originalFormat.parse(items.getEvent_Date());

            // Formatear la fecha al formato deseado
            SimpleDateFormat newFormat = new SimpleDateFormat("dd MMMM", new Locale("es", "ES"));
            formattedDate = newFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }
}
