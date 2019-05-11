package com.dealuck.demo.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.dealuck.demo.R;
import com.dealuck.demo.adapter.PhotoDetailAdapter;
import com.dealuck.demo.fragment.PhotoDetailFragment;
import com.dealuck.demo.model.ImageModel;
import com.dealuck.demo.model.ListItemModel;
import com.dealuck.demo.widget.FixedPhotoView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 大图详情页
 */
public class PhotoDetailActivity extends AppCompatActivity {

    private RelativeLayout mRootView;
    private ViewPager mViewPager;
    private PhotoDetailAdapter mAdapter;

    private ListItemModel mModel;
    private int mItemPosition;
    private int mImageIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //初始化转场动画
            initTransition();
        }

        mRootView = (RelativeLayout) findViewById(R.id.root_view);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mModel = (ListItemModel) getIntent().getSerializableExtra("model");
        mItemPosition = getIntent().getIntExtra("itemPosition", 0);
        mImageIndex = getIntent().getIntExtra("imageIndex", 0);
        List<ImageModel> images = mModel.images;
        List<Fragment> fragments = initFragments(images);
        mAdapter = new PhotoDetailAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mImageIndex);
        mViewPager.setOffscreenPageLimit(images.size() - 1);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mImageIndex = position;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initTransition() {
        postponeEnterTransition();
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                int currentItem = mViewPager.getCurrentItem();
                PhotoDetailFragment fragment = (PhotoDetailFragment) mAdapter.getItem(currentItem);
                FixedPhotoView photoView = fragment.getShareElement();
                String name = (String) photoView.getTag();
                names.clear();
                sharedElements.clear();
                names.add(name);
                sharedElements.put(name, photoView);
            }
        });
    }

    private List<Fragment> initFragments(List<ImageModel> imageModels) {
        List<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i < imageModels.size(); i++) {
            ImageModel imageModel = imageModels.get(i);
            PhotoDetailFragment fragment = PhotoDetailFragment.getInstance(imageModel);
            fragments.add(fragment);
        }
        return fragments;
    }

    public void onImageDragging(float percent) {
        int alpha = (int) (255 * (1f - percent));
        mRootView.getBackground().mutate().setAlpha(alpha);
    }

    public void finishActivity() {
        Intent data = new Intent();
        data.putExtra("exitPosition", mItemPosition);
        data.putExtra("exitIndex", mImageIndex);
        setResult(RESULT_OK, data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
    }
}
