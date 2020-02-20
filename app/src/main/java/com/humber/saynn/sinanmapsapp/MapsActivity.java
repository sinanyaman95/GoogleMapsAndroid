package com.humber.saynn.sinanmapsapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    LatLng lastLatLng = null;
    Marker oldMarker = null;
    ArrayList<Marker> oldMarkers = new ArrayList<>();
    PlacesClient placesClient;
    private Weather weatherData = null;

    private static final int AUTOCOMPLETE_REQUEST_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Initialize the SDK
        Places.initialize(getApplicationContext(), getApplicationContext().getString(R.string.api_key));

        // Create a new Places client instance
        placesClient = Places.createClient(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Get fused location for device location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        //Change background of fragment
        autocompleteFragment.getView().setBackgroundColor(Color.WHITE);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.LAT_LNG, Place.Field.ADDRESS_COMPONENTS));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                // Set the fields to specify which types of place data to
                // return after the user has made a selection.
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                        Place.Field.LAT_LNG, Place.Field.ADDRESS_COMPONENTS);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY, fields)
                        .build(MapsActivity.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
            }
        });

        mapFragment.getMapAsync(this);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
        if (resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            LatLng latLng = place.getLatLng();
            goToLocation(place,place.getName(),latLng);


        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            // TODO: Handle the error.
            Status status = Autocomplete.getStatusFromIntent(data);
        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
        }
        //}
    }


    private boolean getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    lastLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                }
            }
        });

        return lastLatLng != null;
    }

    private void findCurrentPlace(LatLng latLng) {
        /*FindCurrentPlaceRequest findCurrentPlaceRequest = FindCurrentPlaceRequest
                .newInstance(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS_COMPONENTS));

        Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(findCurrentPlaceRequest);
        placeResponse.addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                FindCurrentPlaceResponse response = task.getResult();
                for(PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()){
                    Log.v("sy", String.format("Place '%s' has likelihood: %f",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood()));
                }
            }else{
                Toast.makeText(getApplicationContext(),"Error occured",Toast.LENGTH_SHORT).show();
            }
        });*/

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            Address obj = addresses.get(0);
            String addressLine = obj.getAddressLine(0);
            goToLocation(null,addressLine,latLng);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        private void setMapListeners(){
        mMap.setInfoWindowAdapter(new CustomInfoWindow(this));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                findCurrentPlace(latLng);
                //goToLocation(null,"Location",latLng);
            }
        });

        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnInfoWindowClickListener(this);

        mMap.setPadding(0,180,0,0);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setMapListeners();
        // Add a marker in Sydney and move the camera
        if(lastLatLng != null){
            goToLocation(null,"Your Location",lastLatLng);
        } else {
            LatLng sidney = new LatLng(-33.8, 151);
            goToLocation(null,"Marker in Sidney",sidney);
        }
    }

    private void resetMarker(){
        if(oldMarker != null){
            oldMarker.setVisible(true);
            oldMarker = null;
            removeOldMarkers();
        }
    }

    private void removeOldMarkers() {
        for(Marker m : oldMarkers){
            m.remove();
        }
    }

    private Marker goToLocation(Place place, String title, LatLng position){
        getWeatherData(position);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(title);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        markerOptions.position(position);
        if(place != null){
            markerOptions.snippet(place.getAddress());
        }
        Marker m = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position,5));
        resetMarker();
        m.showInfoWindow();
        oldMarkers.add(m);
        oldMarker = m;
        return m;
    }

    private void getWeatherData(LatLng latlng){
        String lat = latlng.latitude+"";
        String lon = latlng.longitude+"";

        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=f67cd720fbcdb97f7aa61d7da7eefcb1&units=metric";
        //TODO add &units=metric
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject main = response.getJSONObject("main");
                            double temperature = main.getDouble("temp");
                            double feelsLike = main.getDouble("feels_like");
                            JSONArray weather = response.getJSONArray("weather");
                            for(int i=0; i<weather.length(); i++){
                                JSONObject object = weather.getJSONObject(i);
                                String description = object.getString("description");
                                String iconURL = object.getString("icon");
                                Toast.makeText(getApplicationContext(),description,Toast.LENGTH_SHORT).show();
                                //Picasso.get().load("https://openweathermap.org/img/wn/"+iconURL+"@2x.png").into(imageView);
                                weatherData = new Weather(temperature,feelsLike,description,iconURL);
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
    }

    @Override
    public boolean onMyLocationButtonClick() {
        if(lastLatLng != null){
            findCurrentPlace(lastLatLng);
        }
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if(weatherData != null) Toast.makeText(getApplicationContext(),weatherData.getTemperature()+"",Toast.LENGTH_SHORT).show();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(marker.getTitle())
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        goToLocation(null,oldMarkers.get(oldMarkers.size()-1).getTitle(),
                                oldMarkers.get(oldMarkers.size()-1).getPosition());
                    }
                })
                .create()
                .show();

    }
}
