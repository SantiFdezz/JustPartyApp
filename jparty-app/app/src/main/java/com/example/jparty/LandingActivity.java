package com.example.jparty;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class LandingActivity extends AppCompatActivity {
    // Definición de variables.
    private ImageButton circleButton;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing); // Cambia a tu layout de landing page

        // Inicialización de los elementos de la interfaz de usuario
        circleButton = findViewById(R.id.circle_button);

        // Configuración del listener del botón de acceso
        circleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtén las SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("JPARTY_APP_PREFS", MODE_PRIVATE);

                // Verifica si el sessionToken y el email existen
                String sessionToken = sharedPreferences.getString("VALID_TOKEN", null);
                String email = sharedPreferences.getString("VALID_EMAIL", null);

                if (sessionToken != null && email != null) {
                    // Si el sessionToken y el email existen, inicia MainActivity
                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                } else {
                    // Si el sessionToken y el email no existen, inicia LoginActivity
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
