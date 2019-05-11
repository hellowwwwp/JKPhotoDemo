package com.dealuck.demo.widget;

import android.graphics.Rect;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    public static final int LINEAR_LAYOUT = 0;
    public static final int GRID_LAYOUT = 1;
    public static final int STAGGERED_GRID_LAYOUT = 2;

    public static final int ORIENTATION_VERTICAL = 0;
    public static final int ORIENTATION_HORIZONTAL = 1;

    //限定为LINEAR_LAYOUT,GRID_LAYOUT,STAGGERED_GRID_LAYOUT
    @IntDef({LINEAR_LAYOUT, GRID_LAYOUT, STAGGERED_GRID_LAYOUT})
    //表示注解所存活的时间,在运行时,而不会存在. class 文件.
    @Retention(RetentionPolicy.SOURCE)
    public @interface LayoutType {
        int type() default LINEAR_LAYOUT;
    }

    //ORIENTATION_VERTICAL,ORIENTATION_HORIZONTAL
    @IntDef({ORIENTATION_VERTICAL, ORIENTATION_HORIZONTAL})
    //表示注解所存活的时间,在运行时,而不会存在. class 文件.
    @Retention(RetentionPolicy.SOURCE)
    public @interface OrientationType {
        int type() default ORIENTATION_VERTICAL;
    }

    private int leftRight;
    private int topBottom;
    /**
     * 头布局个数
     */
    private int headItemCount;
    /**
     * 边距
     */
    private int space;

    /**
     * 阴影边距，只做了双瀑布流兼容，其他需自己实现
     */
    private Rect shadowRect;

    /**
     * 时候包含边距
     */
    private boolean includeEdge;
    /**
     * 列数
     */
    private int spanCount;

    /**
     * LinearManager orientation
     */
    public @OrientationType
    int orientation;

    private @LayoutType
    int layoutManager;

    /**
     * GridLayoutManager or StaggeredGridLayoutManager spacing
     */
    public SpaceItemDecoration(int leftRight, int topBottom, int headItemCount, @LayoutType int layoutManager) {
        this.leftRight = leftRight;
        this.topBottom = topBottom;
        this.headItemCount = headItemCount;
        this.layoutManager = layoutManager;
    }

    /**
     * GridLayoutManager or StaggeredGridLayoutManager spacing
     */
    public SpaceItemDecoration(int space, boolean includeEdge, @LayoutType int layoutManager) {
        this(space, 0, includeEdge, layoutManager, ORIENTATION_VERTICAL);
    }

    /**
     * GridLayoutManager or StaggeredGridLayoutManager spacing
     */
    public SpaceItemDecoration(int space, boolean includeEdge, @LayoutType int layoutManager, int orientation) {
        this(space, 0, includeEdge, layoutManager, orientation);
    }

    /**
     * GridLayoutManager or StaggeredGridLayoutManager spacing
     */
    public SpaceItemDecoration(int space, int headItemCount, boolean includeEdge, @LayoutType int layoutManager) {
        this(space, headItemCount, includeEdge, layoutManager, ORIENTATION_VERTICAL);
    }

    /**
     * GridLayoutManager or StaggeredGridLayoutManager spacing
     */
    public SpaceItemDecoration(int space, int headItemCount, boolean includeEdge, @LayoutType int layoutManager, @OrientationType int orientation) {
        this.space = space;
        this.headItemCount = headItemCount;
        this.includeEdge = includeEdge;
        this.layoutManager = layoutManager;
        this.orientation = orientation;
    }

    /**
     * GridLayoutManager or StaggeredGridLayoutManager spacing
     */
    public SpaceItemDecoration(int space, int headItemCount, @LayoutType int layoutManager) {
        this(space, headItemCount, true, layoutManager, ORIENTATION_VERTICAL);
    }

    /**
     * LinearLayoutManager or GridLayoutManager or StaggeredGridLayoutManager spacing
     */
    public SpaceItemDecoration(int space, @LayoutType int layoutManager) {
        this(space, 0, true, layoutManager, ORIENTATION_VERTICAL);
    }

    /**
     * 设置边距，双瀑布流带有阴影，不好控制
     *
     * @param left   阴影
     * @param top    阴影
     * @param right  阴影
     * @param bottom 阴影
     */
    public void setShadowPadding(int left, int top, int right, int bottom) {
        shadowRect = new Rect(left, top, right, bottom);
    }

    /**
     * 设置边距，双瀑布流带有阴影，不好控制，
     *
     * @param rect 阴影边距
     */
    public void setShadowPadding(Rect rect) {
        shadowRect = rect;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        switch (layoutManager) {
            case LINEAR_LAYOUT:
                setLinearLayoutSpaceItemDecoration(outRect, view, parent, state);
                break;
            case GRID_LAYOUT:
                GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();
                //列数
                spanCount = gridLayoutManager.getSpanCount();
                setNGridLayoutSpaceItemDecoration(outRect, view, parent, state);
                break;
            case STAGGERED_GRID_LAYOUT:
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) parent.getLayoutManager();
                //列数
                spanCount = staggeredGridLayoutManager.getSpanCount();
                setStaggeredLayoutSpaceItemDecoration(outRect, view, parent, state);
                break;
            default:
                break;
        }
    }

    /**
     * LinearLayoutManager spacing
     */
    private void setLinearLayoutSpaceItemDecoration(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view) - headItemCount;
        if (parent.getAdapter() == null) {
            return;
        }
        int count = parent.getAdapter().getItemCount();
        if (orientation == ORIENTATION_VERTICAL) {
            outRect.bottom = 0;
            if (includeEdge) {
                outRect.left = space;
                outRect.right = space;
                outRect.top = space;
                if (position == count - 1) {
                    outRect.bottom = space;
                }
            } else {
                outRect.left = 0;
                outRect.right = 0;
                if (parent.getChildLayoutPosition(view) == 0) {
                    outRect.top = 0;
                } else {
                    outRect.top = space;
                }
            }
        } else {
            if (includeEdge) {
                if (position == 0) {            //第一项
                    outRect.left = space;
                    outRect.top = space;
                    outRect.bottom = space;
                    outRect.right = space / 2;
                } else if (position == count - 1) { //最后一项
                    outRect.left = space / 2;
                    outRect.top = space;
                    outRect.bottom = space;
                    outRect.right = space;
                } else {                    //中间项
                    outRect.left = space / 2;
                    outRect.top = space;
                    outRect.bottom = space;
                    outRect.right = space / 2;
                }
            } else {
                if (position == 0) {            //第一项
                    outRect.left = 0;
                    outRect.top = 0;
                    outRect.bottom = 0;
                    outRect.right = space / 2;
                } else if (position == count - 1) { //最后一项
                    outRect.left = space / 2;
                    outRect.top = 0;
                    outRect.bottom = 0;
                    outRect.right = 0;
                } else {                    //中间项
                    outRect.left = space / 2;
                    outRect.top = 0;
                    outRect.bottom = 0;
                    outRect.right = space / 2;
                }
            }
        }

    }

    /**
     * GridLayoutManager or StaggeredGridLayoutManager spacing
     */
    private void setNGridLayoutSpaceItemDecoration(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view) - headItemCount;
        if (headItemCount != 0 && position == -headItemCount) {
            return;
        }
        int column = position % spanCount;
        if (includeEdge) {
            outRect.left = space - column * space / spanCount;
            outRect.right = (column + 1) * space / spanCount;
            if (position < spanCount) {
                outRect.top = space;
            }
            outRect.bottom = space;
        } else {
            outRect.left = column * space / spanCount;
            outRect.right = space - (column + 1) * space / spanCount;
            if (position >= spanCount) {
                outRect.top = space;
            }
        }
    }

    /**
     * 设置瀑布流间距
     */
    private void setStaggeredLayoutSpaceItemDecoration(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        int position = parent.getChildAdapterPosition(view) - headItemCount;
        if (headItemCount != 0 && position == -headItemCount) {
            return;
        }
        int spanIndex = layoutParams.getSpanIndex();
        int halfDividerWidth = space / 2;

        if (shadowRect == null) {
            outRect.top = space;
            if (spanIndex == 0) {
                // left
                outRect.left = space;
                outRect.right = halfDividerWidth;
            } else if (spanIndex == spanCount - 1) {
                outRect.left = halfDividerWidth;
                outRect.right = space;
            } else {
                outRect.left = halfDividerWidth;
                outRect.right = halfDividerWidth;
            }
        } else {    //如果每个item都有阴影
            if (position < spanCount) {
                //第一排Item
                outRect.top = space - shadowRect.top;
            } else {
                outRect.top = space - shadowRect.bottom;
            }

            if (spanIndex == 0) {
                // left
                outRect.left = space - shadowRect.left;
                outRect.right = halfDividerWidth - shadowRect.right;
            } else if (spanIndex == spanCount - 1) {
                outRect.left = halfDividerWidth - shadowRect.left;
                outRect.right = space - shadowRect.right;
            } else {
                outRect.left = halfDividerWidth - shadowRect.left;
                outRect.right = halfDividerWidth - shadowRect.right;
            }
        }
    }

    /**
     * GridLayoutManager设置间距（此方法最左边和最右边间距为设置的一半）
     */
    private void setGridLayoutSpaceItemDecoration(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        //判断总的数量是否可以整除
        int totalCount = layoutManager.getItemCount();
        int surplusCount = totalCount % layoutManager.getSpanCount();
        int childPosition = parent.getChildAdapterPosition(view);
        //竖直方向的
        if (layoutManager.getOrientation() == GridLayoutManager.VERTICAL) {
            if (surplusCount == 0 && childPosition > totalCount - layoutManager.getSpanCount() - 1) {
                //后面几项需要bottom
                outRect.bottom = topBottom;
            } else if (surplusCount != 0 && childPosition > totalCount - surplusCount - 1) {
                outRect.bottom = topBottom;
            }
            //被整除的需要右边
            if ((childPosition + 1 - headItemCount) % layoutManager.getSpanCount() == 0) {
                //加了右边后最后一列的图就非宽度少一个右边距
                //outRect.right = leftRight;
            }
            outRect.top = topBottom;
            outRect.left = leftRight / 2;
            outRect.right = leftRight / 2;
        } else {
            if (surplusCount == 0 && childPosition > totalCount - layoutManager.getSpanCount() - 1) {
                //后面几项需要右边
                outRect.right = leftRight;
            } else if (surplusCount != 0 && childPosition > totalCount - surplusCount - 1) {
                outRect.right = leftRight;
            }
            //被整除的需要下边
            if ((childPosition + 1) % layoutManager.getSpanCount() == 0) {
                outRect.bottom = topBottom;
            }
            outRect.top = topBottom;
            outRect.left = leftRight;
        }
    }
}