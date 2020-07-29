package com.example.nicolai.clider.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nicolai.clider.R;
import com.example.nicolai.clider.model.MapData.Example;
import com.example.nicolai.clider.model.MapData.Result;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Example example;
    Gson gson = new GsonBuilder().create();
    RequestQueue requestQueue;
    Double mLng;
    Double mLat;
    LocationManager mLocationManager;
    LatLng myLatLng;
    Bitmap bm;
    private FusedLocationProviderClient mFusedProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Checkfailed", "onCreate: ");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        getMyLocation();
        mMap.setMyLocationEnabled(true);
    }

    public Example exampleConverter(String json) {
        return gson.fromJson(json, Example.class);
    }

    private String urlBuilder(String lat, String lng){
        String baseUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
        String suffix = "&radius=600&type=clothing_store&key=AIzaSyBAuFr0SI-riVMb8hPEZK8mXLrnHQGEyQI";
        String newUrl = baseUrl + lat + "," + lng + suffix;
        return newUrl;
    }

    public void sendRequest() {
        if (mLat!= null && mLng != null) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, urlBuilder(mLat.toString(), mLng.toString()), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    example = exampleConverter(response);
                    HashMap<String, LatLng> places = new HashMap<>();
                    ArrayList<Result> results = new ArrayList<>();
                    for (int i = 0; i < example.getResults().size(); i++) {
                        results.add(example.getResults().get(i));
                        places.put(example.getResults().get(i).getName(), new LatLng(example.getResults().get(i).getGeometry().getLocation().getLat(), example.getResults().get(i).getGeometry().getLocation().getLng()));
                    }
                    Toast.makeText(MapsActivity.this, "Succes", Toast.LENGTH_SHORT).show();
                    Log.d("places", "onResponse: " + places.entrySet());
                    Log.d("SUCCES", "onResponse: ");
                    mMap.clear();
                    if (places != null) {
                        for (Result result : results) {
                            try {
                                bm = Ion.with(getApplicationContext()).load(result.getIcon()).asBitmap().get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                            mMap.addMarker(new MarkerOptions().position(new LatLng(result.getGeometry().getLocation().getLat()
                                    , result.getGeometry().getLocation().getLng()))
                                    .title(result.getName()).snippet(result.getVicinity())
                                    .icon(BitmapDescriptorFactory.fromBitmap(bm)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(result.getGeometry().getLocation().getLat(),
                                    result.getGeometry().getLocation().getLng())));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
                        }
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("ERROR", "onErrorResponse: ");
                }
            });
            requestQueue.add(stringRequest);
        }
    }

    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedProvider = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
        try {
            Task location = mFusedProvider.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Location currentLocation = (Location) task.getResult();
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
                        mLng = currentLocation.getLongitude();
                        mLat = currentLocation.getLatitude();
                        sendRequest();
                    }   else {
                        Log.d("mylocation", "onComplete: unsucces");
                    }
                }
            });
        }catch (SecurityException e ){

        }

    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Granted", "onRequestPermissionsResult: ");

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case 2:
                if (grantResults.length>0 &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED);
                // other 'case' lines to check for other
                // permissions this app might request
        }
    }


}
