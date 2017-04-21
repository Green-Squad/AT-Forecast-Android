package com.greensquad.atforecast;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.greensquad.atforecast.base.BaseActivity;
import com.greensquad.atforecast.fragments.StateListFragment;

import butterknife.OnItemClick;

public class MainActivity extends BaseActivity  implements ConnectionCallbacks, OnConnectionFailedListener {

    static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView drawerList;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private GoogleApiClient mGoogleApiClient = null;
    protected Location mLastLocation;
    private final static int MY_PERMISSIONS_LOCATION = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        drawerList = (NavigationView) findViewById(R.id.nav_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setupNavigationItems();
        setupDrawerAndToggle();
        showStateList();
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
                Log.d(LOG_TAG, "GPS");
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.d(LOG_TAG, "GRANTED");
                       findGPS();
                    } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_LOCATION);
                    Log.d(LOG_TAG, "REQUEST");
                }


                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    findGPS();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void findGPS() {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            Log.d(LOG_TAG, "LAST LOCATION NULL?");
            if (mLastLocation != null) {
                Log.d(LOG_TAG, "NOT NULL");
                Toast.makeText(this, String.valueOf(mLastLocation.getLatitude()) + " " + String.valueOf(mLastLocation.getLongitude()), Toast.LENGTH_SHORT).show();
            }
        } catch(SecurityException se) {

        }
    }

    private void setupDrawerAndToggle() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                setDrawerIndicatorEnabled(true);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    private void setupNavigationItems() {
        String[] navigationItems = {"Books", "Random Book", "Settings"};
        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, navigationItems);
        //drawerList.setAdapter(mAdapter);
    }

    @OnItemClick(R.id.nav_view)
    public void onItemClick(int index) {
        switch (index) {
            case 0:
                //showBookingList();
                break;
            case 1:
                //showRandom();
                break;
            case 2:
                //showSettings();
                break;
            default:
                break;
        }
        drawerLayout.closeDrawer(drawerList);
    }

    private void showStateList() {
        add(StateListFragment.newInstance());
    }

    @Override
    protected DrawerLayout getDrawer() {
        return drawerLayout;
    }

    @Override
    protected ActionBarDrawerToggle getDrawerToggle() {
        return mDrawerToggle;
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}