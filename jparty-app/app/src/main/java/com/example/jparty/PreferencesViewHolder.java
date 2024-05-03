package com.example.jparty;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jparty.PreferencesData;
import com.example.jparty.R;
import com.example.jparty.fragments.Util;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PreferencesViewHolder extends RecyclerView.ViewHolder {
    // Variables para los elementos de la vista
    private TextView place_name;
    private CheckBox isSelectedCheckBox;
    private ImageView imageView;

    // Constructor del ViewHolder
    public PreferencesViewHolder(@NonNull View ivi) {
        super(ivi);
        // Encontrar los elementos de la vista
        place_name = ivi.findViewById(R.id.music_genre);
        isSelectedCheckBox = ivi.findViewById(R.id.checkBoxMusic);
        imageView = ivi.findViewById(R.id.image_view);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar el estado del CheckBox cuando se hace clic en la celda
                isSelectedCheckBox.setChecked(!isSelectedCheckBox.isChecked());
            }
        });
    }

    public boolean isChecked() {
        return isSelectedCheckBox.isChecked();
    }

    // Método para mostrar los datos en los elementos de la vista
    public void showData(PreferencesData items) {
        // Establecer el texto de los TextViews y la imagen del ImageView
        place_name.setText(items.getPlace_Name());
        Boolean musicSelected = items.getMusicSelected();
        if (items.getMusicSelected()) {
            isSelectedCheckBox.setChecked(true);
        } else {
            isSelectedCheckBox.setChecked(false);
        }
        Util.downloadBitmapToImageView(items.getImage_url(), this.imageView);
     }
    }