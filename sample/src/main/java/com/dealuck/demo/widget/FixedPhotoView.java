package com.dealuck.demo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

/**
 * https://github.com/chrisbanes/PhotoView
 * Implementation of ImageView for Android that supports zooming, by various touch gestures.
 * <p>
 * 基于 即刻 效果修改的PhotoView
 *
 * @author wangpan 2019/05/10
 */
public class FixedPhotoView extends PhotoView {

    private OnPhotoViewClickListener mOnPhotoViewClickListener;

    public FixedPhotoView(Context context) {
        this(context, null);
    }

    public FixedPhotoView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public FixedPhotoView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        //默认设置最低缩放0.4f
        setMinimumScale(0.4f);
        //设置缩放动画时长
        setZoomTransitionDuration(300);
        PhotoViewAttacher photoViewAttacher = getAttacher();
        photoViewAttacher.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent event) {
                if (mOnPhotoViewClickListener != null) {
                    mOnPhotoViewClickListener.onPhotoViewClick(FixedPhotoView.this);
                }
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent event) {
                try {
                    float scale = getScale();
                    float x = event.getX();
                    float y = event.getY();
                    if (scale < getMediumScale()) {
                        setScale(getMediumScale(), x, y, true);
                    } else {
                        setScale(1f, x, y, true);
                    }
                } catch (Exception e) {
                    // Can sometimes happen when getX() and getY() is called
                }
                return true;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent event) {
                //no op
                return false;
            }
        });
    }

    public void setOnPhotoViewClickListener(OnPhotoViewClickListener listener) {
        this.mOnPhotoViewClickListener = listener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean handleTouchEvent = super.dispatchTouchEvent(event);
        //手指抬起时还原缩放到1f
        if (event.getAction() == MotionEvent.ACTION_UP && getScale() < 1f) {
            setScale(1f, true);
        }
        return handleTouchEvent;
    }

    public interface OnPhotoViewClickListener {

        void onPhotoViewClick(FixedPhotoView photoView);
    }
}
