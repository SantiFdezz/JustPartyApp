package com.example.jparty;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.jparty.fragments.DetailFragment;

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        DetailFragment fragment = new DetailFragment();
        Intent intent = getIntent();
        int itemId = intent.getIntExtra("event_id", -1);
        System.out.println(itemId);
        Bundle bundle = new Bundle();
        String event_id = "/" + itemId;
        bundle.putString("event_id", event_id);
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }
}