package com.humber.saynn.sinanmapsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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


    CardView forecastCard;
    LinearLayout forecastLayout;
    ImageView forecastImage;
    TextView forecastDescription;
    TextView forecastTemperature;
    TextView forecastFeels;
    TextView forecastTime;
    ArrayList<Weather> weatherList = new ArrayList<>();
    ForecastAdapter forecastAdapter;
    RecyclerView forecastRecycler;
    private String dt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_weather_window);

        forecastRecycler = findViewById(R.id.forecastRecycler);

        Intent intent = getIntent();
        double lat = intent.getDoubleExtra("lat", 35.0);
        double lon = intent.getDoubleExtra("lon", -131.9);
        LatLng latLng = new LatLng(lat, lon);
        getForecastData(latLng);


    }

    private void showData() {
        Log.d("sy", "Size inside onCreate" + weatherList.size());

        if (weatherList.size() != 0) {
            forecastAdapter = new ForecastAdapter(this, weatherList);
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

    private void getForecastData(LatLng latlng) {
        String lat = latlng.latitude + "";
        String lon = latlng.longitude + "";

        String url = "https://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon + "&appid=f67cd720fbcdb97f7aa61d7da7eefcb1&units=metric";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<Weather> tempList = new ArrayList<>();
                        try {
                            JSONArray list = response.getJSONArray("list");
                            for (int i = 0; i < list.length(); i++) {
                                JSONObject weatherData = list.getJSONObject(i);
                                JSONObject main = weatherData.getJSONObject("main");
                                String temp = main.getString("temp");
                                String feelsLike = main.getString("feels_like");
                                JSONArray weather = weatherData.getJSONArray("weather");
                                JSONObject weatherObject = weather.getJSONObject(0);
                                String description = weatherObject.getString("description");
                                String iconURL = weatherObject.getString("icon");
                                dt = weatherData.getString("dt_txt");
                                Weather w = new Weather(Double.valueOf(temp), Double.valueOf(feelsLike), description, iconURL);
                                tempList.add(w);
                            }

                            weatherList = tempList;
                            Log.d("sy", "Size inside response: " + weatherList.size());
                            showData();
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
    }
}
