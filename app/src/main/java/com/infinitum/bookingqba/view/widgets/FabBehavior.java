package com.infinitum.bookingqba.view.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class FabBehavior extends FloatingActionButton.Behavior {

    public FabBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        boolean ret = false;
        if(axes == ViewCompat.SCROLL_AXIS_VERTICAL){
            ret = true;
        }else{
            ret = super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
        }
        return ret;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);

        if (dyConsumed > 0) {
            if(child.getVisibility() == View.VISIBLE) {
                child.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                               @Override
                               public void onHidden(FloatingActionButton fab) {
                                   super.onHidden(fab);
                                   ((View) fab).setVisibility(View.INVISIBLE);
                                   fab.setEnabled(false);
                               }
                           }
                );
            }
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            child.show(new FloatingActionButton.OnVisibilityChangedListener() {
                @Override
                public void onShown(FloatingActionButton fab) {
                    super.onShown(fab);
                    fab.setEnabled(true);
                }
            });
        }
    }
}

