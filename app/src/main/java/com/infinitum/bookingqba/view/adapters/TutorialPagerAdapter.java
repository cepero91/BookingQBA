package com.infinitum.bookingqba.view.adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import com.infinitum.bookingqba.view.tutorial.PageOneFragment;
import com.infinitum.bookingqba.view.tutorial.PageTwoFragment;

import java.util.List;

public class TutorialPagerAdapter extends FragmentPagerAdapter {

    public TutorialPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return PageOneFragment.newInstance();
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return PageTwoFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
