package com.example.ptsafe.networkconnection;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkConnection {

    //stores all necessary OkHttp variables and query results variables
    private OkHttpClient client = null;
    private String results;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    //public constructor
    public NetworkConnection(){
        client= new OkHttpClient();
    }

    //stores URL
    private static final String BASE_URL = "https://ptsafe-backend.herokuapp.com/";
    private static final String NEWS_URL = "v1/news/";

    //get news data
    public String getAllNews(){
        final String endpointPath = "findAll";
        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + NEWS_URL + endpointPath);
        Request request = builder.build();
        try {
            Response response = client.newCall(request).execute();
            results = response.body().string();
        }catch (Exception e){
            e.printStackTrace();
        }
        return results;
    }
}
