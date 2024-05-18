package com.example.jparty.fragments;


import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.jparty.JsonObjectRequestWithAuthentication;
import com.example.jparty.R;
import com.example.jparty.Server;

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
    public TextView assist_count;
    public LinearLayout key_button;
    public LinearLayout link_button;
    public ConstraintLayout recycler_view;
    public ImageView link_icon;
    private ImageView image_view;
    public ImageView assist_icon;
    public ImageView like_icon;
    public ImageButton assist_button;
    public ImageButton like_button;
    public RequestQueue requestQueue;

    // Constructor del ViewHolder
    public EventsViewHolder(@NonNull View ivi) {
        super(ivi);
        // Encontrar los elementos de la vista
        event_name = ivi.findViewById(R.id.event_name);
        description = ivi.findViewById(R.id.description);
        date = ivi.findViewById(R.id.date);
        assist_count = ivi.findViewById(R.id.assist_count);
        music_name = ivi.findViewById(R.id.music_name);
        key_button = ivi.findViewById(R.id.key_button);
        link_button = ivi.findViewById(R.id.link_button);
        link_icon = ivi.findViewById(R.id.link_icon);
        image_view = ivi.findViewById(R.id.image_view);
        assist_icon = ivi.findViewById(R.id.assist_icon);
        like_icon = ivi.findViewById(R.id.like_icon);
        assist_button = ivi.findViewById(R.id.assist_button);
        like_button = ivi.findViewById(R.id.like_button);
        recycler_view = ivi.findViewById(R.id.recycler_view);
        this.requestQueue = Volley.newRequestQueue(itemView.getContext());

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
        } else {
            this.assist_icon.setImageResource(R.drawable.balloon_unselected);
        }
        if (items.getUserLike()) {
            this.like_icon.setImageResource(R.drawable.like_selected);
        } else {
            this.like_icon.setImageResource(R.drawable.like_unselected);
        }
        try {
            Util.downloadBitmapToImageView(items.getImage_url(), this.image_view);
        } catch (Exception e) {
            this.image_view.setImageResource(R.drawable.musicstock_icon); // Reemplaza 'default_image' con tu imagen predeterminada
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
