package com.infinitum.bookingqba.view.tutorial;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.view.adapters.TutorialPagerAdapter;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import timber.log.Timber;

public class TutorialActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    private Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        btn_back = findViewById(R.id.btn_back);

        DotsIndicator dotsIndicator = findViewById(R.id.dots_indicator);
        ViewPager vpPager = (ViewPager) findViewById(R.id.onboarding_viewpager);
        TutorialPagerAdapter adapterViewPager = new TutorialPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        vpPager.addOnPageChangeListener(this);
        dotsIndicator.setViewPager(vpPager);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Timber.d("onPageScrolled position %s",String.valueOf(position));
    }

    @Override
    public void onPageSelected(int position) {
        Timber.d("onPageSelected position %s",String.valueOf(position));
        if(position > 0){
            btn_back.setVisibility(View.VISIBLE);
        }else if(position == 0){
            btn_back.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Timber.d("onPageScrollStateChanged state %s",String.valueOf(state));
    }
}
