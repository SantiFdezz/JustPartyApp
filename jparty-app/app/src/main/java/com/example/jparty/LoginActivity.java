package com.example.jparty;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private EditText userEmail;
    private EditText userPassword;
    private ImageButton accessButton;
    private TextView registerRedirect;
    private Context context = this;
    private RequestQueue requestQueue;
    private ProgressBar pb1;

    public interface UserCallback {
        void onCallback(String username, boolean manager);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmail = findViewById(R.id.userinput);
        userPassword = findViewById(R.id.passwordinput);
        accessButton = findViewById(R.id.circle_button);
        registerRedirect = findViewById(R.id.register_text);
        pb1 = findViewById(R.id.loadingScreen);
        requestQueue = Volley.newRequestQueue(this);

        accessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userEmail.getText().toString();
                String password = userPassword.getText().toString();
                if (email.isEmpty()) {
                    userEmail.setError("Rellene el campo de email");
                } else if (password.isEmpty()) {
                    userPassword.setError("Rellene el campo de contraseña");
                } else if (validateLogin(email, password)) {
                    pb1.setVisibility(View.VISIBLE);
                    loginUser(email, password);
                }
            }
        });

        registerRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateLogin(String email, String password) {
        if (!email.contains("@") || email.length() < 8) {
            userEmail.setError("Formato inválido de email");
            return false;
        }
        return true;
    }

    private void loginUser(String email, String password) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("password", password);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Server.name+"/user/session",
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String receivedToken;
                        try {
                            receivedToken = response.getString("SessionToken");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        Toast.makeText(context, "Token: " + receivedToken, Toast.LENGTH_SHORT).show();

                        SharedPreferences preferences = context.getSharedPreferences("JPARTY_APP_PREFS", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("VALID_EMAIL", email);
                        editor.putString("VALID_TOKEN", receivedToken);
                        editor.commit();

                        getUser(new UserCallback() {
                            @Override
                            public void onCallback(String username, boolean manager) {
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("VALID_USERNAME", username);
                                editor.putBoolean("VALID_MANAGER", manager);
                                editor.commit();


                            }
                        });
                        Intent intent = new Intent(context, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse == null) {
                            pb1.setVisibility(View.GONE);
                            Toast.makeText(context, "La conexión no se ha establecido", Toast.LENGTH_LONG).show();
                        } else {
                            pb1.setVisibility(View.GONE);
                            int serverCode = error.networkResponse.statusCode;
                            Toast.makeText(context, "Estado de respuesta " + serverCode, Toast.LENGTH_LONG).show();
                        }
                        error.printStackTrace();
                    }
                }
        );
        this.requestQueue.add(request);
    }

    private void getUser(final UserCallback callback) {
        JsonObjectRequestWithAuthentication request = new JsonObjectRequestWithAuthentication(
                Request.Method.GET,
                "/user",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String username;
                        Boolean manager;
                        try {
                            username = response.getString("username");
                            manager = response.getBoolean("manager");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        pb1.setVisibility(View.GONE);
                        callback.onCallback(username, manager);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pb1.setVisibility(View.GONE);
                        if (error.networkResponse == null) {
                            Toast.makeText(context, "La conexión no se ha establecido", Toast.LENGTH_LONG).show();
                        } else {
                            int serverCode = error.networkResponse.statusCode;
                            Toast.makeText(context, "Estado de respuesta " + serverCode, Toast.LENGTH_LONG).show();
                        }
                        error.printStackTrace();
                    }
                },
                this
        );
        this.requestQueue.add(request);
    }
}