package com.example.ptsafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    private String newsTitle;
    private String imageUrl;
    private String newsContent;
    private String newsUrl;
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
                showAlertDialog(AddCommentsActivity.this);
//                addCommentsForSpecificNewsId(newsId);
            }
        });
    }

    private void initView() {
        commentTitleEt = findViewById(R.id.comment_title_et);
        commentContentEt = findViewById(R.id.comment_content_et);
        addCommentsBtn = findViewById(R.id.add_comments_two_btn);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AddCommentsActivity.this, NewsDetails.class);
        Bundle bundle = new Bundle();
        bundle.putString("newsId", newsId);
        bundle.putString("imageUrl", imageUrl);
        bundle.putString("newsTitle", newsTitle);
        bundle.putString("newsContent", newsContent);
        bundle.putString("newsUrl", newsUrl);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }


    //todo: implement a function to get intent from another page
    private void getIntentData() {
        Intent intent = getIntent();
        this.newsId = intent.getExtras().getString("newsId");
        this.newsTitle = intent.getExtras().getString("newsTitle");
        this.imageUrl = intent.getExtras().getString("imageUrl");
        this.newsContent = intent.getExtras().getString("newsContent");
        this.newsUrl = intent.getExtras().getString("newsUrl");
    }

    private void showAlertDialog(Context context){
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(context)
                .setTitle("Confirm creation")
                .setMessage("Are you sure you want to create a comment?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        addCommentsForSpecificNewsId(newsId);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert);
        AlertDialog ad = adBuilder.create();
        ad.show();

        Button noBtn = ad.getButton(DialogInterface.BUTTON_NEGATIVE);
        noBtn.setTextColor(Color.BLACK);
        Button yesBtn = ad.getButton(DialogInterface.BUTTON_POSITIVE);
        yesBtn.setTextColor(Color.BLUE);
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
                Intent intent = new Intent(AddCommentsActivity.this, NewsDetails.class);
                Bundle bundle = new Bundle();
                bundle.putString("newsId", newsId);
                bundle.putString("newsTitle", newsTitle);
                bundle.putString("imageUrl", imageUrl);
                bundle.putString("newsContent", newsContent);
                bundle.putString("newsUrl", newsUrl);
                intent.putExtras(bundle);
                startActivity(intent);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddCommentsActivity.this, "Successfully add a comment!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            //todo: toast message
        });
    }
}