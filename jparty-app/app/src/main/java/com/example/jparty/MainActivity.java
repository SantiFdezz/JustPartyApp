 package com.example.jparty;

 import android.content.Context;
 import android.content.Intent;
 import android.content.SharedPreferences;
 import android.os.Bundle;
 import android.view.MenuItem;
 import android.view.View;
 import android.widget.TextView;

 import com.example.jparty.fragments.HomeFragment;
 import com.example.jparty.fragments.LikeFragment;
 import com.example.jparty.fragments.OwnFragment;
 import com.google.android.material.navigation.NavigationView;

 import androidx.activity.OnBackPressedCallback;
 import androidx.annotation.NonNull;
 import androidx.appcompat.app.ActionBarDrawerToggle;
 import androidx.appcompat.app.AppCompatActivity;
 import androidx.appcompat.widget.Toolbar;
 import androidx.core.view.GravityCompat;
 import androidx.fragment.app.Fragment;
 import androidx.drawerlayout.widget.DrawerLayout;

 public class MainActivity extends AppCompatActivity {
     private Context context = this;
     private DrawerLayout drawerLayout;
     private Toolbar toolbar;
     private boolean manager = false;
     public static boolean isRunning;

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
     protected void onCreate(Bundle savedInstanceState) {//inicializamos los atributos
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);
         drawerLayout = findViewById(R.id.drawer_layout);
         toolbar = findViewById(R.id.toolbar);
         NavigationView navigationView = findViewById(R.id.nav_view);
         SharedPreferences sharedPreferences = getSharedPreferences("JPARTY_APP_PREFS", MODE_PRIVATE);
         String validEmail = sharedPreferences.getString("VALID_EMAIL", null);
         String validUsername = sharedPreferences.getString("VALID_USERNAME", null);
         //Cargamos el header del menu y lo editamos con los datos del usuario
         View headerView = navigationView.getHeaderView(0);
         TextView userNameTextView = headerView.findViewById(R.id.username_nav);
         TextView emailTextView = headerView.findViewById(R.id.email_nav);
         userNameTextView.setText(validUsername);
         emailTextView.setText(validEmail);
         // Obtén las preferencias compartidas


         // Verifica si es la primera vez que se abre la cuenta
         boolean isFirstTime = sharedPreferences.getBoolean("isFirstTime", true);

         if (isFirstTime) {
             // Si es la primera vez, inicia PreferencesActivity
             Intent intent = new Intent(this, PreferencesActivity.class);
             startActivity(intent);

             // Actualiza el valor de isFirstTime en las preferencias compartidas
             SharedPreferences.Editor myEdit = sharedPreferences.edit();
             myEdit.putBoolean("isFirstTime", false);
             myEdit.apply();
         } else {
             setSupportActionBar(toolbar);
             ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                     this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
             drawerLayout.addDrawerListener(toggle);
             toggle.syncState();
             //   creamos el elemento que escuchara en cual boton clickamos de nuestro menú
             navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                 @Override
                 public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                     // menu que lleva a sus actividades
                     Fragment fragment = null;
                     if (item.getItemId() == R.id.homescreen) {
                         fragment = new HomeFragment();
                     } else if (item.getItemId() == R.id.likedevents) {
                         fragment = new LikeFragment();
                     } else if (item.getItemId() == R.id.assistevents) {
                         // fragment = new SavedPlacesFragment();
                     } else if ((item.getItemId() == R.id.own_events) && (manager == false)) {
                         fragment = new OwnFragment();
                     }else if(item.getItemId() == R.id.accountsettings){
                         //fragment = new SavedPlacesFragment();
                     } else if (item.getItemId() == R.id.preferences) {
                         Intent intent = new Intent(context, PreferencesActivity.class);
                         startActivity(intent);
                     } else if (item.getItemId() == R.id.closesession) {
                         //fragment = new Routes();
                     }

                     //si no llega ningun fragment
                     if (fragment != null) {
                         getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                         drawerLayout.closeDrawer(GravityCompat.START);
                         return true;
                     }
                     // Cierra el Navigation Drawer después de la selección
                     return false;
                 }
             });
             getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();


         }

         // Verificar si la actividad se inició con una acción específica
             //getSupportFragmentManager().beginTransaction()
              //       .replace(R.id.fragment_container, new LoginActivity())
                //     .commit();

         //creamos metodo que cierre el menu y no la app
         getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
             //metodo que cierra el menu si se pulsa atrás.
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
 }