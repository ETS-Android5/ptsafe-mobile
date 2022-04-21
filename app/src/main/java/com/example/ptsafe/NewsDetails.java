package com.example.ptsafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ptsafe.adapter.CommentAdapter;
import com.example.ptsafe.adapter.NewsAdapter;
import com.example.ptsafe.model.Comment;
import com.example.ptsafe.model.News;
import com.squareup.picasso.Picasso;

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

public class NewsDetails extends AppCompatActivity {
    private String newsId;
    private List<Comment> comments;
    private RecyclerView commentsRv;
    private ImageView newsIv;
    private TextView newsTv;
    private TextView contentTv;
    private TextView errorMessageTv;
    private Button addCommentsBtn;
    private Button readWebsiteBtn;
    private CommentAdapter adapter;
    private RecyclerView.Adapter itemsAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        comments = new ArrayList<>();
        initView();
        getAllCommentsByNewsId();
        readWebsiteBtn.setOnClickListener(setReadNewsBtnListener());
        addCommentsBtn.setOnClickListener(setAddCommentsBtnListener());
    }

    private View.OnClickListener setReadNewsBtnListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse(getNewsUrl()));
                startActivity(viewIntent);
            }
        };
    }
    private View.OnClickListener setAddCommentsBtnListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewsDetails.this, AddCommentsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("newsId", newsId);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        };
    }

    private void initView() {
        commentsRv = findViewById(R.id.comments_rv);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        newsIv = findViewById(R.id.news_detail_iv);
        newsTv = findViewById(R.id.news_detail_tv);
        contentTv = findViewById(R.id.abstract_tv);
        errorMessageTv = findViewById(R.id.error_message_tv);
        addCommentsBtn = findViewById(R.id.add_comments_btn);
        readWebsiteBtn = findViewById(R.id.read_website_btn);
        generateDetails();
    }

    private void generateDetails() {
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        setNewsId(data.getString("newsId"));
        Picasso.get().load(data.getString("imageUrl")).into(newsIv);
        newsTv.setText(data.getString("newsTitle"));
        contentTv.setText(data.getString("newsContent"));
    }

    private void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    private void embedDataToAdapter() {
        adapter = new CommentAdapter(comments);
        commentsRv.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                LinearLayoutManager.VERTICAL));
        commentsRv.setAdapter(adapter);
        commentsRv.setLayoutManager(layoutManager);
    }

    private String getNewsUrl() {
        Intent intent = getIntent();
        String newsUrl = intent.getExtras().getString("newsUrl");
        return newsUrl;
    }

    //get news data by implementing okhttp
    private void getAllCommentsByNewsId(){
        Intent intent = getIntent();
        newsId = intent.getExtras().getString("newsId");
        OkHttpClient client = new OkHttpClient();
        String url = "http://ptsafenodejsapi-env.eba-cx9pgkwu.us-east-1.elasticbeanstalk.com/v1/comment/findByNewsId?newsid=" + newsId;
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
                    if (item instanceof JSONArray){
                        data = resultObj.getJSONArray("message");
                        for(int i = 0; i < data.length(); i++) {
                            JSONObject obj = null;
                            try {
                                obj = data.getJSONObject(i);
                                String commentId = obj.getString("comment_id");
                                String newsId = obj.getString("news_id");
                                String commentTitle = obj.getString("comment_title");
                                String commentContent = obj.getString("comment_content");
                                Comment newComment = new Comment(commentId, newsId, commentTitle, commentContent);
                                comments.add(newComment);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                errorMessageTv.setVisibility(View.INVISIBLE);
                                embedDataToAdapter();
                            }
                        });
                    }
                    else {
                        runOnUiThread(new Runnable() {
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
    }
}