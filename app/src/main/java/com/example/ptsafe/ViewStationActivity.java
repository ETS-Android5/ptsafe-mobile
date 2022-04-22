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
                    mMap.addMarker(new MarkerOptions().position(currLocation).title("Your location"));
                    String destLocation = destSearchEt.getText().toString();
                    dest = getLocationFromAddress(destLocation);
                    mMap.addMarker(new MarkerOptions().position(dest).title("Your destination").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                    //todo: show toast asking the user to walk instead of showing nearest stops if the destination is too near
                    getAllNearestStopsToCurrentLocation(currLocation);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dest, 10f));

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
                return false;
            }
        });
    }
    
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        currLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currLocation).title("Your location"));
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
        bundle.putDouble("latitude", dest.latitude);
        bundle.putDouble("longitude", dest.longitude);
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
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    mapFragment.getMapAsync(ViewStationActivity.this);
                }
            }
        });
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
            Address location = address.get(0);
            p1 =  new LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return p1;
    }

    private void addStationsMarkerToHashMap (List<NearestStops> stopsData) {
        for (NearestStops stop: stopsData) {
            LatLng coordinate = new LatLng(stop.getStopLat(), stop.getStopLong());
            Marker marker = mMap.addMarker(new MarkerOptions().
                    position(coordinate).title(stop.getStopName())
                    .snippet("Passengers: " + stop.getPaxWeekday() + ", Police stations: " + stop.getTotalPoliceStations())
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            stationMarkers.put(marker.getId(), stop.getStopId());
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
                        int paxWeekday = obj.getInt("pax_weekday");
                        int totalPoliceStation = obj.getInt("total_police_station");
                        float distanceInKm = (float) obj.getDouble("distance_in_km");
                        NearestStops newStop = new NearestStops(stopId, stopName, stopLat, stopLong, paxWeekday, totalPoliceStation, distanceInKm);
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