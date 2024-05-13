package com.example.jparty.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.jparty.JsonObjectRequestWithAuthentication;
import com.example.jparty.R;
import com.example.jparty.Server;

import org.json.JSONObject;


public class AccountFragment extends Fragment {

    private TextView birthdateTextView, provinceTextView, emailTextView, usernameTextView;
    private EditText passwordrepeatedinput, userinputEditText, emailinputEditText, birthdateinputEditText, provinceeditText, passwordinputEditText;

    private ImageButton circle_button;
    private ImageButton circle_button_manager;
    private CheckBox manager_checkbox;
    private RequestQueue requestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        requestQueue = Volley.newRequestQueue(getContext());
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        passwordinputEditText = view.findViewById(R.id.passwordinput);
        passwordrepeatedinput = view.findViewById(R.id.passwordrepeatedinput);
        circle_button = view.findViewById(R.id.circle_button);
        circle_button_manager = view.findViewById(R.id.circle_button_manager);
        usernameTextView = view.findViewById(R.id.username_textview);
        emailTextView = view.findViewById(R.id.email_textview);
        birthdateTextView = view.findViewById(R.id.birthdate_textview);
        provinceTextView = view.findViewById(R.id.province_textview);
        userinputEditText = view.findViewById(R.id.userinput);
        emailinputEditText = view.findViewById(R.id.emailinput);
        birthdateinputEditText = view.findViewById(R.id.birthdateinput);
        provinceeditText = view.findViewById(R.id.provinceeditText);
        manager_checkbox = view.findViewById(R.id.manager_checkbox);
        setUser();
        circle_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// Asegúrate de tener una instancia de RequestQueue disponible.
            }
        });
    }
    public void setUser(){
        JsonObjectRequestWithAuthentication request = new JsonObjectRequestWithAuthentication(
                Request.Method.GET,
                Server.name + "/user",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String username = response.getString("username");
                            String email = response.getString("email");
                            String birthdate = response.getString("birthdate");
                            String province = response.getString("province");
                            Boolean manager = response.getBoolean("manager");

                            usernameTextView.setText(username);
                            emailTextView.setText(email);
                            birthdateTextView.setText(birthdate);
                            provinceTextView.setText(province);

                            userinputEditText.setText(username);
                            emailinputEditText.setText(email);
                            birthdateinputEditText.setText(birthdate);
                            provinceeditText.setText(province);
                            manager_checkbox.setChecked(manager);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                },
                getContext() // Asegúrate de pasar el contexto correcto aquí.
        );
        requestQueue.add(request); // Asegúrate de tener una instancia de RequestQueue disponible.
    }
    public void updateUser(){
        String username = userinputEditText.getText().toString();
        String email = emailinputEditText.getText().toString();
        String birthdate = birthdateinputEditText.getText().toString();
        String province = provinceeditText.getText().toString();
        String password = passwordinputEditText.getText().toString();
        String password2 = passwordrepeatedinput.getText().toString();
        if (password.isEmpty()) {
            password = null;
        } else if (!password.equals(password2)) {
            passwordrepeatedinput.setError("Las contraseñas no coinciden");
            return;
        }
        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("email", email);
            json.put("birthdate", birthdate);
            json.put("province", province);
            json.put("password", password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequestWithAuthentication request = new JsonObjectRequestWithAuthentication(
                Request.Method.PUT,
                Server.name + "/user",
                json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            usernameTextView.setText(username);
                            emailTextView.setText(email);
                            birthdateTextView.setText(birthdate);
                            provinceTextView.setText(province);
                            userinputEditText.setText(username);
                            emailinputEditText.setText(email);
                            birthdateinputEditText.setText(birthdate);
                            provinceeditText.setText(province);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                },
                getContext() // Asegúrate de pasar el contexto correcto aquí.
        );
        requestQueue.add(request);
    }
}