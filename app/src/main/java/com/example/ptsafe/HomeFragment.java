package com.example.ptsafe;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ptsafe.R;
import com.example.ptsafe.model.NearestStops;
import com.example.ptsafe.model.StopByRouteId;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class HomeFragment extends Fragment {

    private TextView stationNameTv;
    private TextView stationAddressTv;
    private TextView nearestDistanceTv;
    private Button addTripBtn;
    private Button reportCrowdednessBtn;
    private Button meditationBtn;
    private Button newsBtn;
    private Button emergencyBtn;
    Location currentLocation;
    private NearestStops nearestStop;
    private static final int REQUEST_CODE = 101;
    FusedLocationProviderClient fusedLocationProviderClient;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the View for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        initVars();
        initView(view);
        getCurrentLocation();
        reportCrowdednessBtn.setOnClickListener(setMenuBtn("crowd"));
        addTripBtn.setOnClickListener(setMenuBtn("trip"));
        meditationBtn.setOnClickListener(setMenuBtn("meditation"));
        newsBtn.setOnClickListener(setMenuBtn("news"));
        emergencyBtn.setOnClickListener(setMenuBtn("emergency"));
        return view;
    }


    private View.OnClickListener setMenuBtn(String menu) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                switch (menu) {
                    case "crowd":
                        CrowdingDetectFragment crowdingName = new CrowdingDetectFragment();
                        fragmentTransaction.replace(R.id.content_frame, crowdingName);
                        fragmentTransaction.commit(); break;
                    case "trip":
                        FindStationFragment findStationName = new FindStationFragment();
                        fragmentTransaction.replace(R.id.content_frame, findStationName);
                        fragmentTransaction.commit(); break;
                    case "meditation":
                        MeditationFragment meditationFragment = new MeditationFragment();
                        fragmentTransaction.replace(R.id.content_frame, meditationFragment);
                        fragmentTransaction.commit(); break;
                    case "news":
                        NewsFragment newsFragment = new NewsFragment();
                        fragmentTransaction.replace(R.id.content_frame, newsFragment);
                        fragmentTransaction.commit(); break;
                    case "emergency":
                        EmergencyFragment emergencyFragment = new EmergencyFragment();
                        fragmentTransaction.replace(R.id.content_frame, emergencyFragment);
                        fragmentTransaction.commit(); break;
                    case "user manual":
                        Intent intent = new Intent(getActivity(), FirstScreen.class);
                        startActivity(intent); break;
                }

            }
        };
    }

    private void initVars() {
        nearestStop = new NearestStops();
    }

    private void initView(View view) {
        stationNameTv = view.findViewById(R.id.station_name_tv);
        stationAddressTv = view.findViewById(R.id.station_address_tv);
        nearestDistanceTv = view.findViewById(R.id.nearest_distance_tv);
        addTripBtn = view.findViewById(R.id.add_trip_onboarding_btn);
        reportCrowdednessBtn = view.findViewById(R.id.crowdedness_onboarding_btn);
        meditationBtn = view.findViewById(R.id.meditation_onboarding_btn);
        newsBtn = view.findViewById(R.id.news_onboarding_btn);
        emergencyBtn = view.findViewById(R.id.emergency_onboarding_btn);
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    //todo: remove this debug log when the expo ends
                    Log.i("latitude", String.valueOf(location.getLatitude()));
                    Log.i("longitude", String.valueOf(location.getLongitude()));
                    getAllNearestStopsToCurrentLocation(currentLocation);
                }
            }
        });
    }

    private String getAddressFromCoordinate(double latitude, double longitude) throws IOException {
        Geocoder coder = new Geocoder(this.getContext());
        List<Address> addresses;

        addresses = coder.getFromLocation(latitude, longitude, 1);

        String stopAddress = addresses.get(0).getAddressLine(0);
        return stopAddress;
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

    private void getAllNearestStopsToCurrentLocation(Location currLocation){
        OkHttpClient client = new OkHttpClient();
        String url = "http://ptsafenodejsapi-env.eba-cx9pgkwu.us-east-1.elasticbeanstalk.com/v1/report/findNearestStopsByCurrLocation?lat=" + currLocation.getLatitude() + "&long=" + currLocation.getLongitude();
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
                        float crimeRateIndex = (float) obj.getDouble("crime_rate_index");
                        float distanceInKm = (float) obj.getDouble("distance_in_km");
                        NearestStops newStop = new NearestStops(stopId, stopName, stopLat, stopLong, crowdednessDensity, totalPoliceStation, crimeRateIndex, distanceInKm);
                        nearestStopsData.add(newStop);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Australia/Sydney"));
                Date currentLocalTime = cal.getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                try {
                    Log.d("currentDate", String.valueOf(dateFormat.parse(dateFormat.format(currentLocalTime)).after(dateFormat.parse("18:00:00"))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                try {
                    if(dateFormat.parse(dateFormat.format(currentLocalTime)).after(dateFormat.parse("18:00:00")) || dateFormat.parse(dateFormat.format(currentLocalTime)).before(dateFormat.parse("07:00:00")))
                    {
                        nearestStop = nearestStopsData.get(1);
                    }else{
                        nearestStop = nearestStopsData.get(0);
                    }
                } catch (ParseException e) {
                    nearestStop = nearestStopsData.get(0);
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stationNameTv.setText(nearestStop.getStopName());
                        try {
                            stationAddressTv.setText(getAddressFromCoordinate(nearestStop.getStopLat(), nearestStop.getStopLong()));
                        } catch (IOException e) {
                            Toast.makeText(getContext(), "Cannot find the address!", Toast.LENGTH_SHORT).show();
                        }
                        nearestDistanceTv.setText(String.format("%.02f",distance(currLocation.getLatitude(), currLocation.getLongitude(), nearestStop.getStopLat(), nearestStop.getStopLong())) + " km away");
                    }
                });
            }
        });
    };
}