package com.greensquad.atforecast;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;

import com.greensquad.atforecast.base.BaseActivity;
import com.greensquad.atforecast.fragments.StateListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class MainActivity extends BaseActivity {

    static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView drawerList;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerList = (NavigationView) findViewById(R.id.nav_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setupNavigationItems();
        setupDrawerAndToggle();
        showStateList();
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
}