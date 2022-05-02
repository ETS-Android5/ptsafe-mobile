package com.example.ptsafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ptsafe.model.Comment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText passwordEt;
    private Button loginBtn;
    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getPermission();
        initView();
        loginBtn.setOnClickListener(setLoginListener());
    }

    private void initView() {
        passwordEt = findViewById(R.id.password_et);
        loginBtn = findViewById(R.id.login_btn);
    }

    private View.OnClickListener setLoginListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCredentialByUserIdAndPassword(passwordEt);
            }
        };
    }

    private void getPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
    }

    private boolean isPasswordEtEmpty(EditText passwordEt) {
        String password = passwordEt.getText().toString();
        if (password.matches("")) {
            return true;
        }
        return false;
    }

    private void getCredentialByUserIdAndPassword(EditText passwordEt){
        if (isPasswordEtEmpty(passwordEt)) {
            Toast.makeText(this, "You did not enter a password", Toast.LENGTH_SHORT).show();
        }
        else {
            String password = passwordEt.getText().toString();
            OkHttpClient client = new OkHttpClient();
            String url = "http://ptsafenodejsapi-env.eba-cx9pgkwu.us-east-1.elasticbeanstalk.com/v1/credential/findByUserNameAndPassword?username=ta27_ptsafe&password=" + password;
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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "Successfully logged in to the system!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                        else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, (String) item, Toast.LENGTH_SHORT).show();
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
}