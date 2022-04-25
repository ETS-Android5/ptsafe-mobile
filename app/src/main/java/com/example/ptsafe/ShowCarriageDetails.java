package com.example.ptsafe;

import static com.example.ptsafe.AddNewsActivity.JSON;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ptsafe.model.Comment;
import com.example.ptsafe.model.PostCrowdedness;
import com.example.ptsafe.model.PostWishlist;
import com.google.gson.Gson;

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
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShowCarriageDetails extends AppCompatActivity {

    private TextView crowdednessCarriageValueTv;
    private TextView carriageCrimeContentTv;
    private TextView carriageInstructionValueTv;
    private Button addTripBtn;
    private int carriageNumber;
    private double averageCrowdednessLevel;
    private String day;
    private String routeId;
    private int stopId;
    private String departureTime;
    private String crimeActivities;
    private String currentAddress;
    private String destinationAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_carriage_details);
        getDataFromIntent();
        initView();
        renderLevelOfCrowdedness(averageCrowdednessLevel);
        getCriminalActivities(carriageNumber, day, routeId, stopId, departureTime);
        getInstruction(averageCrowdednessLevel);
        addTripBtn.setOnClickListener(addTrip());
    }

    private void initView() {
        crowdednessCarriageValueTv = findViewById(R.id.crowdedness_carriage_value_tv);
        carriageCrimeContentTv = findViewById(R.id.carriage_crime_content_tv);
        carriageInstructionValueTv = findViewById(R.id.carriage_instruction_value_tv);
        addTripBtn = findViewById(R.id.add_carriage_btn);
    }

    private View.OnClickListener addTrip() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTripWishlist(currentAddress, destinationAddress, stopId, routeId, departureTime, carriageNumber);
                Intent intent = new Intent(ShowCarriageDetails.this, MainActivity.class);
                startActivity(intent);
            }
        };
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        averageCrowdednessLevel = bundle.getDouble("averageCrowdednessLevel");
        carriageNumber = bundle.getInt("carriageNumber");
        day = bundle.getString("day");
        routeId = bundle.getString("routeId");
        stopId = bundle.getInt("stopId");
        departureTime = bundle.getString("departureTime");
        currentAddress = bundle.getString("currentAddress");
        destinationAddress = bundle.getString("destinationAddress");
    }

    private void getInstruction(double averageCrowdednessLevel) {
        if (averageCrowdednessLevel > 0.8) {
            carriageInstructionValueTv.setText(getString(R.string.instruction_content));
        }
        else if (averageCrowdednessLevel > 0.5) {
            carriageInstructionValueTv.setText(getString(R.string.instruction_content));
        }
        else if (averageCrowdednessLevel == -1) {
            carriageInstructionValueTv.setText("No instructions");
        }
        else {
            carriageInstructionValueTv.setText(getString(R.string.instruction_low_content));
        }
    }

    private void renderLevelOfCrowdedness(double averageCrowdednessLevel) {
        if (averageCrowdednessLevel > 0.8) {
            crowdednessCarriageValueTv.setText("high");
            crowdednessCarriageValueTv.setTextColor(Color.RED);
        }
        else if (averageCrowdednessLevel > 0.5) {
            crowdednessCarriageValueTv.setText("medium");
            crowdednessCarriageValueTv.setTextColor(Color.CYAN);
        }
        else if (averageCrowdednessLevel == -1) {
            crowdednessCarriageValueTv.setText("No crowdedness level detected");
            crowdednessCarriageValueTv.setTextColor(Color.GRAY);
        }
        else {
            crowdednessCarriageValueTv.setText("low");
            crowdednessCarriageValueTv.setTextColor(Color.GREEN);
        }
    }

    private void getCriminalActivities(int carriageNumber, String day, String routeId, int stopId, String departureTime) {
        OkHttpClient client = new OkHttpClient();
        String url = "http://ptsafenodejsapi-env.eba-cx9pgkwu.us-east-1.elasticbeanstalk.com/v1/report/findCriminalActivitiesByCarriageNumber?carriagenumber=" + carriageNumber + "&day=" + day + "&routeid=" + routeId + "&stopid=" + stopId + "&departuretime=" + departureTime;
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
                    Object item = resultObj.get("message");
                    if (item instanceof JSONArray){
                        data = resultObj.getJSONArray("message");
                        for(int i = 0; i < data.length(); i++) {
                            JSONObject obj = null;
                            try {
                                obj = data.getJSONObject(i);
                                String newCrimeActivities = obj.getString("criminal_activity");
                                crimeActivities = "";
                                crimeActivities += String.valueOf(i+1) + ". " + newCrimeActivities + System.lineSeparator();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                               carriageCrimeContentTv.setText(crimeActivities);
                            }
                        });
                    }
                    else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                carriageCrimeContentTv.setText("no crimes recorded for this carriage");
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void createTripWishlist(String sourceName, String destinationName, int stopId, String routeId, String departureTime, int carriageNumber) {
        OkHttpClient client = new OkHttpClient();
        String url = "http://ptsafenodejsapi-env.eba-cx9pgkwu.us-east-1.elasticbeanstalk.com/v1/report/createTripWishlist";
        PostWishlist wishlist = null;
        wishlist = new PostWishlist(sourceName, destinationName, stopId, routeId, departureTime, carriageNumber);
        Gson gson = new Gson();
        String wishlistJson = gson.toJson(wishlist);
        RequestBody body = RequestBody.create(wishlistJson, JSON);
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
                Intent intent = new Intent(ShowCarriageDetails.this, MainActivity.class);
                startActivity(intent);
            }
            //todo: toast message
        });
    }
}