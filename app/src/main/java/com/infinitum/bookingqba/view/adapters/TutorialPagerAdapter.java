package com.infinitum.bookingqba.view.adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.infinitum.bookingqba.view.tutorial.PageFourFragment;
import com.infinitum.bookingqba.view.tutorial.PageOneFragment;
import com.infinitum.bookingqba.view.tutorial.PageThreeFragment;
import com.infinitum.bookingqba.view.tutorial.PageTwoFragment;

import java.util.List;

public class TutorialPagerAdapter extends FragmentStatePagerAdapter {
    private SparseArray<Fragment>registerdFragments = new SparseArray<>();

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
            case 2: // Fragment # 0 - This will show FirstFragment different title
                return PageThreeFragment.newInstance();
            case 3: // Fragment # 0 - This will show FirstFragment different title
                return PageFourFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registerdFragments.put(position,fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registerdFragments.remove(position);
        super.destroyItem(container, position, object);
    }


    public Fragment getRegisteredFragment(int position){
        return registerdFragments.get(position);
    }
}
