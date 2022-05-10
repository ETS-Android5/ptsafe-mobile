package com.example.ptsafe;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.ptsafe.R;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_home:
                replaceFragment(new HomeFragment());
                break;
            case R.id.menu_detect_crowding:
                replaceFragment(new CrowdingDetectFragment());
                break;
            case R.id.menu_news:
                replaceFragment(new NewsFragment());
                break;
            case R.id.menu_emergency_call:
                replaceFragment(new EmergencyFragment());
                break;
            case R.id.menu_find_a_station:
                replaceFragment(new FindStationFragment());
                break;
            case R.id.menu_meditation:
                replaceFragment(new MeditationFragment());
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment(Fragment nextFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, nextFragment);
        fragmentTransaction.commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fragment homeFragment = new HomeFragment();
        Fragment findStationFragment = new FindStationFragment();
        Intent fromLogin = getIntent();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nv);
        toggle = new ActionBarDrawerToggle(this,
                drawerLayout,R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);
        //homeFragment.setArguments(bundle);
        try {
            Bundle extras = getIntent().getExtras();
            String carriageDetailsIndicator = extras.getString("fromCarriageDetails");
            if (carriageDetailsIndicator != null) {
                replaceFragment(findStationFragment);
            }
        }
        catch(Exception e) {
            replaceFragment(homeFragment);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }
}