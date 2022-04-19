package com.example.ptsafe;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ptsafe.R;


public class HomeFragment extends Fragment {

    private View crowdedMenuVw;
    private View emergencyMenuVw;
    private View newsMenuVw;
    private View addTripMenuVw;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the View for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().setTitle("Home");
        initView(view);
        crowdedMenuVw.setOnClickListener(setMenuBtn("crowd"));
        emergencyMenuVw.setOnClickListener(setMenuBtn("emergency"));
        newsMenuVw.setOnClickListener(setMenuBtn("news"));
        addTripMenuVw.setOnClickListener(setMenuBtn("trip"));
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
                    case "emergency":
                        EmergencyFragment emergencyName = new EmergencyFragment();
                        fragmentTransaction.replace(R.id.content_frame, emergencyName);
                        fragmentTransaction.commit(); break;
                    case "news":
                        NewsFragment newsName = new NewsFragment();
                        fragmentTransaction.replace(R.id.content_frame, newsName);
                        fragmentTransaction.commit(); break;
                    case "trip":
                        FindStationFragment findStationName = new FindStationFragment();
                        fragmentTransaction.replace(R.id.content_frame, findStationName);
                }

            }
        };
    }

    private void initView(View view) {
        crowdedMenuVw = view.findViewById(R.id.detect_menu_view);
        emergencyMenuVw = view.findViewById(R.id.emergency_menu_view);
        newsMenuVw = view.findViewById(R.id.news_view_menu);
        addTripMenuVw = view.findViewById(R.id.add_trip_menu_view);
    }
}