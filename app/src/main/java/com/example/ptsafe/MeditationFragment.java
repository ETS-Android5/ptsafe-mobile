package com.example.ptsafe;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


public class MeditationFragment extends Fragment {

    private ConstraintLayout lightLl;
    private ConstraintLayout boxLl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meditation, container, false);
        getActivity().setTitle("Meditation");
        initView(view);
        lightLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), LightBreathingActivity.class);
                startActivity(intent);
            }
        });
        boxLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), BoxBreathingActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private void initView(View view){
        lightLl = view.findViewById(R.id.ll_llight);
        boxLl = view.findViewById(R.id.ll_box);
    }
}