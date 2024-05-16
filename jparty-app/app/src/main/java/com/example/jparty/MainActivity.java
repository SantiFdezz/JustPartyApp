package com.example.jparty;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.jparty.fragments.AccountFragment;
import com.example.jparty.fragments.AssistancesFragment;
import com.example.jparty.fragments.HomeFragment;
import com.example.jparty.fragments.LikeFragment;
import com.example.jparty.fragments.OwnFragment;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private Context context = this;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private RequestQueue requestQueue;
    public static boolean isRunning;
    private boolean manager;
    private String username, email;

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeAttributes();
        checkFirstTimeUser();

        setSupportActionBar(toolbar);
        setupDrawerToggle();
        setupNavigationView();

        handleBackPress();
    }

    private void initializeAttributes() {
        requestQueue = Volley.newRequestQueue(this);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);

        SharedPreferences sharedPreferences = getSharedPreferences("JPARTY_APP_PREFS", MODE_PRIVATE);
        manager = sharedPreferences.getBoolean("VALID_MANAGER", false);
        username = sharedPreferences.getString("VALID_USERNAME", "");
        email = sharedPreferences.getString("VALID_EMAIL", "");

    }

    private void checkFirstTimeUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("JPARTY_APP_PREFS", MODE_PRIVATE);
        boolean isFirstTime = sharedPreferences.getBoolean("isFirstTime", true);

        if (isFirstTime) {
            Intent intent = new Intent(this, PreferencesActivity.class);
            startActivity(intent);

            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putBoolean("isFirstTime", false);
            myEdit.apply();
        }
        loadHomeFragment();
    }

    private void setupDrawerToggle() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                updateMenuItemsVisibility();
            }
        };

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void updateMenuItemsVisibility() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        MenuItem ownEventsItem = navigationView.getMenu().findItem(R.id.own_events);
        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.username_nav);
        TextView emailTextView = headerView.findViewById(R.id.email_nav);
        // cogemos los datos del usuario
        SharedPreferences sharedPreferences = getSharedPreferences("JPARTY_APP_PREFS", MODE_PRIVATE);
        manager = sharedPreferences.getBoolean("VALID_MANAGER", false);
        username = sharedPreferences.getString("VALID_USERNAME", "");
        email = sharedPreferences.getString("VALID_EMAIL", "");
        userNameTextView.setText(username);
        emailTextView.setText(email);
        ownEventsItem.setVisible(manager);
    }

    private void setupNavigationView() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = getFragmentForMenuItem(item);

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        return false;
    }

    private Fragment getFragmentForMenuItem(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.homescreen) {
            return new HomeFragment();
        } else if (id == R.id.likedevents) {
            return new LikeFragment();
        } else if (id == R.id.assistevents) {
            return new AssistancesFragment();
        } else if (id == R.id.own_events) {
            return manager ? new OwnFragment() : null;
        } else if (id == R.id.accountsettings) {
            return new AccountFragment();
        } else if (id == R.id.preferences) {
            startActivity(new Intent(context, PreferencesActivity.class));
            return null;
        } else if (id == R.id.closesession) {
            showLogoutConfirmationDialog();
            return null;
        } else {
            return null;
        }
    }

    private void loadHomeFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }

    private void handleBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    if (isEnabled()) {
                        setEnabled(false);
                        MainActivity.super.onBackPressed();
                    }
                }
            }
        });
    }

    private void logoutUser() {
        JsonObjectRequestWithAuthentication request = new JsonObjectRequestWithAuthentication(
                Request.Method.DELETE,
                Server.name + "/user/session",
                new JSONObject(), // Objeto JSON vacío
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Manejar la respuesta del servidor
                        Toast.makeText(context, "Sesión cerrada con éxito", Toast.LENGTH_SHORT).show();

                        // Eliminar las SharedPreferences
                        SharedPreferences preferences = context.getSharedPreferences("JPARTY_APP_PREFS", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.apply();

                        // Redirigir al usuario a la pantalla de inicio de sesión
                        Intent intent = new Intent(context, LandingActivity.class);
                        startActivity(intent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar el error
                        if (error.networkResponse == null) {
                            Toast.makeText(MainActivity.this, "La conexión no se ha establecido", Toast.LENGTH_LONG).show();
                        } else {
                            int serverCode = error.networkResponse.statusCode;
                            Toast.makeText(MainActivity.this, "Estado de respuesta " + serverCode, Toast.LENGTH_LONG).show();
                        }
                        error.printStackTrace();
                    }
                },
                this
        );
        this.requestQueue.add(request);
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de que quieres cerrar la sesión?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Realizar la solicitud DELETE a user/session
                        logoutUser();
                    }
                })
                .setNegativeButton("No", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void setManager(boolean manager) {
        this.manager = manager;
    }
}