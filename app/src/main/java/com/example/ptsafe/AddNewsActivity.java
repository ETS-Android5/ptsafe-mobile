package com.example.ptsafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.ptsafe.model.PostComment;
import com.example.ptsafe.model.PostNews;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddNewsActivity extends AppCompatActivity {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private EditText newsTitleEt;
    private Spinner newsLabelSpinner;
    private EditText newsContentEt;
    private EditText imageUrlEt;
    private EditText newsUrlEt;
    private Button addNewsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_news);
        initView();
        addNewsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNews();
            }
        });
    }

    private void initView() {
        newsTitleEt = findViewById(R.id.news_title_et);
        newsLabelSpinner = findViewById(R.id.news_submit_spinner);
        newsContentEt = findViewById(R.id.news_content_et);
        imageUrlEt = findViewById(R.id.image_url_et);
        newsUrlEt = findViewById(R.id.news_url_et);
        addNewsBtn = findViewById(R.id.add_news_two_btn);
    }

    //todo: implement add comments function using okHttp
    private void addNews(){
        OkHttpClient client = new OkHttpClient();
        String url = "http://ptsafenodejsapi-env.eba-cx9pgkwu.us-east-1.elasticbeanstalk.com/v1/news/create";

        PostNews news = null;
        String label = "";
        if (newsLabelSpinner.getSelectedItem().toString().equals("crime")) {
            label = "crime";
        }
        else {
            label = "transport";
        }
        news = new PostNews(newsTitleEt.getText().toString(), label, newsContentEt.getText().toString(), imageUrlEt.getText().toString(), newsUrlEt.getText().toString());
        Gson gson = new Gson();
        String commentJson = gson.toJson(news);
        RequestBody body = RequestBody.create(commentJson, JSON);
        Request request = new Request.Builder().url(url).post(body).build();
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
                Intent intent = new Intent(AddNewsActivity.this, MainActivity.class);
                startActivity(intent);
            }
            //todo: toast message
        });
    }
}