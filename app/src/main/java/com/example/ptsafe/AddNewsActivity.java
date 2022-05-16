package com.example.ptsafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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
                showAlertDialog(AddNewsActivity.this);
//                addNews();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }


    private void initView() {
        newsTitleEt = findViewById(R.id.news_title_et);
        newsLabelSpinner = findViewById(R.id.news_submit_spinner);
        newsContentEt = findViewById(R.id.news_content_et);
        imageUrlEt = findViewById(R.id.image_url_et);
        newsUrlEt = findViewById(R.id.news_url_et);
        addNewsBtn = findViewById(R.id.add_news_two_btn);
    }

    private void showAlertDialog(Context context){
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(context)
                .setTitle("Confirm creation")
                .setMessage("Are you sure you want to create the news?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        addNews();
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddNewsActivity.this, "Successfully add a news!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}