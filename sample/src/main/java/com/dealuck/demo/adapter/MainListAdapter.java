package com.dealuck.demo.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dealuck.demo.R;
import com.dealuck.demo.model.ImageModel;
import com.dealuck.demo.model.ListItemModel;
import com.dealuck.demo.utils.DensityUtil;
import com.dealuck.demo.widget.SpaceItemDecoration;

import java.util.List;


public class MainListAdapter extends BaseQuickAdapter<ListItemModel, BaseViewHolder> {

    private OnItemImageClickListener mOnItemImageClickListener;

    public MainListAdapter(@Nullable List<ListItemModel> data) {
        super(R.layout.view_list, data);
    }

    public void setOnItemImageClickListener(OnItemImageClickListener listener) {
        this.mOnItemImageClickListener = listener;
    }

    @Override
    protected void convert(BaseViewHolder holder, ListItemModel item) {
        Context context = holder.itemView.getContext();
        holder.setText(R.id.tv_name, item.name);
        RecyclerView recyclerView = (RecyclerView) holder.getView(R.id.recycler_view);
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (recyclerView.getLayoutManager() == null) {
            recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        }
        if (recyclerView.getItemDecorationCount() == 0) {
            recyclerView.addItemDecoration(new SpaceItemDecoration(DensityUtil.dp2px(context, 5), false, SpaceItemDecoration.GRID_LAYOUT));
        }
        if (adapter != null && adapter instanceof ChildAdapter) {
            ChildAdapter childAdapter = (ChildAdapter) adapter;
            childAdapter.setNewData(item.images);
        } else {
            ChildAdapter childAdapter = new ChildAdapter(item.images);
            recyclerView.setAdapter(childAdapter);
            childAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    ImageView imageView = (ImageView) view.findViewById(R.id.image_view);
                    if (mOnItemImageClickListener != null) {
                        mOnItemImageClickListener.onItemImageClick(holder, imageView, holder.getAdapterPosition(), position);
                    }
                }
            });
        }
    }

    public static class ChildAdapter extends BaseQuickAdapter<ImageModel, BaseViewHolder> {

        public ChildAdapter(@Nullable List<ImageModel> data) {
            super(R.layout.view_list_child_item, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, ImageModel item) {
            ImageView imageView = (ImageView) holder.getView(R.id.image_view);
            imageView.setImageResource(item.resId);
            imageView.setTag(item.name);
            //备用
            ImageView standbyImageView = (ImageView) holder.getView(R.id.standby_image_view);
            standbyImageView.setImageResource(item.resId);
            standbyImageView.setVisibility(View.GONE);
        }
    }

    public interface OnItemImageClickListener {

        void onItemImageClick(BaseViewHolder viewHolder, View imageView, int itemPosition, int imageIndex);
    }
}
