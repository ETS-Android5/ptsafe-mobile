package com.example.ptsafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.ptsafe.adapter.NewsAdapter;
import com.example.ptsafe.adapter.TrainAdapter;
import com.example.ptsafe.model.News;
import com.example.ptsafe.model.Train;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListTrainActivity extends AppCompatActivity {

    private RecyclerView crimeNewsRv;
    private TrainAdapter.ClickListener listener;
    private RecyclerView availableTrainsRv;
    private Button showWeeklyBtn;
    private Button showWeekdayBtn;
    private TrainAdapter trainAdapter;
    private RecyclerView.LayoutManager trainLayoutManager;
    private List<Train> trainsData;
    private int stopId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_train);
        initData();
        initView();
        getAllTrainsData();
    }

    private void initView () {
        crimeNewsRv = findViewById(R.id.location_news_rv);
        availableTrainsRv = findViewById(R.id.available_trains_rv);
        trainLayoutManager = new LinearLayoutManager(this);
        showWeekdayBtn = findViewById(R.id.show_weekday_btn);
        showWeeklyBtn = findViewById(R.id.show_weekly_btn);
    }

    private void setOnClickListener(final List<Train> trainsData) {
        listener = position -> {
            Intent intent = new Intent(ListTrainActivity.this, CarriageDetails.class);
            Bundle bundle = new Bundle();
            bundle.putInt("stopId", stopId);
            bundle.putString("routeId", trainsData.get(position).getRouteId());
            bundle.putString("routeLongName", trainsData.get(position).getRouteLongName());
            bundle.putString("tripHeadSign", trainsData.get(position).getTripHeadSign());
            bundle.putString("departureTime", trainsData.get(position).getDepartureTime());
            intent.putExtras(bundle);
            startActivity(intent);
        };
    }

    private void getAllTrainsData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        stopId = bundle.getInt("stopId");
        int destinationType = bundle.getInt("destinationType");
        double latitude = bundle.getDouble("latitude");
        double longitude = bundle.getDouble("longitude");
        getAllTrainsByStopDirectionAndCoordinates(stopId, destinationType, latitude, longitude);
    }

    public void getAllTrainsByStopDirectionAndCoordinates(int stopId, int directionType, double latitude, double longitude){
        OkHttpClient client = new OkHttpClient();
        String url = "http://ptsafenodejsapi-env.eba-cx9pgkwu.us-east-1.elasticbeanstalk.com/v1/report/findRoutesByStopIdDirectionCurrLocation?stopid=" + stopId + "&directiontype=" + directionType + "&lat=" + latitude + "&long=" + longitude;
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                JSONObject resultObj = null;
                List<Train> trainData = new ArrayList<>();
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
                        String routeLongName = obj.getString("route_long_name");
                        String tripHeadSign = obj.getString("trip_headsign");
                        String departureTime = obj.getString("departure_time");
                        Train newTrain = new Train(routeId, routeLongName, tripHeadSign, departureTime);
                        trainsData.add(newTrain);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setOnClickListener(trainsData);
                        trainAdapter = new TrainAdapter(trainsData, listener);
                        availableTrainsRv.addItemDecoration(new DividerItemDecoration(getApplication(),
                                LinearLayoutManager.VERTICAL));
                        availableTrainsRv.setAdapter(trainAdapter);
                        availableTrainsRv.setLayoutManager(trainLayoutManager);
                    }
                });
            }
        });
    }

    private void initData() {
        trainsData = new ArrayList<>();
    }
}