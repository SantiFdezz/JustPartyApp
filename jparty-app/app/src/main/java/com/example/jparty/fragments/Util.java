package com.example.jparty.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
public class Util {

    // Procederemos a descargar una imagen desde una URL y establecerla en un ImageView gracias al
    // siguiente método.
    public static void downloadBitmapToImageView(String url, ImageView imageView) {
        Context context = imageView.getContext(); // Obtenemos el contexto del ImageView.
        if (context instanceof Activity) { // Verificamos que este sea una instancia de Activity.
            // Creamos un nuevo hilo para realizar la operación de red en segundo plano.
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap image = getBitmapFromURL(url); // Obtenemos un Bitmap desde la url.
                    // Actualizamos la interfaz de usuario en el hilo principal de la actividad.
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        // Establecemos el Bitmap descargado en el ImageView.
                        public void run() {
                            imageView.setImageBitmap(image);
                        }
                    });
                }
            });
            thread.start(); // Inicamos el hilo creado.
        }
    }

    // Creamos un método privado para obtener un objeto Bitmap desde una URL.
    private static Bitmap getBitmapFromURL(String urlString) {
        try {
            URL url = new URL(urlString); // Creamos una URL a partir de la cadena del JSON.
            URLConnection connection = url.openConnection(); // Abrimos una conexión a ella.
            connection.connect();

            // Obtenemos un flujo de enStrada desde la conexión.
            InputStream input = connection.getInputStream();
            // Decodificamos el flujo de entrada desde la conexión.
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace(); // Maneja una excepción en caso de error de conexión o decodificación.
            return null;
        }
    }
}