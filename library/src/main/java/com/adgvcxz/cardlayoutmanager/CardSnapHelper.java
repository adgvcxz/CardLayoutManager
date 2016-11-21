package com.adgvcxz.cardlayoutmanager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.MotionEvent;
import android.view.View;

/**
 * zhaowei
 * Created by zhaowei on 2016/11/10.
 */

public class CardSnapHelper extends SnapHelper implements View.OnTouchListener {

    private int mVelocityX;
    private int mVelocityY;
    private View.OnTouchListener mTouchListener;

    @Override
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) throws IllegalStateException {
        super.attachToRecyclerView(recyclerView);
        if (recyclerView != null) {
            recyclerView.setOnTouchListener(this);
        }
    }

    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        int velocityX = mVelocityX;
        int velocityY = mVelocityY;
        mVelocityX = 0;
        mVelocityY = 0;
        if (layoutManager instanceof CardLayoutManager) {
            ((CardLayoutManager) layoutManager).fling(velocityX, velocityY);
        }
        return new int[2];
    }

    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        if (layoutManager instanceof CardLayoutManager) {
            mVelocityX = velocityX;
            mVelocityY = velocityY;
            return ((CardLayoutManager) layoutManager).getTopPosition();
        }
        return RecyclerView.NO_POSITION;
    }

    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof CardLayoutManager) {
            CardLayoutManager manager = (CardLayoutManager) layoutManager;
            View view = manager.findViewByPosition(manager.getTopPosition());
            if (view != null) {
                int x = (int) view.getTranslationX();
                int y = (int) view.getTranslationY();
                int width = layoutManager.getWidth();
                int height = layoutManager.getHeight();
                if (x > width || y > height || (x == 0 && y == 0)) {
                    return null;
                }
                return view;
            }
        }
        return null;

    }

    public void setOnTouchListener(View.OnTouchListener listener) {
        mTouchListener = listener;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (((RecyclerView) view).getLayoutManager() instanceof CardLayoutManager) {
                CardLayoutManager manager = (CardLayoutManager) ((RecyclerView) view).getLayoutManager();
                manager.setDownPoint(view, (int) motionEvent.getX(), (int) motionEvent.getY());
            }
        }
        return mTouchListener != null && mTouchListener.onTouch(view, motionEvent);
    }
}
