package com.example.ptsafe;

import static com.example.ptsafe.AddNewsActivity.JSON;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Color;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
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
    private Spinner directionReportSpinner;
    private AutoCompleteTextView trainNameReportEt;
    private AutoCompleteTextView stopNameReportEt;
    private Spinner dayReportSpinner;
    private AutoCompleteTextView timeReportEt;
    private Spinner carriageNumberSpinner;
    private EditText criminalReportEt;
    private EditText crowdednessLevelEt;
    private Button reportCrowdednessBtn;
    private float crowdednessValue;
    private int destinationType;
    private String routeId;
    private String stopId;
    private List<RouteByDestinationType> routes;
    private List<StopByRouteId> stops;
    private List<DepartureTimesByRouteDirectionStops> departureTimes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crowdedness_report);
        initVars();
        initView();
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
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (stopNameReportEt.getText().toString().trim().length() != 0) {
                        stopId = stopNameReportEt.getText().toString().split(":")[0].trim();
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
                    String day = dayReportSpinner.getSelectedItem().toString();
                    int carriageNumber = Integer.parseInt(carriageNumberSpinner.getSelectedItem().toString());
                    String criminalActivity = criminalReportEt.getText().toString();
                    createCrowdedness(Integer.parseInt(stopId), routeId, departureTime, destinationType, day , carriageNumber, crowdednessValue, criminalActivity);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please fill all the columns first!", Toast.LENGTH_SHORT).show();
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
        dayReportSpinner = findViewById(R.id.day_report_spinner);
        timeReportEt = findViewById(R.id.time_report_et);
        carriageNumberSpinner = findViewById(R.id.carriage_number_spinner);
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
                        StopByRouteId newStop = new StopByRouteId(routeId, stopId, stopName);
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