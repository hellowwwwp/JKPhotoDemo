package com.dealuck.demo.widget;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class ViewPagerScroll extends Scroller {

    private int mDuration = 1000;

    public ViewPagerScroll(Context context) {
        super(context);
    }

    public ViewPagerScroll(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    public void setFixDuration(int duration) {
        mDuration = duration;
    }

    public int getFixDuration() {
        return mDuration;
    }
}