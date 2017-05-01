package com.greensquad.atforecast;

import android.Manifest;
import android.app.ActionBar;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.greensquad.atforecast.base.BaseActivity;
import com.greensquad.atforecast.fragments.ShelterListFragment;
import com.greensquad.atforecast.fragments.StateListFragment;
import com.greensquad.atforecast.models.Shelter;

import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

public class MainActivity extends BaseActivity implements OnLocationUpdatedListener {

    static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    private View loadingBar;

    private static final int LOCATION_PERMISSION_ID = 1001;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        loadingBar = findViewById(R.id.loadingPanel);

        setupDrawerAndToggle();
        if (savedInstanceState == null) {
            showStateList();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.gps:
                // Location permission not granted
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
                    return false;
                }

                findGPS();
                break;
            default:
                break;
        }

        return true;
    }

    private void findGPS() {
        Log.d(LOG_TAG, "FIND GPS");
        Toast.makeText(getApplicationContext(), "Finding GPS", Toast.LENGTH_LONG).show();
        loadingBar.setVisibility(View.VISIBLE);
        LocationParams params = new LocationParams.Builder()
                .setAccuracy(LocationAccuracy.HIGH)
                .setDistance(1f)
                .setInterval(5 * 1000)
                .build();
        SmartLocation smart = new SmartLocation.Builder(this).logging(true).build();
        smart.location().config(params).oneFix().start(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            findGPS();
        } else {
            Toast.makeText(getApplicationContext(), "Location is required to find nearest shelter.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLocationUpdated(Location location) {
        loadingBar.setVisibility(View.GONE);
        List<Shelter> sheltersList = Shelter.findByNearestCoords(location.getLatitude(), location.getLongitude());
        Gson gson = new Gson();
        String shelterList = gson.toJson(sheltersList);
        ShelterListFragment shelterListFragment = ShelterListFragment.newInstance("Current Location", shelterList);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().setCustomAnimations(
                R.anim.fragment_slide_left_enter,
                R.anim.fragment_slide_left_exit,
                R.anim.fragment_slide_right_enter,
                R.anim.fragment_slide_right_exit)
                .replace(
                        R.id.fragment_main,
                        shelterListFragment,
                        shelterListFragment.getTag()
                ).addToBackStack("shelter_list_fragment").commit();
    }

   private void setupDrawerAndToggle() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mDrawerToggle = new ActionBarDrawerToggle(this, new DrawerLayout(this), toolbar, 0, 0);
        mDrawerToggle.syncState();
    }

    private void showStateList() {
        add(StateListFragment.newInstance());
    }

    @Override
    protected ActionBarDrawerToggle getDrawerToggle() {
        return mDrawerToggle;
    }

}