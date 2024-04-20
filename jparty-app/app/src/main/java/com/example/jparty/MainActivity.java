 package com.example.jparty;

 import android.content.Context;
 import android.os.Bundle;
 import android.view.MenuItem;

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

     @Override
     protected void onCreate(Bundle savedInstanceState) {//inicializamos los atributos
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);
         drawerLayout = findViewById(R.id.drawer_layout);
         toolbar = findViewById(R.id.toolbar);
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
         setSupportActionBar(toolbar);
         ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                 this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
         drawerLayout.addDrawerListener(toggle);
         toggle.syncState();
         NavigationView navigationView = findViewById(R.id.nav_view);
         //   creamos el elemento que escuchara en cual boton clickamos de nuestro menú
         navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
             @Override
             public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                 // menu que lleva a sus actividades
                 Fragment fragment = null;
                 if (item.getItemId() == R.id.homescreen) {
                     //fragment = new HotspotsFragment();
                 } else if (item.getItemId() == R.id.likedevents) {
                     // fragment = new RecommendedFragment();
                 } else if (item.getItemId() == R.id.assistevents) {
                     // fragment = new SavedPlacesFragment();
                 } else if (item.getItemId() == R.id.own_events) {
                     // fragment = new SummerModeFragment();
                 }else if((item.getItemId() == R.id.accountsettings)&& (manager == true)){
                     //fragment = new SavedPlacesFragment();
                 } else if (item.getItemId() == R.id.preferences) {
                     // fragment = new GeneralMapFragment();
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
     }
 }