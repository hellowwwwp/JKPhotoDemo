package com.dealuck.demo.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.lang.reflect.Field;

public class FixedViewPager extends ViewPager {

    private final String TAG = "ViewPagerFixed";

    //是否允许滚动
    private boolean mScrollEnabled = true;

    public FixedViewPager(Context context) {
        this(context, null);
    }

    public FixedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScrollDuration(600);
    }

    public void setScrollDuration(int duration) {
        try {
            ViewPagerScroll scroller = new ViewPagerScroll(getContext());
            scroller.setFixDuration(duration);
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            field.set(this, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否禁止滚动
     */
    public void setScrollEnabled(boolean enabled) {
        this.mScrollEnabled = enabled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return mScrollEnabled && super.onTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return mScrollEnabled && super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return mScrollEnabled && super.canScrollHorizontally(direction);
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return mScrollEnabled && super.canScrollVertically(direction);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        return mScrollEnabled && super.canScroll(v, checkV, dx, x, y);
    }
}
