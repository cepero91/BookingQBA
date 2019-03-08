package com.infinitum.bookingqba.view.tutorial;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.databinding.DataBindingUtil;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;

import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.ActivityTutorialBinding;
import com.infinitum.bookingqba.view.adapters.TutorialPagerAdapter;
import com.moos.library.CircleProgressView;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.DaggerAppCompatActivity;
import dagger.android.support.HasSupportFragmentInjector;
import de.mateware.snacky.Snacky;
import timber.log.Timber;

public class TutorialActivity extends DaggerAppCompatActivity implements ViewPager.OnPageChangeListener
        , View.OnClickListener,PageFourInterface, HasSupportFragmentInjector {

    private WeakReference<Fragment> wFragment;
    private TutorialPagerAdapter adapterViewPager;


    private ActivityTutorialBinding tutorialBinding;

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);
        tutorialBinding = DataBindingUtil.setContentView(this,R.layout.activity_tutorial);

        tutorialBinding.btnUpdate.setOnClickListener(this);
        tutorialBinding.btnNext.setOnClickListener(this);

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


    public void updateBtnAndAnimate(int position){
        if (position > 0 && position < 3) {
            showAnimateView(tutorialBinding.btnBack);
//            tutorialBinding.btnBack.setVisibility(View.VISIBLE);
            tutorialBinding.btnNext.setVisibility(View.VISIBLE);
            tutorialBinding.btnUpdate.setVisibility(View.GONE);
        } else if (position == 0) {
            tutorialBinding.btnBack.setVisibility(View.GONE);
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
            } break;
        }
    }


    @Override
    public void onDownloadSuccess() {

    }

    @Override
    public void onDownloadError(String msg) {
        Snackbar.make(tutorialBinding.flContentBtns,msg,Snackbar.LENGTH_SHORT).show();
    }

    private void showAnimateView(View view){
        view.setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view,"alpha",1,0);
        animator.setDuration(3000).setInterpolator(new AccelerateInterpolator());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(animator);
    }

    private void hideAnimateView(View view){
        ObjectAnimator animator = ObjectAnimator.ofFloat(view,"alpha",1);
        animator.setDuration(300).setInterpolator(new AccelerateInterpolator());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                view.setVisibility(View.GONE);
            }
        });
        animatorSet.play(animator);
    }
}
