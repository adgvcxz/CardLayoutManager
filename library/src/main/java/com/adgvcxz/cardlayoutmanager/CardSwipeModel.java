package com.adgvcxz.cardlayoutmanager;

/**
 * zhaowei
 * Created by zhaowei on 2016/11/15.
 */

public class CardSwipeModel {

    static final int DIRECTION_START = 1;
    static final int DIRECTION_END = 2;

    private int mDirection;
    private int mDistanceX;
    private int mDistanceY;
    private float mVelocity;
    private float mDensity;

    CardSwipeModel(int direction, int distanceX, int distanceY, float velocity, float density) {
        mDirection = direction;
        mDistanceX = distanceX;
        mDistanceY = distanceY;
        mVelocity = velocity;
        mDensity = density;
    }

    int getDx() {
        return mDirection == DIRECTION_START ? -mDistanceX : mDistanceX;
    }

    int getDy() {
        return mDirection == DIRECTION_START ? -mDistanceY : mDistanceY;
    }

    int getDuration() {
        int distance = (int) Math.sqrt(Math.pow(mDistanceX, 2) + Math.pow(mDistanceY, 2));
        int min = (int) (distance / mDensity);
        return Math.max(350, min);
    }
}
