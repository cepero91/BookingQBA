package com.infinitum.bookingqba.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.infinitum.bookingqba.R;

import java.util.ArrayList;
import java.util.List;

import viewpagerwc.ui.dom.wrapping.WrappingFragmentPagerAdapter;
import viewpagerwc.ui.dom.wrapping.WrappingFragmentStatePagerAdapter;
import viewpagerwc.ui.dom.wrapping.WrappingViewPager;

public class InnerViewPagerAdapter extends WrappingFragmentPagerAdapter {

    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();

    public InnerViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public InnerViewPagerAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> titleList) {
        super(fm);
        this.fragmentList = fragmentList;
        this.titleList = titleList;
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        titleList.add(title);
    }

    public void setFragmentList(List<Fragment> fragmentList){
        fragmentList.addAll(fragmentList);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }
}
