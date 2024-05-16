package com.example.jparty.fragments;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.jparty.JsonObjectRequestWithAuthentication;
import com.example.jparty.MainActivity;
import com.example.jparty.R;
import com.example.jparty.Server;

import org.json.JSONObject;

import java.util.Calendar;


public class AccountFragment extends Fragment {
    private TextView birthdate_textview, email_textview, username_textview, province_textview;
    private EditText passwordrepeatedinput, userinputEditText, emailinputEditText, birthdateinputEditText, passwordinputEditText;
    private Spinner provinceSpinner;

    private ImageButton circle_button;
    private ImageButton circle_button_manager, circle_button_delete;
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
        circle_button_delete = view.findViewById(R.id.circle_button_delete);
        username_textview = view.findViewById(R.id.username_textview);
        email_textview = view.findViewById(R.id.email_textview);
        province_textview = view.findViewById(R.id.province_textview);
        birthdate_textview = view.findViewById(R.id.birthdate_textview);
        passwordinputEditText = view.findViewById(R.id.passwordinput);
        passwordrepeatedinput = view.findViewById(R.id.passwordrepeatedinput);
        circle_button = view.findViewById(R.id.circle_button);
        circle_button_manager = view.findViewById(R.id.circle_button_manager);
        userinputEditText = view.findViewById(R.id.userinput);
        emailinputEditText = view.findViewById(R.id.emailinput);
        birthdateinputEditText = view.findViewById(R.id.birthdateinput);
        provinceSpinner = view.findViewById(R.id.provinceSpinner);
        manager_checkbox = view.findViewById(R.id.manager_checkbox);
        setUser();
        birthdateinputEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AccountFragment.this.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String month, day;
                                // si el mes o el dia es menor a 10 se le añade un 0 delante ("05/03/2000")
                                if (monthOfYear < 10) {
                                    month = "0" + (monthOfYear + 1);
                                } else {
                                    month = String.valueOf(monthOfYear + 1);
                                }
                                if (dayOfMonth < 10) {
                                    day = "0" + dayOfMonth;
                                } else {
                                    day = String.valueOf(dayOfMonth);
                                }
                                if (year < 2024) {
                                    birthdateinputEditText.setText(year + "-" + month + "-" + day);
                                } else {
                                    birthdateinputEditText.setText("");
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(AccountFragment.this.getContext(),
                R.array.province_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provinceSpinner.setAdapter(adapter);
        circle_button_manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateManager();
            }
        });
        circle_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// Asegúrate de tener una instancia de RequestQueue disponible.
                updateUser();
            }
        });
        circle_button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "No se puede eliminar la cuenta", Toast.LENGTH_SHORT).show();
                //deleteUser();
            }
        });
    }

    public void setUser() {
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
                            SpinnerAdapter adapter = provinceSpinner.getAdapter();
                            int spinnerPosition = 0;

                            for (int i = 0; i < adapter.getCount(); i++) {
                                if (adapter.getItem(i).toString().equals(province)) {
                                    spinnerPosition = i;
                                    break;
                                }
                            }
                            birthdate_textview.setText(birthdate);
                            email_textview.setText(email);
                            username_textview.setText(username);
                            province_textview.setText(province);
                            userinputEditText.setText(username);
                            emailinputEditText.setText(email);
                            birthdateinputEditText.setText(birthdate);
                            provinceSpinner.setSelection(spinnerPosition);
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

    public void updateManager() {
        Boolean manager = manager_checkbox.isChecked();
        JSONObject json = new JSONObject();
        MainActivity mainActivity = (MainActivity) getActivity();
        try {
            json.put("manager", manager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequestWithAuthentication request = new JsonObjectRequestWithAuthentication(
                Request.Method.PUT,
                Server.name + "/user/manager",
                json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            SharedPreferences preferences = getContext().getSharedPreferences("JPARTY_APP_PREFS", getContext().MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("VALID_MANAGER", manager);
                            editor.commit();
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
        requestQueue.add(request);
    }

    public void updateUser() {
        String username = userinputEditText.getText().toString();
        String email = emailinputEditText.getText().toString();
        String birthdate = birthdateinputEditText.getText().toString();
        String province = provinceSpinner.getSelectedItem().toString();
        String password = passwordinputEditText.getText().toString();
        String password2 = passwordrepeatedinput.getText().toString();
        if (username.isEmpty()) {
            userinputEditText.setError("El nombre de usuario es obligatorio");
            return;
        }
        if (email.isEmpty()) {
            emailinputEditText.setError("El email es obligatorio");
            return;
        }
        if (birthdate.isEmpty()) {
            birthdateinputEditText.setError("La fecha de nacimiento es obligatoria");
            return;
        }
        if (!password.isEmpty() && !password.equals(password2)) {
            passwordrepeatedinput.setError("Las contraseñas no coinciden");
            return;
        }
        if (provinceSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(getContext(), "La provincia es obligatoria", Toast.LENGTH_SHORT).show();
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
                            SharedPreferences preferences = getContext().getSharedPreferences("JPARTY_APP_PREFS", getContext().MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("VALID_USERNAME", username);
                            editor.putString("VALID_EMAIL", email);
                            birthdate_textview.setText(birthdate);
                            email_textview.setText(email);
                            username_textview.setText(username);
                            province_textview.setText(province);
                            userinputEditText.setText(username);
                            emailinputEditText.setText(email);
                            birthdateinputEditText.setText(birthdate);
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