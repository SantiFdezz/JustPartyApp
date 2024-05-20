package com.example.jparty;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class EditEventActivity extends AppCompatActivity {
    private Spinner eventProvince, eventMusicGenre;
    private EditText eventName, eventLocation, eventDate, eventPrice, eventTime, eventLink, eventImage, eventDescription;
    private TextView access_text;
    private ImageButton circle_button, back_arrow;
    private boolean isGetDone = false;
    private RequestQueue requestQueue;
    private EditText secretkey;

    private CheckBox sk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_addevent);
        access_text = findViewById(R.id.access_text);
        eventName = findViewById(R.id.event);
        eventLocation = findViewById(R.id.location);
        eventProvince = findViewById(R.id.province);
        eventMusicGenre = findViewById(R.id.music_genre);
        eventPrice = findViewById(R.id.price);
        eventDate = findViewById(R.id.date);
        eventTime = findViewById(R.id.time);
        eventLink = findViewById(R.id.link);
        eventImage = findViewById(R.id.image);
        eventDescription = findViewById(R.id.description);
        circle_button = findViewById(R.id.circle_button);
        sk = findViewById(R.id.skcheckbox);
        secretkey = findViewById(R.id.sk);
        back_arrow = findViewById(R.id.back_arrow);
        secretkey.setEnabled(false);

        int eventId = getIntent().getIntExtra("event_id", -1);
        if (eventId != -1) {
            getEventDetails(eventId);
            isGetDone = true;
            access_text.setText("Editar evento");
        } else {
            isGetDone = false;
        }
        sk.setOnClickListener(v -> {
            if (sk.isChecked()) {
                secretkey.setEnabled(true);
            } else {
                secretkey.setEnabled(false);
            }
        });
        eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditEventActivity.this,
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
                                Calendar selectedDate = Calendar.getInstance();
                                selectedDate.set(year, monthOfYear, dayOfMonth);
                                if (selectedDate.after(Calendar.getInstance())) {
                                    eventDate.setText(year + "-" + month + "-" + day);
                                } else {
                                    eventDate.setText("");
                                }

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        back_arrow.setOnClickListener(v -> {
            finish();
        });
        eventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(EditEventActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String hour, min;
                                // si la hora o el minuto es menor a 10 se le añade un 0 delante ("05:03")
                                if (hourOfDay < 10) {
                                    hour = "0" + hourOfDay;
                                } else {
                                    hour = String.valueOf(hourOfDay);
                                }
                                if (minute < 10) {
                                    min = "0" + minute;
                                } else {
                                    min = String.valueOf(minute);
                                }
                                eventTime.setText(hour + ":" + min);
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();
            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.province_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventProvince.setAdapter(adapter);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.music_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventMusicGenre.setAdapter(adapter);

        circle_button.setOnClickListener(v -> {
            validateEventDetails();
            // Código para enviar la solicitud de creación/edicion de evento de evento
            if (isGetDone) {
                // Si se ha hecho el GET antes, hacer una solicitud PUT
                createEvent(1, ("/event/" + eventId));
            } else {
                // Si no se ha hecho el GET antes, hacer una solicitud POST
                createEvent(2, "/events");
            }

        });
    }


    private void getEventDetails(int eventId) {

        JsonArrayRequestWithAuthentication request = new JsonArrayRequestWithAuthentication(
                Request.Method.GET,
                "/event/" + eventId,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject eventObject = response.getJSONObject(0);
                            eventName.setText(eventObject.getString("title"));
                            eventLocation.setText(eventObject.getString("street"));
                            eventDate.setText(eventObject.getString("date"));
                            String province = eventObject.getString("province");
                            ArrayAdapter<String> adapter = (ArrayAdapter<String>) eventProvince.getAdapter();
                            int position = adapter.getPosition(province);
                            eventProvince.setSelection(position);
                            String music_genre = eventObject.getString("music_genre");
                            adapter = (ArrayAdapter<String>) eventMusicGenre.getAdapter();
                            position = adapter.getPosition(music_genre);
                            eventMusicGenre.setSelection(position);
                            eventPrice.setText(eventObject.getString("price"));
                            eventTime.setText(eventObject.getString("time"));
                            eventLink.setText(eventObject.getString("link"));
                            eventImage.setText(eventObject.getString("image"));
                            eventDescription.setText(eventObject.getString("description"));
                            if (!eventObject.getString("secretkey").equals("null")) {
                                sk.setChecked(true);
                                secretkey.setEnabled(true);
                                secretkey.setText(eventObject.getString("secretkey"));
                            }
                            // Actualiza los demás campos aquí...
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse == null) {
                            Toast.makeText(EditEventActivity.this, "La conexión no se ha establecido", Toast.LENGTH_LONG).show();
                        } else {
                            int serverCode = error.networkResponse.statusCode;
                            Toast.makeText(EditEventActivity.this, "Estado de respuesta " + serverCode, Toast.LENGTH_LONG).show();
                        }
                        error.printStackTrace();
                    }
                },
                this
        );

        // Añadir la solicitud a la cola de solicitudes
        this.requestQueue.add(request);
    }

    private void createEvent(int method, String url) {
        int methodu;
        if (method == 1) {
            methodu = Request.Method.PATCH;
        } else {
            methodu = Request.Method.POST;
        }
        validateEventDetails();
        // Crear el objeto JSON con los detalles del evento
        JSONObject eventDetails = new JSONObject();
        try {
            eventDetails.put("title", eventName.getText().toString());
            eventDetails.put("street", eventLocation.getText().toString());
            eventDetails.put("province", eventProvince.getSelectedItem().toString());
            eventDetails.put("music_genre", eventMusicGenre.getSelectedItem().toString());
            eventDetails.put("price", eventPrice.getText().toString());
            eventDetails.put("date", eventDate.getText().toString());
            eventDetails.put("time", eventTime.getText().toString());
            eventDetails.put("link", eventLink.getText().toString());
            eventDetails.put("image", eventImage.getText().toString());
            eventDetails.put("description", eventDescription.getText().toString());
            if (sk.isChecked() && !secretkey.getText().toString().isEmpty()) {
                eventDetails.put("secretkey", secretkey.getText().toString());
            }else{
                eventDetails.put("secretkey", "null");
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        JsonObjectRequestWithAuthentication request = new JsonObjectRequestWithAuthentication(
                methodu,
                url,
                eventDetails,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Manejo de la respuesta exitosa
                        if (method == 1) {
                            Toast.makeText(EditEventActivity.this, "Evento editado con éxito", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(EditEventActivity.this, "Evento creado con éxito", Toast.LENGTH_LONG).show();
                        }
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse == null) {
                            Toast.makeText(EditEventActivity.this, "La conexión no se ha establecido", Toast.LENGTH_LONG).show();
                        } else {
                            int serverCode = error.networkResponse.statusCode;
                            Toast.makeText(EditEventActivity.this, "Estado de respuesta " + serverCode, Toast.LENGTH_LONG).show();
                        }
                        error.printStackTrace();
                    }
                },
                this
        );

        // Añadir la solicitud a la cola de solicitudes
        this.requestQueue.add(request);
    }
    private boolean validateEventDetails() {
        if (eventName.getText().toString().isEmpty() ||
                eventLocation.getText().toString().isEmpty() ||
                eventPrice.getText().toString().isEmpty() ||
                eventDate.getText().toString().isEmpty() ||
                eventTime.getText().toString().isEmpty() ||
                eventLink.getText().toString().isEmpty() ||
                eventImage.getText().toString().isEmpty() ||
                eventDescription.getText().toString().isEmpty()) {
            Toast.makeText(EditEventActivity.this, "Por favor, rellene todos los campos", Toast.LENGTH_LONG).show();
            return false;
        }
        if (sk.isChecked() && secretkey.getText().toString().isEmpty()) {
            Toast.makeText(EditEventActivity.this, "Por favor, rellene todos los campos", Toast.LENGTH_LONG).show();
            return false;
        }
        if (eventProvince.getSelectedItemPosition() == 0){
            Toast.makeText(EditEventActivity.this, "Por favor, seleccione una provincia", Toast.LENGTH_LONG).show();
            return false;
        }
        if (eventMusicGenre.getSelectedItemPosition() == 0){
            Toast.makeText(EditEventActivity.this, "Por favor, seleccione un genero de musica", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}