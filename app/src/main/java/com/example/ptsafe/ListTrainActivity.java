package com.example.ptsafe;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ptsafe.adapter.NewsAdapter;
import com.example.ptsafe.adapter.NewsTitleAdapter;
import com.example.ptsafe.adapter.TrainAdapter;
import com.example.ptsafe.model.NewsStop;
import com.example.ptsafe.model.Train;
import com.google.android.gms.maps.model.LatLng;

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
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListTrainActivity extends AppCompatActivity {

    private RecyclerView crimeNewsRv;
    private TrainAdapter.ClickListener listener;
    private NewsTitleAdapter.ClickListener newsTitleListener;
    private RecyclerView availableTrainsRv;
    private TextView errorMessageTv;
    private Button showHistoricalDataBtn;
    private Spinner trainSpinner;
    private TrainAdapter trainAdapter;
    private NewsTitleAdapter newsTitleAdapter;
    private RecyclerView.LayoutManager trainLayoutManager;
    private RecyclerView.LayoutManager newsLayoutManager;
    private List<Train> trainsData;
    private List<String> distinctTrainNames;
    private List<NewsStop> newsStopData;
    private List<NewsStop> nearestNewsStopData;
    private int stopId;
    private String currentAddress;
    private String destinationAddress;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_train);
        initData();
        initView();
        getAllCrimeNews();
        getAllTrainsData();
        showHistoricalDataBtn.setOnClickListener(showChart());
        trainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String trainLabel = adapterView.getItemAtPosition(i).toString();
                if (trainLabel.equals("all")) {
                    setOnClickListener(trainsData);
                    trainAdapter = new TrainAdapter(trainsData, listener);
                }
                else {
                    List<Train> filteredTrains = trainsData.stream().filter(item -> item.getRouteLongName().equals(trainLabel)).collect(Collectors.toList());
                    setOnClickListener(filteredTrains);
                    trainAdapter = new TrainAdapter(filteredTrains, listener);
                }
                availableTrainsRv.setAdapter(trainAdapter);
                trainAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initView () {
        crimeNewsRv = findViewById(R.id.location_news_rv);
        availableTrainsRv = findViewById(R.id.available_trains_rv);
        trainLayoutManager = new LinearLayoutManager(this);
        newsLayoutManager = new LinearLayoutManager(this);;
        showHistoricalDataBtn = findViewById(R.id.show_weekday_btn);
        errorMessageTv = findViewById(R.id.error_message_crime_tv);
        trainSpinner = findViewById(R.id.train_spinner);
    }

    private void setOnClickListener(final List<Train> trainsData) {
        listener = position -> {
            Intent intent = new Intent(ListTrainActivity.this, ListCarriageActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("stopId", stopId);
            bundle.putString("routeId", trainsData.get(position).getRouteId());
            bundle.putString("routeLongName", trainsData.get(position).getRouteLongName());
            bundle.putString("tripHeadSign", trainsData.get(position).getTripHeadSign());
            bundle.putString("departureTime", trainsData.get(position).getDepartureTime());
            bundle.putString("currentAddress", currentAddress);
            bundle.putString("destinationAddress", destinationAddress);
            intent.putExtras(bundle);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right,
                    R.anim.slide_out_left);
        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }

    private View.OnClickListener showChart() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListTrainActivity.this, HistoricalGraphActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("stopId", stopId);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        };
    }

    private void setNewsOnClickListener(final List<NewsStop> newsStopsData) {
        newsTitleListener = position -> {
            Intent intent = new Intent(ListTrainActivity.this, NewsDetails.class);
            Bundle bundle = new Bundle();
            bundle.putString("newsId", newsStopsData.get(position).getNewsId());
            bundle.putString("newsTitle", newsStopsData.get(position).getNewsTitle());
            bundle.putString("newsContent", newsStopsData.get(position).getNewsContent());
            bundle.putString("imageUrl", newsStopsData.get(position).getImageUrl());
            bundle.putString("newsUrl", newsStopsData.get(position).getNewsUrl());
            intent.putExtras(bundle);
            startActivity(intent);
        };
    }

    private void getAllTrainsData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        stopId = bundle.getInt("stopId");
        int destinationType = bundle.getInt("destinationType");
        latitude = bundle.getDouble("latitude");
        longitude = bundle.getDouble("longitude");
        currentAddress = bundle.getString("currentAddress");
        destinationAddress = bundle.getString("destinationAddress");
        getAllDistinctTrains(stopId, destinationType, latitude, longitude);
        getAllTrainsByStopDirectionAndCoordinates(stopId, destinationType, latitude, longitude);

    }

    public void getAllTrainsByStopDirectionAndCoordinates(int stopId, int directionType, double latitude, double longitude){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Australia/Sydney"));
        Date currentLocalTime = cal.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        OkHttpClient client = new OkHttpClient();
        String url = "http://ptsafenodejsapi-env.eba-cx9pgkwu.us-east-1.elasticbeanstalk.com/v1/report/findRoutesByStopIdDirectionCurrLocation?stopid=" + stopId + "&directiontype=" + directionType + "&lat=" + latitude + "&long=" + longitude;
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @SuppressLint("NewApi")
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
                        if (dateFormat.parse(newTrain.getDepartureTime()).after(dateFormat.parse(dateFormat.format(currentLocalTime)))) {
                            trainsData.add(newTrain);
                        }
                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                }
                //sort by times
//                dateFormat.parse(dateFormat.format(currentLocalTime)).after(dateFormat.parse("18:00:00"))
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setOnClickListener(trainsData);
                        trainAdapter = new TrainAdapter(trainsData, listener);
                        availableTrainsRv.addItemDecoration(new DividerItemDecoration(getApplication(),
                                LinearLayoutManager.VERTICAL));
                        removeDivider(availableTrainsRv);
                        availableTrainsRv.setAdapter(trainAdapter);
                        availableTrainsRv.setLayoutManager(trainLayoutManager);
                    }
                });
            }
        });
    }

    public void getAllDistinctTrains(int stopId, int directionType, double latitude, double longitude){
        distinctTrainNames.add("all");
        OkHttpClient client = new OkHttpClient();
        String url = "http://ptsafenodejsapi-env.eba-cx9pgkwu.us-east-1.elasticbeanstalk.com/v1/report/findDistinctRoutes?stopid=" + stopId + "&directiontype=" + directionType + "&lat=" + latitude + "&long=" + longitude;
        Request request = new Request.Builder().url(url).build();
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
                        String routeLongName = obj.getString("route_long_name");
                        distinctTrainNames.add(routeLongName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, distinctTrainNames);
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                                .simple_spinner_dropdown_item);
                        trainSpinner.setAdapter(spinnerArrayAdapter);
                    }
                });
            }
        });
    }

    private void removeDivider(RecyclerView rv) {
        for (int i = 0; i < rv.getItemDecorationCount(); i++) {
            if (rv.getItemDecorationAt(i) instanceof DividerItemDecoration)
                rv.removeItemDecorationAt(i);
        }
    }

    private double getApproximateDistance(String strAddress) {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        double stopLatitude = bundle.getDouble("stopLatitude");
        double stopLongitude = bundle.getDouble("stopLongitude");
        LatLng coordinates = getLocationFromAddress(strAddress);
//        Log.i("distance", String.valueOf(distance(coordinates.latitude, coordinates.longitude, stopLatitude, stopLongitude)));
        return distance(stopLatitude, stopLongitude, coordinates.latitude, coordinates.longitude);
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

    //getLocationFromAddress(item.getNewsAddress()
    public void getAllCrimeNews(){
        OkHttpClient client = new OkHttpClient();
        String url = "http://ptsafenodejsapi-env.eba-cx9pgkwu.us-east-1.elasticbeanstalk.com/v1/news/findByNewsLabel?newslabel=crime";
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
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
                        String newsId = obj.getString("news_id");
                        String newsTitle = obj.getString("news_title");
                        String newsFullAddress = obj.getString("news_location") + obj.getString("news_postcode");
                        String newsImageUrl = obj.getString("image_url");
                        String newsContent = obj.getString("news_content");
                        String newsUrl = obj.getString("news_url");
                        NewsStop news = new NewsStop(newsId, newsTitle, newsFullAddress, newsImageUrl, newsContent, newsUrl);
                        newsStopData.add(news);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
//                Log.i("news", newsStopData.get(5).getNewsAddress());
//                Log.i("distance", String.valueOf(getApproximateDistance(newsStopData.get(5).getNewsAddress())));
                nearestNewsStopData = newsStopData.stream().filter(item -> getApproximateDistance(item.getNewsAddress()) < 1.5).collect(Collectors.toList());
//                Log.i("nearestNewsSize", String.valueOf(newsStopData.stream().filter(item -> getApproximateDistance(item.getNewsAddress()) < 1.5).collect(Collectors.toList())));
                if (nearestNewsStopData.size() == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            errorMessageTv.setVisibility(View.VISIBLE);
                        }
                    });
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setNewsOnClickListener(nearestNewsStopData);
                            errorMessageTv.setVisibility(View.INVISIBLE);
                            embedDataToAdapter();
                        }
                    });
                }
            }
        });
    }

    private void embedDataToAdapter() {
        newsTitleAdapter = new NewsTitleAdapter(nearestNewsStopData, newsTitleListener);
        crimeNewsRv.addItemDecoration(new DividerItemDecoration(getApplication(),
                LinearLayoutManager.VERTICAL));
        removeDivider(crimeNewsRv);
        crimeNewsRv.setAdapter(newsTitleAdapter);
        crimeNewsRv.setLayoutManager(newsLayoutManager);
    }

    private void initData() {
        trainsData = new ArrayList<>();
        distinctTrainNames = new ArrayList<>();
        newsStopData = new ArrayList<>();
        nearestNewsStopData = new ArrayList<>();
    }
}