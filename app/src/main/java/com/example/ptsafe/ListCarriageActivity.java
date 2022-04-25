package com.example.ptsafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.ptsafe.adapter.CarriageAdapter;
import com.example.ptsafe.adapter.NewsAdapter;
import com.example.ptsafe.model.Carriage;
import com.example.ptsafe.model.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListCarriageActivity extends AppCompatActivity {

    private RecyclerView listCarriagesRv;
    private CarriageAdapter.ClickListener listener;
    private CarriageAdapter adapter;
    private RecyclerView.Adapter itemsAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Carriage> carriageItems;
    private String day;
    private String routeId;
    private int stopId;
    private String departureTime;
    private String currentAddress;
    private String destinationAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_carriage);
        initVars();
        getIntentData();
        initView();
        getAllCarriagesForDaysRouteStopDepartureTime(day, routeId, stopId, departureTime);
    }

    private void setOnClickListener(final List<Carriage> carriageItems) {
        listener = position -> {
            Intent intent = new Intent(ListCarriageActivity.this, ShowCarriageDetails.class);
            Bundle bundle = new Bundle();
            bundle.putInt("carriageNumber", carriageItems.get(position).getCarriageNumber());
            bundle.putDouble("averageCrowdednessLevel", carriageItems.get(position).getAverageCrowdednessLevel());
            bundle.putString("day", day.trim());
            bundle.putString("routeId", routeId.trim());
            bundle.putInt("stopId", stopId);
            bundle.putString("departureTime", departureTime.trim());
            bundle.putString("currentAddress", currentAddress);
            bundle.putString("destinationAddress", destinationAddress);
            intent.putExtras(bundle);
            startActivity(intent);
        };
    }

    private String getCurrentDayName() {
        String[] dayNames = new DateFormatSymbols().getWeekdays();
        Calendar cal = Calendar.getInstance();
        return dayNames[cal.get(Calendar.DAY_OF_WEEK)];
    }

    private void getIntentData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        day = getCurrentDayName();
        stopId = bundle.getInt("stopId");
        routeId = bundle.getString("routeId");
        departureTime = bundle.getString("departureTime");
        currentAddress = bundle.getString("currentAddress");
        destinationAddress = bundle.getString("destinationAddress");
    }
    private void initView() {
        listCarriagesRv = findViewById(R.id.list_carriages_rv);
        layoutManager = new LinearLayoutManager(this);
    }

    private void initVars() {
        carriageItems = new ArrayList<>();
    }

    //todo: get all carriages for days route stop departure time
    public void getAllCarriagesForDaysRouteStopDepartureTime(String day, String routeId, int stopId, String departureTime){
        OkHttpClient client = new OkHttpClient();
        String url = "http://ptsafenodejsapi-env.eba-cx9pgkwu.us-east-1.elasticbeanstalk.com/v1/report/findCarriagesByDayRouteStopDepartureTime?day=" + day + "&routeid=" + routeId + "&stopid=" + stopId + "&departuretime=" + departureTime;
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                JSONObject resultObj = null;
                List<Carriage> carriageData = new ArrayList<>();
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
                        int newsId = obj.getInt("carriage_number");
                        double averageCrowdnessLevel = obj.getDouble("average_crowdness_level");
                        Carriage newCarriage = new Carriage(newsId, averageCrowdnessLevel);
                        carriageItems.add(newCarriage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setOnClickListener(carriageItems);
                        adapter = new CarriageAdapter(carriageItems, listener);
                        listCarriagesRv.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                                LinearLayoutManager.VERTICAL));
                        listCarriagesRv.setAdapter(adapter);
                        listCarriagesRv.setLayoutManager(layoutManager);
                    }
                });
            }

        });
    };
}