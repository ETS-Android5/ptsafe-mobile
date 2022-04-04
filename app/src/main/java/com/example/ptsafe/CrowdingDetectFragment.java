package com.example.ptsafe;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ptsafe.R;

public class CrowdingDetectFragment extends Fragment {

    public CrowdingDetectFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the View for this fragment
        View view = inflater.inflate(R.layout.fragment_crowding_detect, container, false);
        getActivity().setTitle("Detect crowding");
        return view;

    }
}