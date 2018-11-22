package com.greensquad.atforecast.base;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.greensquad.atforecast.R;

// The methods in this class are shared by both the BaseActivity and the BaseFragment.
// Really they belong in both classes, but I refactored them out here to prevent code duplication
public class AddFragmentHandler {
    private final FragmentManager fragmentManager;

    AddFragmentHandler(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void add(BaseFragment fragment) {
        //don't add a fragment of the same type on top of itself.
        BaseFragment currentFragment = getCurrentFragment();
        if (currentFragment != null) {
            if (currentFragment.getClass() == fragment.getClass()) {
                Log.w("Fragment Manager", "Tried to add a fragment of the same type to the backstack. This may be done on purpose in some circumstances but generally should be avoided.");
                return;
            }
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_main, fragment, fragment.getTitle());
        fragmentTransaction.addToBackStack(fragment.getTitle());
        fragmentTransaction.commit();
    }

    @Nullable
    BaseFragment getCurrentFragment() {
        if (fragmentManager.getBackStackEntryCount() == 0) {
            return null;
        }
        FragmentManager.BackStackEntry currentEntry = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1);

        String tag = currentEntry.getName();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        return (BaseFragment) fragment;
    }
}
