package com.greensquad.atforecast.base;

import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String LOG_TAG = BaseActivity.class.getSimpleName();

    private FragmentManager fragmentManager;
    private AddFragmentHandler fragmentHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentManager = getSupportFragmentManager();
        fragmentHandler = new AddFragmentHandler(fragmentManager);
        fragmentManager.addOnBackStackChangedListener(backStackListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        fragmentManager.removeOnBackStackChangedListener(backStackListener);
        fragmentManager = null;
        super.onDestroy();
    }

    protected void add(BaseFragment fragment) {
        fragmentHandler.add(fragment);
    }

    @Override
    public void onBackPressed() {
        if (sendBackPressToDrawer()) {
            //the drawer consumed the backpress
            return;
        }

        if (sendBackPressToFragmentOnTop()) {
            // fragment on top consumed the back press
            return;
        }

        //let the android system handle the back press, usually by popping the fragment
        super.onBackPressed();

        //close the activity if back is pressed on the root fragment
        if (fragmentManager.getBackStackEntryCount() == 0) {
            finish();
        }
    }

    final View.OnClickListener navigationBackPressListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            fragmentManager.popBackStack();
        }
    };

    FragmentManager.OnBackStackChangedListener backStackListener = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            onBackStackChangedEvent();
        }
    };

    private void onBackStackChangedEvent() {
        syncDrawerToggleState();
    }

    protected void syncDrawerToggleState() {
        ActionBarDrawerToggle drawerToggle = getDrawerToggle();
        if (drawerToggle == null) {
            return;
        }
        if (fragmentManager.getBackStackEntryCount() > 1) {
            drawerToggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            drawerToggle.setToolbarNavigationClickListener(navigationBackPressListener);
        } else {
            drawerToggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    private boolean sendBackPressToDrawer() {
        return false;
    }

    private boolean sendBackPressToFragmentOnTop() {
        BaseFragment fragmentOnTop = fragmentHandler.getCurrentFragment();
        if (fragmentOnTop == null) {
            return false;
        }
        if (!(fragmentOnTop instanceof BackButtonSupportFragment)) {
            return false;
        }
        return ((BackButtonSupportFragment) fragmentOnTop).onBackPressed();
    }

    protected abstract ActionBarDrawerToggle getDrawerToggle();

}
