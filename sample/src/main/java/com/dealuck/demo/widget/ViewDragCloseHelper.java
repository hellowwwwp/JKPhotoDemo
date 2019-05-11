package com.dealuck.demo.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.FloatRange;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Android 仿微信朋友圈图片拖拽返回
 * https://mp.weixin.qq.com/s/U7twuqKXo4IDyU4_1MLCEg
 *
 * @author bauerbao  鸿洋
 * @modify wangpan 2019/05/10
 */
public class ViewDragCloseHelper {

    private static final String TAG = "drag_tag";

    private ViewConfiguration viewConfiguration;
    //还原动画执行时长
    private long mResetAnimationDuration = 200;
    //滑动边界距离
    private int mMaxExitY = 200;
    //最小的缩放尺寸
    private float mMinScale = 0.4f;
    //拖拽是否准备
    private boolean mDragPrepared;
    //是否在滑动关闭中，手指还在触摸中
    private boolean mDraggingFlag;
    //上次触摸坐标
    private float mLastX, mLastY;
    //上次触摸手指id
    private int mLastPointerId;
    //上次位移距离
    private float mLastTranX, mLastTranY;
    //原位动画
    private ValueAnimator mTranAnimator;
    //父View和子View
    private View mParentView, mChildView;
    //是否适配ViewPager
    private boolean mFitViewPager;
    //拖拽监听
    private ViewDragCloseListener mViewDragCloseListener;
    //是否只允许下滑关闭
    private boolean mOnlyDownClose = true;
    //是否只允许纵向拖拽
    private boolean mOnlyVerticalDrag = false;
    //是否禁用拖拽缩放
    private boolean mDisableDragScale = false;

    public ViewDragCloseHelper(Context mContext) {
        viewConfiguration = ViewConfiguration.get(mContext);
    }

    public void setViewDragCloseListener(ViewDragCloseListener dragCloseListener) {
        this.mViewDragCloseListener = dragCloseListener;
    }

    /**
     * 设置拖拽关闭的view
     */
    public void setDragView(View parentView, View childView) {
        this.mParentView = parentView;
        this.mChildView = childView;
    }

    /**
     * 设置复原动画时长
     */
    public void setResetAnimationDuration(long duration) {
        this.mResetAnimationDuration = duration;
    }

    /**
     * 设置最大退出距离
     */
    public void setMaxExitY(int maxExitY) {
        this.mMaxExitY = maxExitY;
    }

    /**
     * 设置最小缩放尺寸
     */
    public void setMinScale(@FloatRange(from = 0.1f, to = 1.0f) float minScale) {
        this.mMinScale = minScale;
    }

    /**
     * 设置是否只允许下滑关闭
     */
    public void setOnlyDownClose(boolean onlyDownClose) {
        this.mOnlyDownClose = onlyDownClose;
    }

    /**
     * 设置是否只允许纵向拖拽
     */
    public void setOnlyVerticalDrag(boolean onlyVerticalDrag) {
        this.mOnlyVerticalDrag = onlyVerticalDrag;
    }

    /**
     * 设置是否禁用拖拽缩放
     */
    public void setDisableDragScale(boolean disableDragScale) {
        this.mDisableDragScale = disableDragScale;
    }

    /**
     * 是否有拖拽
     */
    public boolean hasDragged() {
        return mLastTranX != 0 || mLastTranY != 0;
    }

    /**
     * 设置是否适配ViewPager
     */
    public void setFitViewPager(boolean fitViewPager) {
        this.mFitViewPager = fitViewPager;
    }

    /**
     * 处理touch事件
     */
    public boolean processTouchEvent(MotionEvent event) {
        if (mViewDragCloseListener != null && mViewDragCloseListener.shouldIntercept()) {
            //事件需要被外部拦截
            Log.e(TAG, "processMotionEvent 事件拦截: " + event.getAction());
            resetFlag();
            return false;
        } else {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                //取消还原动画
                cancelResetAnimation();
                mDragPrepared = true;
                mDraggingFlag = false;
                mLastPointerId = event.getPointerId(0);
                mLastX = event.getRawX();
                mLastY = event.getRawY();
                Log.e(TAG, "processMotionEvent ACTION_DOWN: " + mLastPointerId);
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                Log.e(TAG, "processMotionEvent ACTION_MOVE: " + event.getPointerCount() + ", " + mDragPrepared + ", " + mDraggingFlag);
                if (!mDragPrepared) {
                    //滑动流程没有经过ACTION_DOWN, 不执行滑动操作
                    Log.e(TAG, "滑动流程没有经过ACTION_DOWN, 不执行滑动操作");
                    boolean processTouchEvent = false;
                    if (mDraggingFlag) {
                        resetChildView();
                        processTouchEvent = true;
                    }
                    //重置标记
                    resetFlag();
                    return processTouchEvent;
                }
                if (event.getPointerCount() > 1 || event.getPointerId(0) != mLastPointerId) {
                    //有多个手指或者移动手指和按下手指不一致, 复原
                    Log.e(TAG, "多个手指或者手指不一致, 复原");
                    boolean processTouchEvent = false;
                    if (mDraggingFlag) {
                        resetChildView();
                        processTouchEvent = true;
                    }
                    //重置标记
                    resetFlag();
                    return processTouchEvent;
                }
                float currentX = event.getRawX();
                float currentY = event.getRawY();
                float xDiff = Math.abs(currentX - mLastX);
                float yDiff = Math.abs(currentY - mLastY);
                //判断是否达到触发滑动条件
                if (mDraggingFlag || yDiff > 2 * viewConfiguration.getScaledTouchSlop()) {
                    Log.e(TAG, "processMotionEvent ACTION_MOVE start: " + currentX + ", " + currentY);
                    if (!mDraggingFlag) {
                        //适配ViewPager, 如果横向滑动大于纵向滑动就不处理拖拽
                        if (mFitViewPager && xDiff >= yDiff) {
                            //重置标记
                            resetFlag();
                            return false;
                        }
                        mDraggingFlag = true;
                        //回调开始拖拽
                        if (mViewDragCloseListener != null) {
                            mViewDragCloseListener.onDragStart();
                        }
                    }
                    //更新childView
                    float tranX;
                    if (mOnlyVerticalDrag) {
                        tranX = mLastTranX;
                    } else {
                        tranX = mLastTranX + currentX - mLastX;
                    }
                    float tranY = mLastTranY + currentY - mLastY;
                    float percent = calculateDragPercent(tranY);
                    updateChildView(tranX, tranY, percent);
                    mLastX = currentX;
                    mLastY = currentY;
                    mLastTranX = tranX;
                    mLastTranY = tranY;
                    return true;
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                Log.e(TAG, "processMotionEvent ACTION_UP: " + mLastTranY + ", " + mDragPrepared + ", " + mDraggingFlag);
                boolean processTouchEvent = false;
                if (mDragPrepared && mDraggingFlag) {
                    if (canDragClose()) {
                        if (mViewDragCloseListener != null) {
                            mViewDragCloseListener.onDragClose();
                        }
                    } else {
                        resetChildView();
                    }
                    processTouchEvent = true;
                }
                //重置标记
                resetFlag();
                return processTouchEvent;
            }
        }
        return false;
    }

    private boolean canDragClose() {
        return (mOnlyDownClose && mLastTranY > mMaxExitY) || (!mOnlyDownClose && Math.abs(mLastTranY) > mMaxExitY);
    }

    /**
     * 计算拖拽进度
     */
    private float calculateDragPercent(float tranY) {
        float maxTranY = mChildView.getHeight();
        float percent = Math.abs(tranY / maxTranY);
        if (percent > 1f) {
            percent = 1f;
        } else if (percent < 0) {
            percent = 0;
        }
        return percent;
    }

    /**
     * 更新childView
     */
    private void updateChildView(float tranX, float tranY, float percent) {
        mChildView.setTranslationX(tranX);
        mChildView.setTranslationY(tranY);
        if (!mDisableDragScale) {
            float scale = 1f - percent;
            if (scale > 1f) {
                scale = 1f;
            } else if (scale < mMinScale) {
                scale = mMinScale;
            }
            mChildView.setScaleX(scale);
            mChildView.setScaleY(scale);
        }

        if (mViewDragCloseListener != null) {
            mViewDragCloseListener.onDragging(percent);
        }
    }

    /**
     * 取消还原动画
     */
    private void cancelResetAnimation() {
        if (mTranAnimator != null && mTranAnimator.isRunning()) {
            mTranAnimator.cancel();
            mTranAnimator = null;
        }
    }

    private boolean isResetting() {
        return mTranAnimator != null && mTranAnimator.isRunning();
    }

    /**
     * 动画恢复到原位
     */
    public void resetChildView() {
        //如果正在复原或者之前没有移动
        if (isResetting() || !hasDragged()) {
            return;
        }
        //重置标记
        resetFlag();
        float startTranX = mLastTranX;
        float startTranY = mLastTranY;
        mTranAnimator = ValueAnimator.ofFloat(1f, 0);
        mTranAnimator.setDuration(mResetAnimationDuration);
        mTranAnimator.addUpdateListener(valueAnimator -> {
            float ratio = (float) valueAnimator.getAnimatedValue();
            float tranX;
            if (mOnlyVerticalDrag) {
                tranX = startTranX;
            } else {
                tranX = startTranX * ratio;
            }
            float tranY = startTranY * ratio;
            float percent = calculateDragPercent(tranY);
            updateChildView(tranX, tranY, percent);
            mLastTranX = tranX;
            mLastTranY = tranY;
        });
        mTranAnimator.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //回调拖拽取消
                if (mViewDragCloseListener != null) {
                    mViewDragCloseListener.onDragCancel();
                }
            }
        });
        mTranAnimator.start();
    }

    private void resetFlag() {
        mDragPrepared = false;
        mDraggingFlag = false;
    }

    public interface ViewDragCloseListener {

        //是否有拦截
        boolean shouldIntercept();

        //开始拖拽
        void onDragStart();

        //拖拽中
        void onDragging(float percent);

        //取消拖拽
        void onDragCancel();

        //拖拽结束并且关闭
        void onDragClose();
    }
}
