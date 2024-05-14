package com.example.jparty.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
public class Util {

    // Procederemos a descargar una imagen desde una URL y establecerla en un ImageView gracias al
    // siguiente método.
    public static void downloadBitmapToImageView(String url, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(url)
                .into(imageView);
    }
}