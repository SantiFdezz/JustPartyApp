package com.example.jparty.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.jparty.JsonArrayRequestWithAuthentication;
import com.example.jparty.JsonObjectRequestWithAuthentication;
import com.example.jparty.R;
import com.example.jparty.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DetailFragment extends Fragment {

    private RequestQueue requestQueue;
    private ProgressBar pb1;
    private TextView eventName, eventLocation, organizedBy, description, assistants, price;
    private ImageView eventImage, userLiked, userAssist;
    private ImageButton userLikedBttn, backBttn;
    private LinearLayout userAssistBttn;
    private String eventId;
    private boolean suserAssist, suserLiked;
    private int assistantss;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detailscreen, container, false);
        eventName = view.findViewById(R.id.event_name);
        eventLocation = view.findViewById(R.id.event_location);
        organizedBy = view.findViewById(R.id.organized_by);
        description = view.findViewById(R.id.description);
        assistants = view.findViewById(R.id.assistants);
        price = view.findViewById(R.id.price);
        eventImage = view.findViewById(R.id.event_image);
        userLiked = view.findViewById(R.id.like_icon);
        userLikedBttn = view.findViewById(R.id.like_button);
        userAssistBttn = view.findViewById(R.id.assist_button);
        userAssist = view.findViewById(R.id.assist_icon);
        backBttn = view.findViewById(R.id.back_button);
        pb1 = view.findViewById(R.id.loadingScreen);
        Bundle bundle = getArguments();
        this.eventId = bundle.getString("event_id");

        if (bundle != null) {
            this.requestQueue = Volley.newRequestQueue(getContext());
            JsonArrayRequestWithAuthentication request = new JsonArrayRequestWithAuthentication(
                            Request.Method.GET,"/event" + eventId,null,
                            new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    pb1.setVisibility(View.GONE);
                                    for (int i = 0; i < response.length(); i++) {
                                        try {
                                            JSONObject eventObject = response.getJSONObject(i);
                                            String manager = eventObject.getString("manager");
                                            String title = eventObject.getString("title");
                                            String street = eventObject.getString("street");
                                            String province = eventObject.getString("province");
                                            String music_genre = eventObject.getString("music_genre");
                                            String priceValue = eventObject.getString("price");
                                            String secretkey = eventObject.getString("secretkey");
                                            String link = eventObject.getString("link");
                                            String date = eventObject.getString("date");
                                            String time = eventObject.getString("time");
                                            String image = eventObject.getString("image");
                                            String descriptionValue = eventObject.getString("description");
                                            boolean userLikeds = eventObject.getBoolean("userLiked");
                                            boolean userAssists = eventObject.getBoolean("userAssist");
                                            int assistantsValue = eventObject.getInt("assistants");

                                            setUserLiked(userLikeds);
                                            setUserAssist(userAssists);
                                            eventName.setText(title);
                                            eventLocation.setText(street + ", " + province);
                                            organizedBy.setText("Organizado Por: " + manager + " | " + date + "  " + time);
                                            description.setText(descriptionValue);
                                            assistants.setText(assistantsValue + " personas Actualmente asistirán!");
                                            price.setText(priceValue + "€");
                                            try {
                                                Util.downloadBitmapToImageView(image, eventImage);
                                            } catch (Exception e) {
                                                eventImage.setImageResource(R.drawable.ic_launcher_background); // Reemplaza 'default_image' con tu imagen predeterminada
                                            }
                                            userLiked.setImageResource((userLikeds ? R.drawable.like_selected : R.drawable.like_unselected));

                                            userAssist.setImageResource((userAssists ? R.drawable.balloon_selected : R.drawable.balloon_unselected));

                                            userAssistBttn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    boolean isAssisted = getUserAssist();
                                                    Integer assistantsValue = getAssistants();
                                                    String url = Server.name + "/user/assistevent" + eventId;
                                                    int method = isAssisted ? Request.Method.DELETE : Request.Method.POST;
                                                    JsonObjectRequestWithAuthentication request = new JsonObjectRequestWithAuthentication(
                                                            method, url, null,
                                                            new Response.Listener<JSONObject>() {
                                                                @Override
                                                                public void onResponse(JSONObject response) {
                                                                    setUserAssist(!isAssisted);
                                                                    int assistantsValues = !isAssisted ? (assistantsValue + 1) : (assistantsValue - 1);
                                                                    if (assistantsValues < 0) {
                                                                        assistantsValues = 0;
                                                                    }
                                                                    setAssistants(assistantsValues);
                                                                    assistants.setText(assistantsValues + " personas Actualmente asistirán!");
                                                                    userAssist.setImageResource((!isAssisted ? R.drawable.balloon_selected : R.drawable.balloon_unselected));
                                                                    userAssist.invalidate(); // Forzar a la vista a dibujarse de nuevo
                                                                    assistants.invalidate();
                                                                }
                                                            },
                                                            new Response.ErrorListener() {
                                                                @Override
                                                                public void onErrorResponse(VolleyError error) {
                                                                    pb1.setVisibility(View.GONE);
                                                                    if (error.networkResponse == null) {
                                                                        Toast.makeText(getContext(), "La conexión no se ha establecido", Toast.LENGTH_LONG).show();
                                                                    } else {
                                                                        int serverCode = error.networkResponse.statusCode;
                                                                        Toast.makeText(getContext(), "Estado de respuesta " + serverCode, Toast.LENGTH_LONG).show();
                                                                    }
                                                                    error.printStackTrace();
                                                                }
                                                            },
                                                            getContext()
                                                    );
                                                    requestQueue.add(request);
                                                }
                                            });
                                            userLikedBttn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    boolean isLiked = getUserLiked();

                                                    int method = isLiked ? Request.Method.DELETE : Request.Method.POST;
                                                    JsonObjectRequestWithAuthentication request = new JsonObjectRequestWithAuthentication(
                                                            method, "/user/likedevent" + eventId, null,
                                                            new Response.Listener<JSONObject>() {
                                                                @Override
                                                                public void onResponse(JSONObject response) {
                                                                    setUserLiked(!isLiked);

                                                                    userLiked.setImageResource((getUserLiked() ? R.drawable.like_selected : R.drawable.like_unselected));
                                                                    userLiked.invalidate(); // Forzar a la vista a dibujarse de nuevo

                                                                }
                                                            },
                                                            new Response.ErrorListener() {
                                                                @Override
                                                                public void onErrorResponse(VolleyError error) {
                                                                    pb1.setVisibility(View.GONE);
                                                                    if (error.networkResponse == null) {
                                                                        Toast.makeText(getContext(), "La conexión no se ha establecido", Toast.LENGTH_LONG).show();
                                                                    } else {
                                                                        int serverCode = error.networkResponse.statusCode;
                                                                        Toast.makeText(getContext(), "Estado de respuesta " + serverCode, Toast.LENGTH_LONG).show();
                                                                    }
                                                                    error.printStackTrace();
                                                                }
                                                            },
                                                            getContext()
                                                    );
                                                    requestQueue.add(request);
                                                }
                                            });
                                            backBttn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    getActivity().onBackPressed();
                                                    getActivity().finish();
                                                }
                                            });
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pb1.setVisibility(View.GONE); // Alternamos entre la visibilidad de la barra de progresión a nuestra conveniencia.
                            pb1.setVisibility(View.GONE);
                            if (error.networkResponse == null) {
                                Toast.makeText(getContext(), "La conexión no se ha establecido", Toast.LENGTH_LONG).show();
                            } else {
                                int serverCode = error.networkResponse.statusCode;
                                Toast.makeText(getContext(), "Estado de respuesta " + serverCode, Toast.LENGTH_LONG).show();
                            }
                            error.printStackTrace();
                        }
                    }, getContext());
            this.requestQueue.add(request);
        }


        return view;
    }

    public void setUserAssist(boolean suserAssist) {
        this.suserAssist = suserAssist;
    }

    public void setUserLiked(boolean suserLiked) {
        this.suserLiked = suserLiked;
    }

    public Boolean getUserAssist() {
        return suserAssist;
    }

    public Boolean getUserLiked() {
        return suserLiked;
    }

    public void setAssistants(int assistantss) {
        this.assistantss = assistantss;
    }

    public int getAssistants() {
        return assistantss;
    }
}