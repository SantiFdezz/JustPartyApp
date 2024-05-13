 package com.example.jparty;

 import android.content.Context;
 import android.content.Intent;
 import android.content.SharedPreferences;
 import android.os.Bundle;
 import android.view.MenuItem;
 import android.view.View;
 import android.widget.TextView;

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

 import androidx.activity.OnBackPressedCallback;
 import androidx.annotation.NonNull;
 import androidx.appcompat.app.ActionBarDrawerToggle;
 import androidx.appcompat.app.AppCompatActivity;
 import androidx.appcompat.widget.Toolbar;
 import androidx.core.view.GravityCompat;
 import androidx.fragment.app.Fragment;
 import androidx.drawerlayout.widget.DrawerLayout;

 import org.json.JSONObject;

 public class MainActivity extends AppCompatActivity {
     private Context context = this;
     private DrawerLayout drawerLayout;
     private Toolbar toolbar;
     private boolean manager = true;
     private RequestQueue requestQueue;
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
         requestQueue = Volley.newRequestQueue(this);
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
                         fragment = new AssistancesFragment();
                     } else if ((item.getItemId() == R.id.own_events) && (manager == true)) {
                         fragment = new OwnFragment();
                     }else if(item.getItemId() == R.id.accountsettings){
                         fragment = new AccountFragment();
                     } else if (item.getItemId() == R.id.preferences) {
                         Intent intent = new Intent(context, PreferencesActivity.class);
                         startActivity(intent);
                     } else if (item.getItemId() == R.id.closesession) {
                         //aviso estas seguro de querer cerrar sesión? si o no
                         closeSession();
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
     public void closeSession(){
            //cerrar sesión
         JsonObjectRequestWithAuthentication request = new JsonObjectRequestWithAuthentication(
                 Request.Method.DELETE,
                 Server.name + "/user/session",
                 null,
                 new Response.Listener<JSONObject>() {
                     @Override
                     public void onResponse(JSONObject response) {
                         // Aquí puedes manejar la respuesta. Por ejemplo, puedes desloguear al usuario y redirigirlo a la pantalla de inicio de sesión.
                         SharedPreferences preferences = getSharedPreferences("JPARTY_APP_PREFS", MODE_PRIVATE);
                         SharedPreferences.Editor editor = preferences.edit();
                         editor.remove("VALID_TOKEN");
                         editor.apply();

                         Intent intent = new Intent(MainActivity.this, LandingActivity.class);
                         startActivity(intent);
                         finish();
                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         // Aquí puedes manejar el error. Por ejemplo, puedes mostrar un mensaje de error al usuario.
                     }
                 },
                 this // Asegúrate de pasar el contexto correcto aquí.
         );
         requestQueue.add(request); //
     }
 }