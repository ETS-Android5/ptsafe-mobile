package com.example.ptsafe;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class FindStationFragment extends Fragment {

    private Button addTripBtn;

    public FindStationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_find_station, container, false);
        initView(view);
        addTripBtn.setOnClickListener(setAddTripBtnListener());
        return view;
    }

    private View.OnClickListener setAddTripBtnListener () {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ViewStationActivity.class);
                startActivity(intent);
            }
        };
    }
    private void initView(View view) {
        addTripBtn = view.findViewById(R.id.add_trip_btn);
    }


}