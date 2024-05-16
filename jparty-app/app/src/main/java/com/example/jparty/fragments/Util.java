package com.example.jparty.fragments;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class Util {

    // Procederemos a descargar una imagen desde una URL y establecerla en un ImageView gracias al
    // siguiente método.
    public static void downloadBitmapToImageView(String url, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(url)
                .into(imageView);
    }
}