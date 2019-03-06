package com.infinitum.bookingqba.view.tutorial;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.view.adapters.TutorialPagerAdapter;
import com.moos.library.CircleProgressView;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.DaggerAppCompatActivity;
import dagger.android.support.HasSupportFragmentInjector;
import timber.log.Timber;

public class TutorialActivity extends DaggerAppCompatActivity implements ViewPager.OnPageChangeListener
        , View.OnClickListener, PageFourInterface, HasSupportFragmentInjector {

    private Button mBtnBack, mBtnNext, mBtnUpdate;
    private Fragment mFragment;
    private ViewPager vpPager;
    private TutorialPagerAdapter adapterViewPager;
    private DotsIndicator dotsIndicator;

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);
        setContentView(R.layout.activity_tutorial);

        mBtnBack = findViewById(R.id.btn_back);
        mBtnNext = findViewById(R.id.btn_next);
        mBtnUpdate = findViewById(R.id.btn_update);
        mBtnUpdate.setOnClickListener(this);


        dotsIndicator = findViewById(R.id.dots_indicator);
        vpPager = (ViewPager) findViewById(R.id.onboarding_viewpager);
        adapterViewPager = new TutorialPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        vpPager.addOnPageChangeListener(this);
        dotsIndicator.setViewPager(vpPager);

    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        updateBtnAndAnimate(position);
    }


    public void updateBtnAndAnimate(int position){
        if (position > 0 && position < 3) {
            mBtnBack.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            mBtnUpdate.setVisibility(View.GONE);
        } else if (position == 0) {
            mBtnBack.setVisibility(View.GONE);
            mBtnNext.setVisibility(View.VISIBLE);
            mBtnUpdate.setVisibility(View.GONE);
        } else if (position == 3) {
            mBtnNext.setVisibility(View.GONE);
            mBtnUpdate.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update: {
                mFragment = (Fragment) adapterViewPager.getRegisteredFragment(3);
                ((PageFourFragment) mFragment).startDownload();
            } break;
        }
    }

    @Override
    public void onDownloadFinished() {

    }
}
