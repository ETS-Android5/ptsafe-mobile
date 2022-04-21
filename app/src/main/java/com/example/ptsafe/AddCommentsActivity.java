package com.example.ptsafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.ptsafe.model.Comment;
import com.example.ptsafe.model.PostComment;
import com.google.gson.Gson;

import org.json.JSONArray;
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

public class AddCommentsActivity extends AppCompatActivity {
    private String newsId;
    private EditText commentTitleEt;
    private EditText commentContentEt;
    private Button addCommentsBtn;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comments);
        getIntentData();
        initView();
        addCommentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCommentsForSpecificNewsId(newsId);
            }
        });
    }

    private void initView() {
        commentTitleEt = findViewById(R.id.comment_title_et);
        commentContentEt = findViewById(R.id.comment_content_et);
        addCommentsBtn = findViewById(R.id.add_comments_two_btn);
    }

    //todo: implement a function to get intent from another page
    private void getIntentData() {
        Intent intent = getIntent();
        this.newsId = intent.getExtras().getString("newsId");
    }

    //todo: implement add comments function using okHttp
    private void addCommentsForSpecificNewsId(String newsId){
        OkHttpClient client = new OkHttpClient();
        String url = "http://ptsafenodejsapi-env.eba-cx9pgkwu.us-east-1.elasticbeanstalk.com/v1/comment/create";

        PostComment comment = null;
        comment = new PostComment(newsId, commentTitleEt.getText().toString(), commentContentEt.getText().toString());
        Gson gson = new Gson();
        String commentJson = gson.toJson(comment);
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
                Intent intent = new Intent(AddCommentsActivity.this, MainActivity.class);
                startActivity(intent);
            }
            //todo: toast message
        });
    }
}