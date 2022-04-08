package com.example.ptsafe;

import android.annotation.SuppressLint;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.example.ptsafe.adapter.NewsAdapter;
import com.example.ptsafe.model.News;

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

public class NewsFragment extends Fragment {

    private RecyclerView newsRv;
    private NewsAdapter.ClickListener listener;
    private List<News> newsItems;
    private List<News> crimeNewsItems;
    private List<News> techNewsItems;
    private NewsAdapter adapter;
    private RecyclerView.Adapter itemsAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Spinner filterSpinner;
    private Button addNewsBtn;

    public NewsFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the View for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        getActivity().setTitle("News");
        initView(view);
        getAllNews();
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("NewApi")
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String newsLabel = adapterView.getItemAtPosition(i).toString();
                switch (newsLabel){
                    case "all":
                        setOnClickListener(newsItems);
                        adapter = new NewsAdapter(newsItems, listener);
                        break;
                    case "crime":
                        if (crimeNewsItems == null) {
                            crimeNewsItems = newsItems.stream()
                                    .filter(n -> n.getNewsLabel().equals("crime")).collect(Collectors.toList());
                        }
                        setOnClickListener(crimeNewsItems);
                        adapter = new NewsAdapter(crimeNewsItems, listener);
                        break;
                    case "tech":
                        if (techNewsItems == null) {
                            techNewsItems = newsItems.stream()
                                    .filter(n -> n.getNewsLabel().equals("tech")).collect(Collectors.toList());
                        }
                        setOnClickListener(techNewsItems);
                        adapter = new NewsAdapter(techNewsItems, listener);
                        break;
                }
                newsRv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return view;
    }

    //todo: create onclick listener to move to another page details
    private void setOnClickListener(final List<News> newsItems) {
        listener = position -> {
            Intent intent = new Intent(getContext(), NewsDetails.class);
            Bundle bundle = new Bundle();
            bundle.putString("newsId", newsItems.get(position).getNewsId());
            bundle.putString("newsTitle", newsItems.get(position).getNewsTitle());
            bundle.putString("newsLabel", newsItems.get(position).getNewsLabel());
            bundle.putString("newsContent", newsItems.get(position).getNewsContent());
            bundle.putString("imageUrl", newsItems.get(position).getImageUrl());
            bundle.putString("newsUrl", newsItems.get(position).getNewsUrl());

            intent.putExtras(bundle);
            startActivity(intent);
        };
    }

    private View.OnClickListener setAddCommentsBtnListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddNewsActivity.class);
                startActivity(intent);
            }
        };
    }

    public void initView(View view) {
        newsRv = view.findViewById(R.id.emergency_rv);
        layoutManager = new LinearLayoutManager(getContext());
        filterSpinner = view.findViewById(R.id.news_label_spinner);
        addNewsBtn = view.findViewById(R.id.add_news_btn);
    }

    //get news data by implementing okhttp
    public void getAllNews(){
        OkHttpClient client = new OkHttpClient();
        String url = "https://ptsafe-backend.herokuapp.com/v1/news/findAll";
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                JSONObject resultObj = null;
                List<News> newsData = new ArrayList<>();
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
                        String newsId = obj.getString("news_id");
                        String newsTitle = obj.getString("news_title");
                        String newsLabel = obj.getString("news_label");
                        String newsContent = obj.getString("news_content");
                        String imageUrl = obj.getString("image_url");
                        String newsUrl = obj.getString("news_url");
                        String newsLocation = obj.optString("news_location");
                        Integer newsPostcode = obj.optInt("news_postcode");
                        News newNews = new News(newsId, newsTitle, newsLabel, newsContent, imageUrl, newsUrl, newsLocation, newsPostcode);
                        newsData.add(newNews);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                newsItems = newsData;
                Log.i("string", String.valueOf(newsData.size()));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setOnClickListener(newsItems);
                        adapter = new NewsAdapter(newsItems, listener);
                        newsRv.addItemDecoration(new DividerItemDecoration(getContext(),
                                LinearLayoutManager.VERTICAL));
                        newsRv.setAdapter(adapter);
                        newsRv.setLayoutManager(layoutManager);
                        addNewsBtn.setOnClickListener(setAddCommentsBtnListener());
                    }
                });
            }

        });
    };
}