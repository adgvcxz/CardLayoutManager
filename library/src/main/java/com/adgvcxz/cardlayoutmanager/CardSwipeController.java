package com.adgvcxz.cardlayoutmanager;

import android.view.View;

/**
 * zhaowei
 * Created by zhaowei on 2016/11/16.
 */

public abstract class CardSwipeController {

    protected int mMaxWidth;
    protected int mMaxHeight;
    private float mDegree;
    protected int mMinVelocity;

    /**
     * smoothScrollToPosition时获取滑出的最终CardSwipeModel
     */
    abstract CardSwipeModel generateAnimOutModel(View view, int position);

    /**
     * smoothScrollToPosition时获取滑入的起点CardSwipeModel
     */
    abstract CardSwipeModel generateAnimInModel(int position);

    /**
     * 手动滑出的终点CardSwipeModel
     */
    abstract CardSwipeModel generateSwipeOutModel(View view, int position, int dx, int dy, int velocityX, int velocityY);

    /**
     * 手动滑入的终点CardSwipeModel  最好按照BaseCardSwipeController的getSwipeInModel
     */
    abstract CardSwipeModel generateSwipeInModel(View view, int position, int dx, int dy, int velocityX, int velocityY);

    /**
     * smoothScrollToPosition时模拟真实滑动  获取一个按下的点  如果为{-1, -1}则是随机
     */
    abstract int[] getDownPosition();

    /**
     * 松手后是否可以滑出
     */
    abstract boolean canSwipeOut(View view, int position, int dx, int dy, int velocityX, int velocityY);



    void setWidthAndHeight(int width, int height) {
        mMaxWidth = width;
        mMaxHeight = height;
    }

    protected float getDegree() {
        return mDegree;
    }

    void setDegree(float degree) {
        mDegree = degree;
    }

    public void setMinVelocity(int velocity) {
        mMinVelocity = velocity;
    }

    public int getMinVelocity() {
        return mMinVelocity;
    }
}
