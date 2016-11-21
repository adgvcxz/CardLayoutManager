package com.adgvcxz.cardlayoutmanager;

import android.view.animation.Interpolator;

/**
 * zhaowei
 * Created by zhaowei on 2016/11/15.
 */

public class CardSwipeModel {

    /**
     * 滑出的方向  DIRECTION_START 或者 DIRECTION_END
     */
    private int mDirection;

    /**
     * 滑入时的起点x
     * 滑出时的终点x
     */
    private int mX;

    /**
     * 滑入时的起点y
     * 滑出时的终点y
     */
    private int mY;

    /**
     * 滑动的时间
     */
    private int mDuration;

    /**
     * 动画的Interpolator
     */
    private Interpolator mInterpolator;
    private int mDx;
    private int mDy;


    public CardSwipeModel(int x, int y, int duration, Interpolator interpolator) {
        mX = x;
        mY = y;
        mDuration = duration;
        mInterpolator = interpolator;
    }

    public CardSwipeModel(int x, int y, int duration, Interpolator interpolator, int direction) {
        mX = x;
        mY = y;
        mDuration = duration;
        mInterpolator = interpolator;
        mDirection = direction;
    }

    int getDuration() {
        return mDuration;
    }

    public int getDirection() {
        return mDirection;
    }

    void updateDxAndDy(int dx, int dy) {
        mDx = mX - dx;
        mDy = mY - dy;
    }

    void setDxAndDy(int dx, int dy) {
        mDx = dx;
        mDy = dy;
    }

    int getDx() {
        return mDx;
    }

    int getDy() {
        return mDy;
    }

    Interpolator getInterpolator() {
        return mInterpolator;
    }
}
