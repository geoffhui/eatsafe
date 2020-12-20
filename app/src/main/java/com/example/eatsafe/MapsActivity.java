package com.example.eatsafe;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity
        implements
        OnMapReadyCallback{


    private GoogleMap mMap;

    private static final String TAG = "MapsActivity";

    private FusedLocationProviderClient mFusedLocationProviderClient;


    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean mLocationPermissionGranted = false;

    String name;
    String date;
    String inspType;
    int numCritical;
    int numNonCritical;
    String hazardRating;
    String violLump;
    String address;

    itemModel item;
    JSONObject jsonObj;
    Marker marker;
    HashMap<Marker, itemModel> hashmap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getLocationPermission();
    }



    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: Getting device location.");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionGranted){
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful() && (task.getResult() != null )){
                            Log.d(TAG, "onComplete: Current location found.");
                            Location currentLocation = (Location) task.getResult();
                            moveCamera((new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())), 15);

                        } else {
                            moveCamera(new LatLng(49.0800, -122.795), 15);
                            Log.d(TAG, "onComplete: Current location not found.");
                            Log.d(TAG, "getLastLocation:exception::" + task.getException());
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }
    

    // move the camera to current location
    private void moveCamera (LatLng latlng, float zoom) {
        Log.d(TAG, "moveCamera: moving camera to current location: " + latlng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
//        Toast.makeText(this, "Map is ready!", Toast.LENGTH_LONG).show();
        mMap = googleMap;
        hashmap = new HashMap<Marker, itemModel>();
        if (mLocationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
//            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

        try {
            JSONObject object = new JSONObject(readJSON());
            JSONArray array = object.getJSONArray("data");
            jsonObj = array.getJSONObject(0);
            double lat0 = array.getJSONObject(0).getDouble("LATITUDE");
            double lng0 = array.getJSONObject(0).getDouble("LONGITUDE");

            String snippet0 = algorithmData(array.getJSONObject(0));


            marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat0, lng0))
                    .title(array.getJSONObject(0).getString("NAME"))
                    .snippet(snippet0));
            name = jsonObj.getString("NAME");
            date = jsonObj.getString("InspectionDate");
            inspType = jsonObj.getString("InspType");
            numCritical = jsonObj.getInt("NumCritical");
            numNonCritical = jsonObj.getInt("NumNonCritical");
            hazardRating = jsonObj.getString("HazardRating");
            violLump = jsonObj.getString("ViolLump");
            address = jsonObj.getString("PHYSICALADDRESS");

            item = new itemModel(name, date, inspType, numCritical, numNonCritical, hazardRating, violLump, address);
            System.out.println("BuffBaby" + marker);
            hashmap.put(marker, item);

            for (int i = 1; i < array.length(); i++) {
                jsonObj = array.getJSONObject(i);
                String prev_address = array.getJSONObject(i-1).getString("NAME");
                String curr_address = jsonObj.getString("NAME");

                if (!(curr_address.equalsIgnoreCase(prev_address))) {
                    String snippet = algorithmData(array.getJSONObject(i));

                    double tmp_lat = jsonObj.getDouble("LATITUDE");
                    double tmp_lng = jsonObj.getDouble("LONGITUDE");

                    marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(tmp_lat, tmp_lng))
                            .title(jsonObj.getString("NAME"))
                            .snippet(snippet));
                    name = jsonObj.getString("NAME");
                    date = jsonObj.getString("InspectionDate");
                    inspType = jsonObj.getString("InspType");
                    numCritical = jsonObj.getInt("NumCritical");
                    numNonCritical = jsonObj.getInt("NumNonCritical");
                    hazardRating = jsonObj.getString("HazardRating");
                    violLump = jsonObj.getString("ViolLump");
                    address = jsonObj.getString("PHYSICALADDRESS");

                    item = new itemModel(name, date, inspType, numCritical, numNonCritical, hazardRating, violLump, address);
                    hashmap.put(marker, item);
                    mMap.setInfoWindowAdapter(new CustomMapInfoWindowAdapter(MapsActivity.this));
                    mMap.setOnInfoWindowClickListener(
                            new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            Intent intent = new Intent(MapsActivity.this, RestaurantDetailsActivity.class);
                            itemModel select = hashmap.get(marker);
                            intent.putExtra("name", select.getTitle());
                            intent.putExtra("date", select.getDate());
                            intent.putExtra("inspection", select.getInspType());
                            intent.putExtra("critical", select.getNumCritical());
                            intent.putExtra("noncritical", select.getNumNonCritical());
                            intent.putExtra("hazard", select.getHazardRating());
                            intent.putExtra("violation", select.getViolLump());
                            intent.putExtra("address", select.getAddress());
                            startActivity(intent);
                        }
                    });
                }
            }


        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        mMap.setInfoWindowAdapter(new CustomMapInfoWindowAdapter(MapsActivity.this));

    }


    public String algorithmData(JSONObject jsonObject){
        String s = null;
        try {
            s = jsonObject.getString("InspectionDate");
            s = "Date of last inspection: " +
                    s.substring(0, 4) + "-" +
                    s.substring(4, 6) + "-" +
                    s.substring(6, 8) + "\n" +
                    "Hazard Rating: " + jsonObject.getString("HazardRating") + "\n" +
                    "Critical Infractions: " + jsonObject.getString("NumCritical") + "\n" +
                    "Non-Critical Infractions: " + jsonObject.getString("NumNonCritical");
        } catch (JSONException e) {
            Log.e(TAG, "onClick: JSONException: " + e.getMessage());
        }

        return s;
    }

    public String readJSON() {
        String json = null;

        try {
            InputStream inputStream = getAssets().open("inspection_data.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return json;
        }
        return json;
    }

    public void onSearch(View v) {
        List<Address> addressList = null;

        EditText editTextLocation = findViewById(R.id.editTextLocation);
        String location = editTextLocation.getText().toString();

        if (location != null && !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 5);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Address adr = addressList.get(0);
            LatLng latLng = new LatLng(adr.getLatitude(), adr.getLongitude());
            mMap.clear();
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            try {
                JSONObject object = new JSONObject(readJSON());
                JSONArray array = object.getJSONArray("data");

                for (int i = 1; i < array.length(); i++) {
                    JSONObject jsonObj = array.getJSONObject(i);
                    String curr_address_physical = jsonObj.getString("PHYSICALADDRESS");
                    String curr_address_name = jsonObj.getString("NAME");
                    Log.d(TAG, curr_address_physical);
                    Log.d(TAG, curr_address_name);
                    if (location.equalsIgnoreCase(curr_address_physical) ||
                            location.equalsIgnoreCase(curr_address_name)) {
                        String snippet = algorithmData(array.getJSONObject(i));
                        double tmp_lat = jsonObj.getDouble("LATITUDE");
                        double tmp_lng = jsonObj.getDouble("LONGITUDE");
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(tmp_lat, tmp_lng))
                                .title(jsonObj.getString("NAME"))
                                .snippet(snippet));
                        }
                    }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            mMap.setInfoWindowAdapter(new CustomMapInfoWindowAdapter(MapsActivity.this));
        }
    }

    public void onChangeMapType(View v) {
        if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
        else
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    public void onZoom(View v) {
        if (v.getId() == R.id.btnZoomIn)
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
        else
            mMap.animateCamera(CameraUpdateFactory.zoomOut());
    }

    private void getLocationPermission(){
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1234);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, 1234);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantresults){
        mLocationPermissionGranted = false;
        switch(requestCode){
            case 1234:{
                if(grantresults.length > 0){
                    for (int i = 0; i < grantresults.length; i++){
                        if(grantresults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    // initialize map
                    initMap();
                }
            }
        }
    }
}
