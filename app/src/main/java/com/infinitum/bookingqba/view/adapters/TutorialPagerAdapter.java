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

import java.lang.ref.WeakReference;
import java.util.List;

import timber.log.Timber;

public class TutorialPagerAdapter extends FragmentStatePagerAdapter {
    private WeakReference<Fragment> weakReference;
    private SparseArray<WeakReference<Fragment>>registerdFragments = new SparseArray<>();

    public TutorialPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return PageOneFragment.newInstance();
            case 1:
                return PageTwoFragment.newInstance();
            case 2:
                return PageThreeFragment.newInstance();
            case 3:
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
        weakReference = new WeakReference<>((Fragment) super.instantiateItem(container, position));
        registerdFragments.put(position,weakReference);
        return registerdFragments.get(position).get();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registerdFragments.remove(position);
        super.destroyItem(container, position, object);
    }


    public Fragment getRegisteredFragment(int position){
        return registerdFragments.get(position).get();
    }
}
