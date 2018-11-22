package com.greensquad.atforecast;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.google.gson.Gson;
import com.greensquad.atforecast.base.BaseActivity;
import com.greensquad.atforecast.fragments.ShelterDetailFragment;
import com.greensquad.atforecast.fragments.ShelterListFragment;
import com.greensquad.atforecast.fragments.StateListFragment;
import com.greensquad.atforecast.models.Shelter;

import net.mediavrog.irr.DefaultOnToggleVisibilityListener;
import net.mediavrog.irr.DefaultRuleEngine;
import net.mediavrog.irr.IrrLayout;

import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

public class MainActivity extends BaseActivity implements OnLocationUpdatedListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int LOCATION_PERMISSION_ID = 1001;
    private static final int UNIT_TYPE_F = 0;
    private static final int UNIT_TYPE_C = 1;

    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    private View loadingBar;
    private SharedPreferences sharedPref;

    protected IrrLayout irr;
    protected DefaultRuleEngine engine;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        int defaultValue = AppCompatDelegate.MODE_NIGHT_NO;
        int nightMode = sharedPref.getInt("nightMode", defaultValue);
        int unitType = sharedPref.getInt("unitType", 0);
        setNightMode(nightMode, false);
        setUnitType(unitType, false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        loadingBar = findViewById(R.id.loadingPanel);

        setupDrawerAndToggle();
        if (savedInstanceState == null) {
            showStateList();
        } else {
            syncDrawerToggleState();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        DefaultRuleEngine.trackAppStart(this);
        initializeRating();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final Menu fMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        searchView.setQueryHint("NOBO Mile. e.g. 630");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchByMileage(query);
                searchView.setIconified(true);
                MenuItemCompat.collapseActionView(fMenu.findItem(R.id.search));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (AppCompatDelegate.getDefaultNightMode()) {
            case AppCompatDelegate.MODE_NIGHT_AUTO:
                menu.findItem(R.id.menu_night_mode_auto).setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                menu.findItem(R.id.menu_night_mode_night).setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_NO:
                menu.findItem(R.id.menu_night_mode_day).setChecked(true);
                break;
        }

        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        int storedUnitType = prefs.getInt("unitType", 0);

        switch (storedUnitType) {
            case UNIT_TYPE_F:
                menu.findItem(R.id.menu_units_fahrenheit).setChecked(true);
                break;
            case UNIT_TYPE_C:
                menu.findItem(R.id.menu_units_celsius).setChecked(true);
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.gps:
                // Location permission not granted
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
                    return false;
                }
                findGPS();
                break;
            case R.id.menu_night_mode_day:
                setNightMode(AppCompatDelegate.MODE_NIGHT_NO, true);
                break;
            case R.id.menu_night_mode_night:
                setNightMode(AppCompatDelegate.MODE_NIGHT_YES, true);
                break;
            case R.id.menu_night_mode_auto:
                setNightMode(AppCompatDelegate.MODE_NIGHT_AUTO, true);
                break;
            case R.id.menu_units_fahrenheit:
                setUnitType(UNIT_TYPE_F, true);
                break;
            case R.id.menu_units_celsius:
                setUnitType(UNIT_TYPE_C, true);
                break;
        }

        return super.onOptionsItemSelected(item);
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
        ShelterListFragment shelterListFragment = ShelterListFragment.newInstance("Nearest Shelters", shelterList);
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

    @Override
    protected ActionBarDrawerToggle getDrawerToggle() {
        return mDrawerToggle;
    }

    private void setNightMode(@AppCompatDelegate.NightMode int nightMode, boolean setNew) {
        AppCompatDelegate.setDefaultNightMode(nightMode);
        if(setNew) {
            sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("nightMode", nightMode);
            editor.apply();
            recreate();
        }
    }

    private void setUnitType(int unitType, boolean setNew) {
        if(setNew) {
            sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("unitType", unitType);
            editor.apply();
            recreate();
        }
    }

    private void findGPS() {
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

    private void searchByMileage(String query) {
        try {
            double mileage = Double.parseDouble(query);
            Shelter shelter = Shelter.findByNearestMileage(mileage);
            ShelterDetailFragment shelterDetailFragment = ShelterDetailFragment.newInstance(shelter.getShelterId());
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().setCustomAnimations(
                    R.anim.fragment_slide_left_enter,
                    R.anim.fragment_slide_left_exit,
                    R.anim.fragment_slide_right_enter,
                    R.anim.fragment_slide_right_exit)
                    .replace(
                            R.id.fragment_main,
                            shelterDetailFragment,
                            shelterDetailFragment.getTag()
                    ).addToBackStack("shelter_detail_fragment").commit();
        } catch (NumberFormatException nfe) {
            Toast.makeText(getApplicationContext(), "You must enter a valid mile number.", Toast.LENGTH_LONG).show();
        } catch (IndexOutOfBoundsException ioobe) {
            Toast.makeText(getApplicationContext(), "Sorry. Could not get any data for the nearest shelter.", Toast.LENGTH_LONG).show();
        }
    }

   private void setupDrawerAndToggle() {
        setSupportActionBar(toolbar);
        mDrawerToggle = new ActionBarDrawerToggle(this, new DrawerLayout(this), toolbar, 0, 0);
        mDrawerToggle.syncState();
    }

    private void showStateList() {
        add(StateListFragment.newInstance());
    }

    protected void initializeRating() {
        irr = (IrrLayout) findViewById(R.id.irr_layout);
        engine = (DefaultRuleEngine) irr.getRuleEngine();
        engine.setListener(new DefaultRuleEngine.DefaultOnUserDecisionListener() {
            @Override
            public void onAccept(Context ctx, IrrLayout.State s) {
                super.onAccept(ctx, s);
            }

            @Override
            public void onDismiss(Context ctx, IrrLayout.State s) {
                super.onDismiss(ctx, s);
            }
        });

        irr.setOnToggleVisibilityListener(new DefaultOnToggleVisibilityListener() {

            @Override
            public void onShow(final IrrLayout irr) {
                if (irr.getVisibility() != View.VISIBLE) {
                    TranslateAnimation anim = new TranslateAnimation(0, 0, -300, 0);
                    anim.setDuration(200);
                    anim.setFillAfter(true);
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            irr.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    irr.startAnimation(anim);
                }
            }

            @Override
            public void onHide(final IrrLayout irr) {
                if (irr.getVisibility() != View.GONE) {
                    TranslateAnimation anim = new TranslateAnimation(0, 0, 0, -300);
                    anim.setDuration(150);
                    anim.setFillAfter(true);
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            irr.setVisibility(View.GONE);
                            irr.clearAnimation();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    irr.startAnimation(anim);
                }
            }
        });
    }

}