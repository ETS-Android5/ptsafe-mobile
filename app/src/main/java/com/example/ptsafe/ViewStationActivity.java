package com.example.ptsafe;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ptsafe.model.NearestStops;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.ptsafe.databinding.ActivityViewStationBinding;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ViewStationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText destSearchEt;
    private Spinner inOutSpinner;
    private ActivityViewStationBinding binding;
    Location currentLocation;
    private LatLng currLocation;
    private LatLng dest;
    private List<NearestStops> nearestStops;
    private String currentAddress;
    private HashMap<String, Integer> stationMarkers;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewStationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        initVar();
        initView();
        getCurrentLocation();

        destSearchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(currLocation).title("Your location").icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                    String destLocation = destSearchEt.getText().toString();
                    dest = getLocationFromAddress(destLocation);
                    if (dest == null) {
                        Toast.makeText(getApplicationContext(), "Cannot find the destination, please provide a complete address!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        mMap.addMarker(new MarkerOptions().position(dest).title("Your destination").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dest, 10f));

                        //todo: show toast asking the user to walk instead of showing nearest stops if the destination is too near
                        if (distance(currLocation.latitude, currLocation.longitude, dest.latitude, dest.longitude) < 1.5) {
                            Toast.makeText(getApplicationContext(), "Your destination is within walking distance", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            getAllNearestStopsToCurrentLocation(currLocation);
                        }
                        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onInfoWindowClick(@NonNull Marker marker) {

                                if(stationMarkers.containsKey(marker.getId())) {
                                    int stopId = stationMarkers.get(marker.getId());
                                    goToListTrainPage(stopId);
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Cannot select markers that are not nearest stops", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        drawPolyline(mMap, currLocation, dest);
                        return true;
                    }
                }
                return false;
            }
        });
    }
    
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        currLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currLocation).title("Your location").icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLocation, 15f));
    }

    private int getSpinnerItemNumber(Spinner spinner) {
        String text = spinner.getSelectedItem().toString();
        if (text.equals("outbound")) {
            return 0;
        }
        return 1;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void goToListTrainPage (int stopId) {
        Optional<NearestStops> object = nearestStops.stream().
                filter(p -> p.getStopId() == stopId).
                findFirst();
        NearestStops nearestStops = object.get();
        Intent intent = new Intent(ViewStationActivity.this, ListTrainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("stopId", stopId);
        bundle.putInt("destinationType", getSpinnerItemNumber(inOutSpinner));
        bundle.putDouble("stopLatitude", nearestStops.getStopLat());
        bundle.putDouble("stopLongitude", nearestStops.getStopLong());
        bundle.putDouble("latitude", dest.latitude);
        bundle.putDouble("longitude", dest.longitude);
        bundle.putString("currentAddress", currentAddress);
        bundle.putString("destinationAddress", destSearchEt.getText().toString());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
//                    currentLocation = new Location(LocationManager.GPS_PROVIDER);
//                    currentLocation.setLatitude(-37.907803);
//                    currentLocation.setLongitude(145.133957);
                    try {
                        getAddressFromCoordinate(currentLocation);
                    } catch (IOException e) {
                        currentAddress = "unknown";
                    }
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    mapFragment.getMapAsync(ViewStationActivity.this);
                }
            }
        });
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 0.8684;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private void getAddressFromCoordinate(Location currLocation) throws IOException {
        Geocoder coder = new Geocoder(this);
        List<Address> addresses;

        addresses = coder.getFromLocation(currLocation.getLatitude(), currLocation.getLongitude(), 1);

        currentAddress = addresses.get(0).getAddressLine(0);
//        String city = addresses.get(0).getLocality();
//        String state = addresses.get(0).getAdminArea();
//        String country = addresses.get(0).getCountryName();
//        String postalCode = addresses.get(0).getPostalCode();
//        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
    }

    private LatLng getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            else if (address.size() == 0) {
                return null;
            }
            else {
                Address location = address.get(0);
                p1 =  new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (Exception ex) {
            return null;
        }
        return p1;
    }

    private void addStationsMarkerToHashMap (List<NearestStops> stopsData) {
        int index = 0;
        for (NearestStops stop: stopsData) {
            LatLng coordinate = new LatLng(stop.getStopLat(), stop.getStopLong());
            BitmapDescriptor markerColor = BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
            if (index != 0) {
                markerColor = BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
            }
            Marker marker = mMap.addMarker(new MarkerOptions().
                    position(coordinate).title(stop.getStopName())
                    .snippet("Crowdedness per platform: " + stop.getCrowdednessDensity() + ", Police stations: " + stop.getTotalPoliceStations())
                    .icon(markerColor));
            stationMarkers.put(marker.getId(), stop.getStopId());
            index++;
        }
    }

    //get nearest stations based on the current location by implementing okhttp
    private void getAllNearestStopsToCurrentLocation(LatLng currLocation){
        OkHttpClient client = new OkHttpClient();
        String url = "http://ptsafenodejsapi-env.eba-cx9pgkwu.us-east-1.elasticbeanstalk.com/v1/report/findNearestStopsByCurrLocation?lat=" + currLocation.latitude + "&long=" + currLocation.longitude;
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                JSONObject resultObj = null;
                List<NearestStops> nearestStopsData = new ArrayList<>();
                try {
                    resultObj = new JSONObject(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray data = null;
                try {
                    data = resultObj.getJSONArray("message");
                    Log.d("JSONArrayLength", String.valueOf(data.length()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for(int i = 0; i < data.length(); i++) {
                    JSONObject obj = null;
                    try {
                        obj = data.getJSONObject(i);
                        int stopId = obj.getInt("stop_id");
                        String stopName = obj.getString("stop_name");
                        float stopLat = (float) obj.getDouble("stop_lat");
                        float stopLong = (float) obj.getDouble("stop_lon");
                        float crowdednessDensity = (float) obj.getDouble("crowdedness_density");
                        int totalPoliceStation = obj.getInt("total_police_station");
                        float distanceInKm = (float) obj.getDouble("distance_in_km");
                        NearestStops newStop = new NearestStops(stopId, stopName, stopLat, stopLong, crowdednessDensity, totalPoliceStation, distanceInKm);
                        nearestStops.add(newStop);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addStationsMarkerToHashMap(nearestStops);
                    }
                });
            }
        });
    };

    private void drawPolyline(GoogleMap map, LatLng currLocation, LatLng dest) {
        map.addPolyline((new PolylineOptions()).add(currLocation, dest).
                        width(3)
                .color(Color.RED)
                .geodesic(true));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (REQUEST_CODE) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                }
                break;
        }
    }

    private void initView() {
        destSearchEt = findViewById(R.id.destination_search_et);
        inOutSpinner = findViewById(R.id.in_out_spinner);
    }

    private void initVar() {
        stationMarkers = new HashMap<>();
        nearestStops = new ArrayList<>();
    }
}