package com.example.ptsafe;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.example.ptsafe.adapter.NewsAdapter;
import com.example.ptsafe.adapter.TripAdapter;
import com.example.ptsafe.model.News;
import com.example.ptsafe.model.TripWishlist;

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

public class FindStationFragment extends Fragment {

    private RecyclerView tripRv;
    private TextView errorMessageTv;
    private TripAdapter.ClickListener listener;
    private TripAdapter adapter;
    private RecyclerView.Adapter itemsAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button addTripBtn;
    private List<TripWishlist> tripWishlistsData;

    public FindStationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_find_station, container, false);
        getActivity().setTitle("Your trips");
        initVars();
        initView(view);
        getAllTripWishlistData();
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

    private void initVars() {
        tripWishlistsData = new ArrayList<>();
    }

    private void initView(View view) {
        layoutManager = new LinearLayoutManager(getContext());
        tripRv = view.findViewById(R.id.saved_trips_rv);
        addTripBtn = view.findViewById(R.id.add_trip_btn);
        errorMessageTv = view.findViewById(R.id.saved_trip_error_message_tv);
    }

    //todo: create onclick listener to move to another page details
    private void setOnClickListener(final List<TripWishlist> tripItems) {
        listener = position -> {
            String wishlistId = tripItems.get(position).getWishlistId();
            deleteTripWishlistById(wishlistId);
        };
    }

    //get trip wishlist data by implementing okhttp
    public void getAllTripWishlistData() {
        OkHttpClient client = new OkHttpClient();
        String url = "http://ptsafenodejsapi-env.eba-cx9pgkwu.us-east-1.elasticbeanstalk.com/v1/report/findAllTripWishlist";
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
                    if (item instanceof JSONArray) {
                        data = resultObj.getJSONArray("message");
                        for(int i = 0; i < data.length(); i++) {
                            JSONObject obj = null;
                            try {
                                obj = data.getJSONObject(i);
                                String wishlistId = obj.getString("wishlist_id");
                                String sourceName = obj.getString("source_name");
                                String destinationName = obj.getString("destination_name");
                                String stopName = obj.getString("stop_name");
                                String routeLongName = obj.getString("route_long_name");
                                String departureTime = obj.getString("departure_time");
                                int carriageNumber = obj.getInt("carriage_number");
                                TripWishlist newTrip = new TripWishlist(wishlistId, sourceName, destinationName, stopName, routeLongName, departureTime, carriageNumber);
                                tripWishlistsData.add(newTrip);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setOnClickListener(tripWishlistsData);
                                errorMessageTv.setVisibility(View.INVISIBLE);
                                embedDataToAdapter();
                            }
                        });
                    }
                    else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                errorMessageTv.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });
    };

    private void embedDataToAdapter() {
        adapter = new TripAdapter(tripWishlistsData, listener);
        tripRv.addItemDecoration(new DividerItemDecoration(getContext(),
                LinearLayoutManager.VERTICAL));
        removeDivider(tripRv);
        tripRv.setAdapter(adapter);
        tripRv.setLayoutManager(layoutManager);
    }

    private void removeDivider(RecyclerView rv) {
        for (int i = 0; i < rv.getItemDecorationCount(); i++) {
            if (rv.getItemDecorationAt(i) instanceof DividerItemDecoration)
                rv.removeItemDecorationAt(i);
        }
    }

    private void deleteTripWishlistById(String wishlistId) {
        OkHttpClient client = new OkHttpClient();
        String url = "http://ptsafenodejsapi-env.eba-cx9pgkwu.us-east-1.elasticbeanstalk.com/v1/report/deleteTripWishlistById?wishlistid=" + wishlistId;
        Request request = new Request.Builder().url(url).delete().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }
}