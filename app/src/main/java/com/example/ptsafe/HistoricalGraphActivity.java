package com.example.ptsafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ptsafe.adapter.NewsAdapter;
import com.example.ptsafe.adapter.TrainAdapter;
import com.example.ptsafe.model.News;
import com.example.ptsafe.model.Train;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HistoricalGraphActivity extends AppCompatActivity {

    private BarChart barChart;
    private Spinner yearSpinner;
    private List<BarEntry> data2018;
    private List<BarEntry> data2019;
    private List<BarEntry> data2020;
    private int stopId;
    private TextView crowdednessValueTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical_graph);
        getIntentData();
        initView();
        initVars();
        getPaxRank(stopId);
        getAllPaxByStopId(stopId);

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String yearLabel = adapterView.getItemAtPosition(i).toString();
                BarDataSet barDataSet;
                BarData barData;
                switch (yearLabel) {
                    case "2018":
                        barDataSet = new BarDataSet(data2018, "crowdedness density per number of platforms");
                        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                        barDataSet.setValueTextColor(Color.BLACK);
                        barDataSet.setValueTextSize(16f);

                        barData = new BarData(barDataSet);
                        barChart.setData(barData);
                        barChart.animateY(2000);
                        barChart.notifyDataSetChanged();
                        barChart.invalidate();
                        break;
                    case "2019":
                        barDataSet = new BarDataSet(data2019, "crowdedness density per number of platforms");
                        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                        barDataSet.setValueTextColor(Color.BLACK);
                        barData = new BarData(barDataSet);
                        barChart.setData(barData);
                        barChart.animateY(2000);
                        barChart.notifyDataSetChanged();
                        barChart.invalidate();
                        break;
                    case "2020":
                        barDataSet = new BarDataSet(data2020, "crowdedness density per number of platforms");
                        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                        barDataSet.setValueTextColor(Color.BLACK);
                        barData = new BarData(barDataSet);
                        barChart.setData(barData);
                        barChart.animateY(2000);
                        barChart.notifyDataSetChanged();
                        barChart.invalidate();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initView() {
        barChart = findViewById(R.id.historical_barchart_bc);
        yearSpinner = findViewById(R.id.year_spinner);
        crowdednessValueTv = findViewById(R.id.crowdedness_value_tv);
    }

    private void initVars() {
        data2018 = new ArrayList<>();
        data2019 = new ArrayList<>();
        data2020 = new ArrayList<>();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        stopId = intent.getExtras().getInt("stopId");
    }

    private int getSelectedYear(Spinner spinner) {
        int year = Integer.parseInt(spinner.getSelectedItem().toString());
        return year;
    }

    private List<BarEntry> getBarChartData(int preAmPeak, int amPeak, int interPeak, int pmPeak, int latePm) {
        List<BarEntry> data = new ArrayList<>();
        data.add(new BarEntry(0, preAmPeak));
        data.add(new BarEntry(1, amPeak));
        data.add(new BarEntry(2, interPeak));
        data.add(new BarEntry(3, pmPeak));
        data.add(new BarEntry(4, latePm));
        return data;
    }

    private void setBarAxis() {
        ArrayList<String> xAxisLables = new ArrayList();
        xAxisLables.add("morning");
        xAxisLables.add("noon");
        xAxisLables.add("late noon");
        xAxisLables.add("evening");
        xAxisLables.add("late evening");

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLables));
        barChart.getXAxis().setLabelCount(xAxisLables.size());
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setAxisMinimum(0f);
        barChart.getAxisLeft().setDrawGridLines(false);
    }

    public void getAllPaxByStopId(int stopId){
        OkHttpClient client = new OkHttpClient();
        String url = "http://ptsafenodejsapi-env.eba-cx9pgkwu.us-east-1.elasticbeanstalk.com/v1/report/findAllPaxForEachStopId?stopid=" + stopId;
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
                JSONObject obj2018 = null;
                try {
                    obj2018 = data.getJSONObject(0);
                    data2018 = getBarChartData(Integer.parseInt(obj2018.getString("density_pre_am_peak")), Integer.parseInt(obj2018.getString("density_am_peak")), Integer.parseInt(obj2018.getString("density_interpeak")), Integer.parseInt(obj2018.getString("density_pm_peak")), Integer.parseInt(obj2018.getString("density_late_pm")));
                    JSONObject obj2019 = data.getJSONObject(1);
                    data2019 = getBarChartData(Integer.parseInt(obj2019.getString("density_pre_am_peak")), Integer.parseInt(obj2019.getString("density_am_peak")), Integer.parseInt(obj2019.getString("density_interpeak")), Integer.parseInt(obj2019.getString("density_pm_peak")), Integer.parseInt(obj2019.getString("density_late_pm")));
                    JSONObject obj2020 = data.getJSONObject(2);
                    data2020 = getBarChartData(Integer.parseInt(obj2020.getString("density_pre_am_peak")), Integer.parseInt(obj2020.getString("density_am_peak")), Integer.parseInt(obj2020.getString("density_interpeak")), Integer.parseInt(obj2020.getString("density_pm_peak")), Integer.parseInt(obj2020.getString("density_late_pm")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BarDataSet barDataSet = new BarDataSet(data2018, "crowdedness_density per number of platforms");
                        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                        barDataSet.setValueTextColor(Color.BLACK);
                        barDataSet.setValueTextSize(16f);

                        BarData barData = new BarData(barDataSet);
                        setBarAxis();
                        barChart.setFitBars(true);
                        barChart.setData(barData);
                        barChart.getDescription().setEnabled(false);
                        barChart.animateY(2000);
                    }
                });
            }
        });
    }

    public void getPaxRank(int stopId){
        OkHttpClient client = new OkHttpClient();
        String url = "http://ptsafenodejsapi-env.eba-cx9pgkwu.us-east-1.elasticbeanstalk.com/v1/report/findRecentStopRankByStopId?stopid=" + stopId;
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
                int rank = 0;
                try {
                    data = resultObj.getJSONArray("message");
                    rank = data.getJSONObject(0).getInt("patronage_rank");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                int finalRank = rank;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        crowdednessValueTv.setText(String.valueOf(finalRank));
                    }
                });
            }

        });
    };
}