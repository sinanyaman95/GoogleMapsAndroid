package com.humber.saynn.sinanmapsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShowWeatherWindow extends AppCompatActivity {

    boolean forecastCalled = false;

    CardView forecastCard;
    LinearLayout forecastLayout;
    ImageView forecastImage;
    TextView forecastDescription;
    TextView forecastTemperature;
    TextView forecastFeels;
    TextView forecastTime;
    ArrayList<Weather> weatherList;
    ForecastAdapter forecastAdapter;
    RecyclerView forecastRecycler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_weather_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int w = dm.widthPixels;
        int h = dm.heightPixels;

        int newWidth = (int) (w*0.8);
        int newHeight = (int) (h*0.9);

        getWindow().setLayout(newWidth,newHeight);

        forecastRecycler = findViewById(R.id.forecastRecycler);

        Intent intent = getIntent();
        double lat = intent.getDoubleExtra("lat",35.0);
        double lon = intent.getDoubleExtra("lon", -131.9);
        LatLng latLng = new LatLng(lat,lon);
        weatherList = new ArrayList<>();
        forecastCalled = getForecastData(latLng);

        if(forecastCalled){
            forecastAdapter = new ForecastAdapter(this,weatherList);
            forecastRecycler.setAdapter(forecastAdapter);
            forecastRecycler.setLayoutManager(new LinearLayoutManager(this));
        }
        //linkInteface();


    }

    private void linkInteface() {

        forecastCard = findViewById(R.id.forecastCard);
        forecastLayout = findViewById(R.id.forecastLayout);
        forecastImage = findViewById(R.id.forecastImage);
        forecastDescription = findViewById(R.id.forecastDescription);
        forecastTemperature = findViewById(R.id.forecastTemperature);
        forecastFeels = findViewById(R.id.forecastFeels);
        forecastTime = findViewById(R.id.forecastTime);

    }

    private boolean getForecastData(LatLng latlng){
        String lat = latlng.latitude+"";
        String lon = latlng.longitude+"";

        String url = "https://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon + "&appid=f67cd720fbcdb97f7aa61d7da7eefcb1&units=metric";
        //TODO add &units=metric
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray list = response.getJSONArray("list");
                            for(int i = 0; i<list.length(); i++){
                                JSONObject weatherData = list.getJSONObject(i);
                                JSONObject main = weatherData.getJSONObject("main");
                                String temp = main.getString("temp");
                                String feelsLike = main.getString("feels_like");
                                JSONArray weather = weatherData.getJSONArray("weather");
                                for(int j = 0; j<weather.length();j++){
                                    JSONObject weatherObject = weather.getJSONObject(i);
                                    String description =weatherObject.getString("description");
                                    String iconURL = weatherObject.getString("icon");
                                    Weather w = new Weather(Double.valueOf(temp),Double.valueOf(feelsLike),description,iconURL);
                                    weatherList.add(w);
                                }
                                String dt = weatherData.getString("dt_text");
                                forecastDescription.setText(dt);
                                Toast.makeText(getApplicationContext(),dt,Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                    }
                });
        // Access the RequestQueue through your singleton class.
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
        return true;
    }
}
