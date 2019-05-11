package com.dealuck.demo.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.dealuck.demo.utils.DensityUtil;

public class DragPhotoView extends FrameLayout implements ViewDragCloseHelper.ViewDragCloseListener {

    private FixedPhotoView mPhotoView;
    private ViewDragCloseHelper mViewDragCloseHelper;
    private OnDragPhotoListener mOnDragPhotoListener;

    public DragPhotoView(@NonNull Context context) {
        this(context, null);
    }

    public DragPhotoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragPhotoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPhotoView = new FixedPhotoView(context);
        mPhotoView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(mPhotoView);
        mViewDragCloseHelper = new ViewDragCloseHelper(context);
        mViewDragCloseHelper.setDragView(this, mPhotoView);
        mViewDragCloseHelper.setMaxExitY(DensityUtil.dp2px(context, 10));
        mViewDragCloseHelper.setViewDragCloseListener(this);
    }

    public FixedPhotoView getPhotoView() {
        return mPhotoView;
    }

    public void setMaxExitY(int maxExitY) {
        mViewDragCloseHelper.setMaxExitY(maxExitY);
    }

    public void setOnDragPhotoListener(OnDragPhotoListener listener) {
        this.mOnDragPhotoListener = listener;
    }

    public void setFitViewPager(boolean fitViewPager) {
        mViewDragCloseHelper.setFitViewPager(fitViewPager);
    }

    public void setOnlyDownClose(boolean onlyDownClose) {
        mViewDragCloseHelper.setOnlyDownClose(onlyDownClose);
    }

    public void setOnlyVerticalDrag(boolean onlyVerticalDrag) {
        mViewDragCloseHelper.setOnlyVerticalDrag(onlyVerticalDrag);
    }

    public void setDisableDragScale(boolean disableDragScale) {
        mViewDragCloseHelper.setDisableDragScale(disableDragScale);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getPointerCount() == 1 && mViewDragCloseHelper.processTouchEvent(event)) {
            return true;
        }
        if (mViewDragCloseHelper.hasDragged()) {
            mViewDragCloseHelper.resetChildView();
            return true;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean shouldIntercept() {
        return false;
    }

    @Override
    public void onDragStart() {
        //no op
    }

    @Override
    public void onDragging(float percent) {
        if (mOnDragPhotoListener != null) {
            mOnDragPhotoListener.onDragging(percent);
        }
    }

    @Override
    public void onDragCancel() {
        //no op
    }

    @Override
    public void onDragClose() {
        if (mOnDragPhotoListener != null) {
            mOnDragPhotoListener.onDragClose();
        }
    }

    public interface OnDragPhotoListener {

        void onDragging(float percent);

        void onDragClose();
    }
}
