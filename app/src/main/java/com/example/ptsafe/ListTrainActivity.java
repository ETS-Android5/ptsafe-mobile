package com.example.ptsafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class ListTrainActivity extends AppCompatActivity {

    private TextView listTrainTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_train);
        initView();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int stopId = bundle.getInt("stopId");
        listTrainTv.setText(String.valueOf(stopId));
    }

    private void initView () {
        listTrainTv = findViewById(R.id.list_train_tv);
    }
}