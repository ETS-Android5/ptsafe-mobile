package com.example.ptsafe;

import static com.example.ptsafe.AddNewsActivity.JSON;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ptsafe.adapter.NewsAdapter;
import com.example.ptsafe.model.DepartureTimesByRouteDirectionStops;
import com.example.ptsafe.model.News;
import com.example.ptsafe.model.PostCrowdedness;
import com.example.ptsafe.model.PostNews;
import com.example.ptsafe.model.RouteByDestinationType;
import com.example.ptsafe.model.StopByRouteId;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CrowdednessReportActivity extends AppCompatActivity {

    private TextView levelOrCrowdednessTv;
    private TextView crowdednessInstructionTv;
    private EditText carriageNumberEt;
    private Spinner directionReportSpinner;
    private AutoCompleteTextView trainNameReportEt;
    private AutoCompleteTextView stopNameReportEt;
//    private Spinner dayReportSpinner;
    private EditText dayReportEt;
    private AutoCompleteTextView timeReportEt;
//    private Spinner carriageNumberSpinner;
    private EditText criminalReportEt;
    private EditText crowdednessLevelEt;
    private Button reportCrowdednessBtn;
    private float crowdednessValue;
    private int destinationType;
    private String routeId;
    private String stopId;
    private List<RouteByDestinationType> routes;
    private List<StopByRouteId> stops;
    Location currentLocation;
    private List<DepartureTimesByRouteDirectionStops> departureTimes;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crowdedness_report);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation();
        initVars();
        initView();
        dayReportEt.setText(getCurrentDayName());
        getDataFromIntent();
        showGuidelines(crowdednessValue);
        directionReportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                destinationType = getDestinationType(directionReportSpinner);
                getAllTrainNamesByDestinationType(destinationType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        trainNameReportEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (trainNameReportEt.getText().toString().trim().length() != 0) {
                        routeId = trainNameReportEt.getText().toString().split(":")[0].trim();
                        getAllStopsByRouteId(routeId);
                    }
                }
            }
        });

        stopNameReportEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (stopNameReportEt.getText().toString().trim().length() != 0) {
                        stopId = stopNameReportEt.getText().toString().split(":")[0].trim();
                        carriageNumberEt.setText(String.valueOf(getCurrentCarriageNumber(getCurrentStop(), currentLocation)));
                        getAllDepartureTimesByDirectionRouteStops(destinationType, routeId, stopId);
                    }
                }
            }
        });

        reportCrowdednessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (trainNameReportEt.getText().toString().trim().length() != 0
                        && stopNameReportEt.getText().toString().trim().length() != 0
                        && timeReportEt.getText().toString().trim().length() != 0
                        && criminalReportEt.getText().toString().trim().length() != 0
                        && crowdednessLevelEt.getText().toString().trim().length() != 0) {
                    String departureTime = timeReportEt.getText().toString();
                    String day = dayReportEt.getText().toString().trim();
//                    String day = dayReportSpinner.getSelectedItem().toString();
                    int carriageNumber = Integer.parseInt(carriageNumberEt.getText().toString());
//                    int carriageNumber = Integer.parseInt(carriageNumberSpinner.getSelectedItem().toString());
                    String criminalActivity = criminalReportEt.getText().toString();
                    createCrowdedness(Integer.parseInt(stopId), routeId, departureTime, destinationType, day , carriageNumber, crowdednessValue, criminalActivity);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please fill all the columns first!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private StopByRouteId getCurrentStop() {
        return stops.stream().filter(item -> item.getStopId() == Integer.parseInt(stopId)).collect(Collectors.toList()).get(0);
    }

    private String getCurrentDayName() {
        String[] dayNames = new DateFormatSymbols().getWeekdays();
        Calendar cal = Calendar.getInstance();
        return dayNames[cal.get(Calendar.DAY_OF_WEEK)].toLowerCase();
    }

    private int getCurrentCarriageNumber(StopByRouteId stop, Location currLocation) {
        double distanceFromFront = distance(stop.getLatitude(), stop.getLongitude(), currLocation.getLatitude(), currLocation.getLongitude()) * 1000;
        if ( distanceFromFront <= 246.5) {
            Log.i("currentCarriage", String.valueOf((int) Math.floor(distanceFromFront / 24.65)));
            return (int) Math.floor(distanceFromFront / 24.65);
        }
        return new Random().nextInt(10 - 1 + 1) + 1;
        //train length = 24.65 * 10 = 246.5 m
        //carriage length = 24.65 m
        //carriage width = 3.04 m
        //source: wikipedia
    }

    //get distance in km
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


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (REQUEST_CODE) {
//            case REQUEST_CODE:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    getCurrentLocation();
//                }
//                break;
//        }
//    }

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
                }
            }
        });
    }

    private void showGuidelines(float crowdednessValue) {
        if (crowdednessValue > 0.8) {
            levelOrCrowdednessTv.setText("High");
            levelOrCrowdednessTv.setTextColor(Color.RED);
            crowdednessInstructionTv.setText(getString(R.string.instruction_content));
        }
        else if (crowdednessValue > 0.5) {
            levelOrCrowdednessTv.setText("Medium");
            levelOrCrowdednessTv.setTextColor(Color.YELLOW);
            crowdednessInstructionTv.setText(getString(R.string.instruction_content));
        }
        else {
            levelOrCrowdednessTv.setText("Low");
            levelOrCrowdednessTv.setTextColor(Color.GREEN);
            crowdednessInstructionTv.setText(getString(R.string.instruction_low_content));
        }
    }

    private int getDestinationType(Spinner spinner) {
        String text = spinner.getSelectedItem().toString();
        if (text.equals("outbound")) {
            return 0;
        }
        return 1;
    }

    private void initView() {
        levelOrCrowdednessTv = findViewById(R.id.level_crowdedness_value_tv);
        crowdednessInstructionTv = findViewById(R.id.instruction_crowdness_content_tv);
        directionReportSpinner = findViewById(R.id.direction_report_spinner);
        trainNameReportEt = findViewById(R.id.line_name_report_et);
        stopNameReportEt = findViewById(R.id.stop_name_report_et);
//        dayReportSpinner = findViewById(R.id.day_report_spinner);
        dayReportEt = findViewById(R.id.day_report_et);
        timeReportEt = findViewById(R.id.time_report_et);
        carriageNumberEt = findViewById(R.id.carriage_number_et);
//        carriageNumberSpinner = findViewById(R.id.carriage_number_spinner);
        criminalReportEt = findViewById(R.id.criminal_report_et);
        crowdednessLevelEt = findViewById(R.id.crowdedness_level_et);
        reportCrowdednessBtn = findViewById(R.id.report_crowdedness_btn);
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        crowdednessValue = bundle.getFloat("crowdnessLevel");
        crowdednessLevelEt.setText(String.valueOf(crowdednessValue));
    }

    private void initVars() {
        routes = new ArrayList<>();
        stops = new ArrayList<>();
        departureTimes = new ArrayList<>();
    }

    //todo: create GET method for retrieving all train names
    public void getAllTrainNamesByDestinationType(int destinationType){
        OkHttpClient client = new OkHttpClient();
        String url = "http://ptsafenodejsapi-env.eba-cx9pgkwu.us-east-1.elasticbeanstalk.com/v1/report/findRoutesByDestType?destinationtype=" + destinationType;
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (routes.size() != 0) {
                    routes.clear();
                }
                JSONObject resultObj = null;
                try {
                    resultObj = new JSONObject(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray data = null;
                try {
                    data = resultObj.getJSONArray("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for(int i = 0; i < data.length(); i++) {
                    JSONObject obj = null;
                    try {
                        obj = data.getJSONObject(i);
                        String routeId = obj.getString("route_id");
                        String routeShortName = obj.getString("route_short_name");
                        String routeLongName = obj.getString("route_long_name");
                        String tripHeadSign = obj.getString("trip_headsign");
                        int directionId = obj.getInt("direction_id");
                        RouteByDestinationType newRoute = new RouteByDestinationType(routeId, routeShortName, routeLongName, tripHeadSign, directionId);
                        routes.add(newRoute);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                List<String> routesString = routes.stream().map(item -> item.getRouteId() + " : " + item.getRouteShortName()).collect(Collectors.toList());
                String[] routesList = routesString.toArray(new String[0]);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_dropdown_item_1line, routesList);
                        trainNameReportEt.setAdapter(adapter);
                    }
                });
            }

        });
    };
    //todo: create GET method for retrieving all station names
    public void getAllStopsByRouteId(String routeId){
        OkHttpClient client = new OkHttpClient();
        String url = "http://ptsafenodejsapi-env.eba-cx9pgkwu.us-east-1.elasticbeanstalk.com/v1/report/findStopsByRouteId?routeid=" + routeId;
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (stops.size() != 0) {
                    stops.clear();
                }
                JSONObject resultObj = null;
                try {
                    resultObj = new JSONObject(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray data = null;
                try {
                    data = resultObj.getJSONArray("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for(int i = 0; i < data.length(); i++) {
                    JSONObject obj = null;
                    try {
                        obj = data.getJSONObject(i);
                        String routeId = obj.getString("route_id");
                        int stopId = obj.getInt("stop_id");
                        String stopName = obj.getString("stop_name");
                        double latitude = obj.getDouble("stop_lat");
                        double longitude = obj.getDouble("stop_lon");
                        StopByRouteId newStop = new StopByRouteId(routeId, stopId, stopName, latitude, longitude);
                        stops.add(newStop);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
//                Log.i("stop_id-stop_name", stops.get(0).getStopId() + " " + stops.get(0).getStopName());
                List<String> stopString = stops.stream().map(item -> item.getStopId() + " : " + item.getStopName()).collect(Collectors.toList());
                String[] stopList = stopString.toArray(new String[0]);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_dropdown_item_1line, stopList);
                        stopNameReportEt.setAdapter(adapter);
                    }
                });
            }

        });
    };
    //todo: create GET method for retrieving departure times
    public void getAllDepartureTimesByDirectionRouteStops(int directionType, String routeId, String stopId){
        OkHttpClient client = new OkHttpClient();
        String url = "http://ptsafenodejsapi-env.eba-cx9pgkwu.us-east-1.elasticbeanstalk.com/v1/report/findDepartureTimesByDirectionRouteIdStopId?directiontype=" + directionType + "&routeid=" + routeId + "&stopid=" + stopId;
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (departureTimes.size() != 0) {
                    departureTimes.clear();
                }
                JSONObject resultObj = null;
                try {
                    resultObj = new JSONObject(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray data = null;
                try {
                    data = resultObj.getJSONArray("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for(int i = 0; i < data.length(); i++) {
                    JSONObject obj = null;
                    try {
                        obj = data.getJSONObject(i);
                        String routeId = obj.getString("route_id");
                        int stopId = obj.getInt("stop_id");
                        String departureTime = obj.getString("departure_time");
                        DepartureTimesByRouteDirectionStops newDepartureTime = new DepartureTimesByRouteDirectionStops(routeId, stopId, departureTime);
                        departureTimes.add(newDepartureTime);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("departure_time", departureTimes.get(0).getDepartureTime());
                List<String> departureTimeString = departureTimes.stream().map(item -> item.getDepartureTime()).collect(Collectors.toList());
                String[] departureTimeList = departureTimeString.toArray(new String[0]);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_dropdown_item_1line, departureTimeList);
                        timeReportEt.setAdapter(adapter);
                    }
                });
            }

        });
    };

    //todo: create POST method for creating crowdedness data
    public void createCrowdedness(int stopId, String routeId, String departureTime, int direction, String day, int carriageNumber, float crowdednessLevel, String criminalActivity){
        OkHttpClient client = new OkHttpClient();
        String url = "http://ptsafenodejsapi-env.eba-cx9pgkwu.us-east-1.elasticbeanstalk.com/v1/report/create";
        PostCrowdedness crowdedness = null;
        crowdedness = new PostCrowdedness(stopId, routeId, departureTime, direction, day, carriageNumber, crowdednessLevel, criminalActivity);
        Gson gson = new Gson();
        String crowdednessJson = gson.toJson(crowdedness);
        RequestBody body = RequestBody.create(crowdednessJson, JSON);
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                JSONObject resultObj = null;
                try {
                    resultObj = new JSONObject(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(CrowdednessReportActivity.this, MainActivity.class);
                startActivity(intent);
            }
            //todo: toast message
        });
    };

}