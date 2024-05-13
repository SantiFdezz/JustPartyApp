package com.example.jparty.fragments;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jparty.R;

public class AssistancesViewHolder extends RecyclerView.ViewHolder {
    private TextView title;
    private TextView assistants;
    private TextView location;
    private TextView date;
    private TextView hour;
    private TextView price;
    public TextView sk;
    public ImageButton unassist_button;
    public ImageButton info_button;
    public ImageButton like_button;
    public ImageView like_icon;
    public LinearLayout link_button;
    public TextView page_number;

    public AssistancesViewHolder(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.title);
        assistants = itemView.findViewById(R.id.assistants);
        location = itemView.findViewById(R.id.location);
        date = itemView.findViewById(R.id.date);
        hour = itemView.findViewById(R.id.hour);
        price = itemView.findViewById(R.id.price);
        sk = itemView.findViewById(R.id.sk);
        unassist_button = itemView.findViewById(R.id.unassist_button);
        info_button = itemView.findViewById(R.id.info_button);
        like_button = itemView.findViewById(R.id.like_button);
        like_icon = itemView.findViewById(R.id.like_icon);
        link_button = itemView.findViewById(R.id.link_button);
        page_number = itemView.findViewById(R.id.page_number);
    }

    public void showData(AssistancesData item) {
        title.setText(item.getTitle());
        assistants.setText(String.valueOf(item.getAssistants()));
        location.setText(item.getStreet());
        date.setText(item.getDate());
        hour.setText(item.getHour());
       if (item.getPrice().equals("0.0")) {
            price.setText("Gratis");
        }else {
           double priceAsDouble = Double.parseDouble(item.getPrice());
           int priceWithoutDecimals = (int) priceAsDouble;
           price.setText(priceWithoutDecimals + "€");
       }
        sk.setText("Clave secreta: "+item.getSecretKey());
        if (item.getUserLiked()) {
            this.like_icon.setImageResource(R.drawable.like_selected);
        }else {
            this.like_icon.setImageResource(R.drawable.like_unselected);
        }
        // unassist_button, unassist_icon, info_button, info_icon, like_button, like_icon, link_button
    }
}
