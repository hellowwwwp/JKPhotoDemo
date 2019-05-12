package com.dealuck.demo.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseViewHolder;
import com.dealuck.demo.R;
import com.dealuck.demo.adapter.MainListAdapter;
import com.dealuck.demo.model.ImageModel;
import com.dealuck.demo.model.ListItemModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 列表页
 */
public class MainListActivity extends AppCompatActivity implements MainListAdapter.OnItemImageClickListener {

    private RecyclerView mRecyclerView;
    private MainListAdapter mAdapter;

    private int mExitPosition = -1;
    private int mExitIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //初始化转场动画
            initTransition();
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<ListItemModel> list = getData();
        mAdapter = new MainListAdapter(list);
        mAdapter.setOnItemImageClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initTransition() {
        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                if (mExitPosition != -1 && mExitIndex != -1) {
                    RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForAdapterPosition(mExitPosition);
                    if (viewHolder != null) {
                        View itemView = viewHolder.itemView;
                        RecyclerView recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_view);
                        RecyclerView.ViewHolder childViewHolder = recyclerView.findViewHolderForAdapterPosition(mExitIndex);
                        if (childViewHolder != null) {
                            View childItemView = childViewHolder.itemView;
                            View imageView = childItemView.findViewById(R.id.image_view);
                            imageView.setVisibility(View.VISIBLE);
                            String name = (String) imageView.getTag();
                            names.clear();
                            sharedElements.clear();
                            names.add(name);
                            sharedElements.put(name, imageView);
                            //显示备用ImageView
                            View standbyImageView = childItemView.findViewById(R.id.standby_image_view);
                            if (standbyImageView.getVisibility() != View.VISIBLE) {
                                standbyImageView.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    mExitPosition = -1;
                    mExitIndex = -1;
                }
            }

            @Override
            public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
                //隐藏备用ImageView
                ViewGroup childItemView = (ViewGroup) sharedElements.get(0).getParent();
                View standbyImageView = childItemView.findViewById(R.id.standby_image_view);
                standbyImageView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onItemImageClick(BaseViewHolder viewHolder, View imageView, int itemPosition, int imageIndex) {
        //让备用ImageView显示
        ViewGroup childItemView = (ViewGroup) imageView.getParent();
        if (childItemView != null) {
            View standbyImageView = childItemView.findViewById(R.id.standby_image_view);
            standbyImageView.setVisibility(View.VISIBLE);
        }

        ListItemModel model = mAdapter.getData().get(itemPosition);
        Intent intent = new Intent(this, PhotoDetailActivity.class);
        intent.putExtra("model", model);
        intent.putExtra("itemPosition", itemPosition);
        intent.putExtra("imageIndex", imageIndex);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String tranName = (String) imageView.getTag();
            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this, imageView, tranName).toBundle();
            startActivity(intent, bundle);
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            mExitPosition = data.getIntExtra("exitPosition", -1);
            mExitIndex = data.getIntExtra("exitIndex", -1);
        }
    }

    private List<ListItemModel> getData() {
        List<ListItemModel> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ListItemModel model = new ListItemModel();
            model.name = "我是第: " + i + "个item";
            model.images = getImages(i);
            list.add(model);
        }
        return list;
    }

    private List<ImageModel> getImages(int position) {
        List<ImageModel> list = new ArrayList<>();
        list.add(new ImageModel(R.drawable.wz1, position + "_image_1"));
        list.add(new ImageModel(R.drawable.wz2, position + "_image_2"));
        list.add(new ImageModel(R.drawable.wz3, position + "_image_3"));
        list.add(new ImageModel(R.drawable.wz4, position + "_image_4"));
        list.add(new ImageModel(R.drawable.wz5, position + "_image_5"));
        list.add(new ImageModel(R.drawable.wz6, position + "_image_6"));
        list.add(new ImageModel(R.drawable.wz7, position + "_image_7"));
        list.add(new ImageModel(R.drawable.wz8, position + "_image_8"));
        list.add(new ImageModel(R.drawable.wz9, position + "_image_9"));
        list.add(new ImageModel(R.drawable.wz10, position + "_image_10"));
        return list;
    }

}
