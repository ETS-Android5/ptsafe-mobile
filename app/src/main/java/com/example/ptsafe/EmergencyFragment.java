package com.example.ptsafe;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.ptsafe.R;

public class EmergencyFragment extends Fragment {

    private Button call;
    private Button call2;
    private Button call3;
    private static final String TAG = "MoreInfoActivity";




    public EmergencyFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the View for this fragment
        View view = inflater.inflate(R.layout.fragment_emergency, container, false);
        call = view.findViewById(R.id.call1);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE},1);
                    Log.d(TAG, "request_permission():Requesting permission");
                    Toast.makeText(getContext(),"need phone permission",Toast.LENGTH_SHORT).show();

                }else{
                    Log.d(TAG, "request_permission():Already have permission!");
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    Uri data = Uri.parse("tel:" + "111");
                    intent.setData(data);
                    startActivity(intent);
                }
            }});
        call2 = view.findViewById(R.id.call2);
        call2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE},1);
                    Log.d(TAG, "request_permission():Requesting permission");
                    Toast.makeText(getContext(),"need phone permission",Toast.LENGTH_SHORT).show();

                }else{
                    Log.d(TAG, "request_permission():Already have permission!");
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    Uri data = Uri.parse("tel:" + "222");
                    intent.setData(data);
                    startActivity(intent);
                }
            }});
        call3 = view.findViewById(R.id.call3);
        call3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE},1);
                    Log.d(TAG, "request_permission():Requesting permission");
                    Toast.makeText(getContext(),"need phone permission",Toast.LENGTH_SHORT).show();

                }else{
                    Log.d(TAG, "request_permission():Already have permission!");
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    Uri data = Uri.parse("tel:" + "333");
                    intent.setData(data);
                    startActivity(intent);
                }
            }});
        return view;

    }
}