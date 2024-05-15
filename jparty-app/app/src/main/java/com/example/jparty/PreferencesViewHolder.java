package com.example.jparty;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jparty.fragments.Util;

import java.util.List;

public class PreferencesViewHolder extends RecyclerView.ViewHolder {
    // Variables para los elementos de la vista
    private TextView place_name;
    private CheckBox isSelectedCheckBox;
    private ImageView imageView;
    private List<PreferencesData> dataset;

    // Constructor del ViewHolder
    public PreferencesViewHolder(@NonNull View ivi, PreferencesAdapter adapter) {
        super(ivi);
        // Encontrar los elementos de la vista
        place_name = ivi.findViewById(R.id.music_genre);
        isSelectedCheckBox = ivi.findViewById(R.id.checkBoxMusic);
        imageView = ivi.findViewById(R.id.image_view);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar el estado del CheckBox cuando se hace clic en la celda
                int position = getAdapterPosition();
                boolean isChecked = !isSelectedCheckBox.isChecked();
                isSelectedCheckBox.setChecked(isChecked);

                adapter.onItemClicked(position, isChecked);
            }
        });
    }

    // Método para mostrar los datos en los elementos de la vista
    public void showData(PreferencesData items) {
        // Establecer el texto de los TextViews y la imagen del ImageView
        place_name.setText(items.getPlace_Name());
        isSelectedCheckBox.setChecked(items.getMusicSelected());
        Util.downloadBitmapToImageView(items.getImage_url(), this.imageView);
    }
}