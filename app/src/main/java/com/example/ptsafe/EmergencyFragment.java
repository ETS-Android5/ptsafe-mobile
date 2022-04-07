package com.example.ptsafe;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ptsafe.R;
import com.example.ptsafe.adapter.EmergencyAdapter;
import com.example.ptsafe.adapter.NewsAdapter;
import com.example.ptsafe.model.Emergency;
import com.example.ptsafe.model.News;

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
import okhttp3.Response;

public class EmergencyFragment extends Fragment {

    private RecyclerView emergencyRv;
    private EmergencyAdapter.ClickListener listener;
    private List<Emergency> emergencyItems;
    private EmergencyAdapter adapter;
    private RecyclerView.Adapter itemsAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private static final String TAG = "MoreInfoActivity";


    public EmergencyFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the View for this fragment
        View view = inflater.inflate(R.layout.fragment_emergency, container, false);
        getActivity().setTitle("Emergency call");
        initView(view);
        getAllEmergency();
        return view;
    }

    //todo: create onclick listener to call
    private void setOnClickListener(final List<Emergency> emergencyItems) {
        listener = position -> {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE},1);
                Log.d(TAG, "request_permission():Requesting permission");
                Toast.makeText(getContext(),"need phone permission",Toast.LENGTH_SHORT).show();

            }else{
                Log.d(TAG, "request_permission():Already have permission!");
                Intent intent = new Intent(Intent.ACTION_CALL);
                Uri data = Uri.parse("tel:" + emergencyItems.get(position).getOrganizationNumber());
                intent.setData(data);
                startActivity(intent);
            }
        };
    }

    //get news data by implementing okhttp
    public void getAllEmergency(){
        OkHttpClient client = new OkHttpClient();
        String url = "https://ptsafe-backend.herokuapp.com/v1/emergency/findAll";
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                JSONObject resultObj = null;
                List<Emergency> emergencyData = new ArrayList<>();
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
                        String organizationId = obj.getString("organization_id");
                        String organizationName = obj.getString("organization_name");
                        String callNumber = obj.getString("call_number");
                        String address = obj.getString("address");
                        Emergency newEmergency = new Emergency(organizationId, organizationName, callNumber, address);
                        emergencyData.add(newEmergency);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                emergencyItems = emergencyData;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setOnClickListener(emergencyItems);
                        adapter = new EmergencyAdapter(emergencyItems, listener);
                        emergencyRv.addItemDecoration(new DividerItemDecoration(getContext(),
                                LinearLayoutManager.VERTICAL));
                        emergencyRv.setAdapter(adapter);
                        emergencyRv.setLayoutManager(layoutManager);
                    }
                });
            }

        });
    };

    public void initView(View view) {
        emergencyRv = view.findViewById(R.id.emergency_rv);
        layoutManager = new LinearLayoutManager(getContext());
    }
}