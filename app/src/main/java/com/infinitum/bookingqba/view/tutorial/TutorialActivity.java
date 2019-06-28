package com.infinitum.bookingqba.view.tutorial;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityTutorialBinding;
import com.infinitum.bookingqba.util.NetworkHelper;
import com.infinitum.bookingqba.view.adapters.TutorialPagerAdapter;
import com.infinitum.bookingqba.view.home.HomeActivity;
import com.infinitum.bookingqba.view.sync.SyncActivity;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.DaggerAppCompatActivity;
import dagger.android.support.HasSupportFragmentInjector;
import de.mateware.snacky.Snacky;

import static com.infinitum.bookingqba.util.Constants.PREF_FIRST_OPEN;

public class TutorialActivity extends DaggerAppCompatActivity implements ViewPager.OnPageChangeListener
        , View.OnClickListener {

    private TutorialPagerAdapter adapterViewPager;

    private ActivityTutorialBinding tutorialBinding;

    @Inject
    NetworkHelper networkHelper;

    @Inject
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);
        tutorialBinding = DataBindingUtil.setContentView(this, R.layout.activity_tutorial);

        tutorialBinding.btnBegin.setOnClickListener(this);
        tutorialBinding.btnNext.setOnClickListener(this);
        tutorialBinding.btnBack.setOnClickListener(this);

        adapterViewPager = new TutorialPagerAdapter(getSupportFragmentManager());

        tutorialBinding.onboardingViewpager.setAdapter(adapterViewPager);
        tutorialBinding.onboardingViewpager.addOnPageChangeListener(this);
        tutorialBinding.dotsIndicator.setViewPager(tutorialBinding.onboardingViewpager);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        updateBtnAndAnimate(position);
    }


    public void updateBtnAndAnimate(int position) {
        if (position > 0 && position < 2) {
            showAnimateView(tutorialBinding.btnBack);
            tutorialBinding.btnBegin.setVisibility(View.GONE);
            tutorialBinding.btnNext.setVisibility(View.VISIBLE);
        } else if (position == 0) {
            hideEnableView(tutorialBinding.btnBack);
            tutorialBinding.btnNext.setVisibility(View.VISIBLE);
            tutorialBinding.btnBegin.setVisibility(View.GONE);
        } else if (position == 2) {
            tutorialBinding.btnNext.setVisibility(View.GONE);
            tutorialBinding.btnBegin.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_begin: {
                saveFirstOpenToSharePref();
                startActivity(new Intent(TutorialActivity.this, SyncActivity.class));
                this.finish();
            }
            break;
            case R.id.btn_next: {
                int pos = tutorialBinding.onboardingViewpager.getCurrentItem()+1;
                tutorialBinding.onboardingViewpager.setCurrentItem(pos,true);
            }
            break;
            case R.id.btn_back: {
                int pos = tutorialBinding.onboardingViewpager.getCurrentItem()-1;
                tutorialBinding.onboardingViewpager.setCurrentItem(pos,true);
            }
            break;

        }
    }

    private void saveFirstOpenToSharePref() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_FIRST_OPEN,false);
        editor.apply();
    }

    private void showAnimateView(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 1f);
        animator.setDuration(500).setInterpolator(new AccelerateInterpolator());
        animator.start();
        view.setEnabled(true);
    }

    private void hideEnableView(View view) {
        view.setEnabled(false);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0f);
        animator.setDuration(300).setInterpolator(new AccelerateInterpolator());
        animator.start();
    }
}
