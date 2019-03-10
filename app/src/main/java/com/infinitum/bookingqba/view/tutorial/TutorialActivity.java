package com.infinitum.bookingqba.view.tutorial;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityTutorialBinding;
import com.infinitum.bookingqba.view.adapters.TutorialPagerAdapter;
import com.infinitum.bookingqba.view.home.HomeActivity;
import com.infinitum.bookingqba.view.interaction.PageFourInteraction;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.DaggerAppCompatActivity;
import dagger.android.support.HasSupportFragmentInjector;
import de.mateware.snacky.Snacky;

public class TutorialActivity extends DaggerAppCompatActivity implements ViewPager.OnPageChangeListener
        , View.OnClickListener, PageFourInteraction, HasSupportFragmentInjector {

    private WeakReference<Fragment> wFragment;
    private TutorialPagerAdapter adapterViewPager;


    private ActivityTutorialBinding tutorialBinding;

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);
        tutorialBinding = DataBindingUtil.setContentView(this, R.layout.activity_tutorial);

        tutorialBinding.btnUpdate.setOnClickListener(this);
        tutorialBinding.btnNext.setOnClickListener(this);
        tutorialBinding.btnBack.setOnClickListener(this);

        adapterViewPager = new TutorialPagerAdapter(getSupportFragmentManager());

        tutorialBinding.onboardingViewpager.setAdapter(adapterViewPager);
        tutorialBinding.onboardingViewpager.addOnPageChangeListener(this);
        tutorialBinding.dotsIndicator.setViewPager(tutorialBinding.onboardingViewpager);

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


    public void updateBtnAndAnimate(int position) {
        if (position > 0 && position < 3) {
            showAnimateView(tutorialBinding.btnBack);
            tutorialBinding.btnUpdate.setVisibility(View.GONE);
            tutorialBinding.btnNext.setVisibility(View.VISIBLE);
        } else if (position == 0) {
            hideEnableView(tutorialBinding.btnBack);
            tutorialBinding.btnNext.setVisibility(View.VISIBLE);
            tutorialBinding.btnUpdate.setVisibility(View.GONE);
        } else if (position == 3) {
            tutorialBinding.btnNext.setVisibility(View.GONE);
            tutorialBinding.btnUpdate.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update: {
                wFragment = new WeakReference<>(adapterViewPager.getRegisteredFragment(3));
                ((PageFourFragment) wFragment.get()).startDownload();
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


    @Override
    public void onDownloadSuccess() {
        Snacky.builder()
                .setActivity(TutorialActivity.this)
                .setText("Actualizacion exitosa")
                .setDuration(1500)
                .success()
                .show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(TutorialActivity.this, HomeActivity.class));
                TutorialActivity.this.finish();
            }
        },1500);
    }

    @Override
    public void onDownloadError(String msg) {
        Snacky.builder()
                .setActivity(TutorialActivity.this)
                .setText(msg)
                .setDuration(Snacky.LENGTH_SHORT)
                .error()
                .show();
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
