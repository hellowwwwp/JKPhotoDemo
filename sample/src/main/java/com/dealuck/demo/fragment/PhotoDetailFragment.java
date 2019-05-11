package com.dealuck.demo.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dealuck.demo.R;
import com.dealuck.demo.activity.PhotoDetailActivity;
import com.dealuck.demo.model.ImageModel;
import com.dealuck.demo.widget.DragPhotoView;
import com.dealuck.demo.widget.FixedPhotoView;


public class PhotoDetailFragment extends Fragment {

    private FixedPhotoView mPhotoView;

    public static PhotoDetailFragment getInstance(ImageModel model) {
        PhotoDetailFragment fragment = new PhotoDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("imageModel", model);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_photo_detail_fragment, container, false);
        DragPhotoView dragPhotoView = (DragPhotoView) view.findViewById(R.id.dpv);
        dragPhotoView.setFitViewPager(true);
        dragPhotoView.setDisableDragScale(true);
        dragPhotoView.setOnlyDownClose(false);
        dragPhotoView.setOnlyVerticalDrag(true);
        mPhotoView = dragPhotoView.getPhotoView();

        Bundle bundle = getArguments();
        ImageModel imageModel = (ImageModel) bundle.getSerializable("imageModel");

        mPhotoView.setImageResource(imageModel.resId);
        mPhotoView.setTag(imageModel.name);
        mPhotoView.setOnPhotoViewClickListener(new FixedPhotoView.OnPhotoViewClickListener() {
            @Override
            public void onPhotoViewClick(FixedPhotoView photoView) {
                PhotoDetailActivity activity = getT8DetailActivity();
                if (activity != null) {
                    activity.finishActivity();
                }
            }
        });

        dragPhotoView.setOnDragPhotoListener(new DragPhotoView.OnDragPhotoListener() {
            @Override
            public void onDragging(float percent) {
                PhotoDetailActivity activity = getT8DetailActivity();
                if (activity != null) {
                    activity.onImageDragging(percent);
                }
            }

            @Override
            public void onDragClose() {
                PhotoDetailActivity activity = getT8DetailActivity();
                if (activity != null) {
                    activity.finishActivity();
                }
            }
        });

        //fragment加载完成后开始转场动画
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            PhotoDetailActivity activity = getT8DetailActivity();
            if (activity != null) {
                activity.startPostponedEnterTransition();
            }
        }
        return view;
    }

    private PhotoDetailActivity getT8DetailActivity() {
        FragmentActivity activity = getActivity();
        if (activity != null && activity instanceof PhotoDetailActivity) {
            return (PhotoDetailActivity) activity;
        }
        return null;
    }

    public FixedPhotoView getShareElement() {
        return mPhotoView;
    }
}
