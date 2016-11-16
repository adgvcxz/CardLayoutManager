package com.adgvcxz.cardlayoutmanager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;

import java.util.Random;

/**
 * zhaowei
 * Created by zhaowei on 2016/11/10.
 */

public class CardSmoothScroller extends RecyclerView.SmoothScroller {

    private static final int SCROLL_NO_POSITION = -1;
    private static final int SCROLL_NEXT = 0;
    private static final int SCROLL_PRE = 1;

    private int mVelocityX;
    private int mVelocityY;
    private int mOrientation;
    private int mScrollToPosition;
    private CardSwipeModel mCardSwipeModel;
    private int mMinVelocity;
    private OnAnimationListener mOnAnimationListener;


    @Override
    protected void onStart() {
    }

    @Override
    protected void onStop() {
    }

    @Override
    protected void onSeekTargetStep(int dx, int dy, RecyclerView.State state, Action action) {
        //不需要seek
        if (mScrollToPosition == SCROLL_PRE) {
            action.update(-mCardSwipeModel.getDx(), -mCardSwipeModel.getDy(), mCardSwipeModel.getDuration(), new AccelerateInterpolator());
        }
    }

    @Override
    protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {

        if (mScrollToPosition == SCROLL_NEXT) {
            View parent = (View) targetView.getParent();
            CardSwipeModel model = randomEndPoint(targetView, parent.getWidth(), parent.getHeight());
            updateAction(action, model.getDx(), model.getDy(), model.getDuration(), true, new AnticipateOvershootInterpolator());
            return;
        }

        if (mScrollToPosition == SCROLL_PRE) {
            updateAction(action, (int) targetView.getTranslationX(), (int) targetView.getTranslationY(), mCardSwipeModel.getDuration(), false, new OvershootInterpolator());
            return;
        }

        int x = (int) targetView.getTranslationX();
        int y = (int) targetView.getTranslationY();
        if (x != 0 || y != 0) {
            if (canSwipeOut(targetView)) {
                CardSwipeModel model = calculateEndPoint(targetView);
                updateAction(action, model.getDx(), model.getDy(), model.getDuration(), true, new DecelerateInterpolator());
            } else {
                updateAction(action, x, y, (int) Math.max(calculateDistanceToZero(x, y) * 0.7, 350), false, new OvershootInterpolator());
                if (mOnAnimationListener != null) {
                    targetView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mOnAnimationListener.onStopIn();
                        }
                    }, 350);
                }
            }
        }
    }

    private int calculateDistanceToZero(int x, int y) {
        return (int) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    void setOrientation(int orientation) {
        mOrientation = orientation;
    }

    void scrollNext() {
        mScrollToPosition = SCROLL_NEXT;
    }

    void scrollPre() {
        mScrollToPosition = SCROLL_PRE;
    }

    CardSwipeModel randomEndPoint(View view, int pWidth, int pHeight) {
        Random random = new Random();
        int direction = random.nextInt(CardSwipeModel.DIRECTION_END) + CardSwipeModel.DIRECTION_START;
        int distanceX;
        int distanceY;
        int left = 0;
        int top = 0;
        int translationX = 0;
        int translationY = 0;
        if (view != null) {
            left = view.getLeft();
            top = view.getTop();
            translationX = (int) view.getTranslationX();
            translationY = (int) view.getTranslationY();
        }
        if (mOrientation == LinearLayout.HORIZONTAL) {
            int cnt = direction == CardSwipeModel.DIRECTION_START ? -translationX : translationX;
            int startMinDistance = pWidth - left - cnt;
            distanceX = (int) (startMinDistance + pWidth * Math.random());
            distanceY = random.nextInt(pHeight / 2) * (random.nextBoolean() ? 1 : -1);
        } else {
            int cnt = direction == CardSwipeModel.DIRECTION_START ? -translationY : translationY;
            int startMinDistance = pHeight - top - cnt;
            distanceY = (int) (startMinDistance + pHeight * Math.random());
            distanceX = random.nextInt(pWidth / 2) * (random.nextBoolean() ? 1 : -1);
        }
        int minVelocity = 150;
        mCardSwipeModel = new CardSwipeModel(direction, distanceX, distanceY, minVelocity * 8, 3/*view.getResources().getDisplayMetrics().density*/);
        return mCardSwipeModel;
    }

    CardSwipeModel calculateEndPoint(View view) {
        ViewGroup layout = (ViewGroup) view.getParent();
        int direction;
        if (mOrientation == LinearLayout.HORIZONTAL) {
            direction = view.getTranslationX() > 0 ? CardSwipeModel.DIRECTION_START : CardSwipeModel.DIRECTION_END;
        } else {
            direction = view.getTranslationY() > 0 ? CardSwipeModel.DIRECTION_START : CardSwipeModel.DIRECTION_END;
        }
        float slope = view.getTranslationY() / view.getTranslationX();
        int distanceX;
        int distanceY;
        if (mOrientation == LinearLayout.HORIZONTAL) {
            int cnt = (int) (direction == CardSwipeModel.DIRECTION_START ? -view.getTranslationX() : view.getTranslationX());
            int startMinDistance = layout.getWidth() - view.getLeft() - cnt;
            distanceX = (int) (startMinDistance + layout.getWidth() * Math.random());
            distanceY = (int) (distanceX * slope);
        } else {
            int cnt = (int) (direction == CardSwipeModel.DIRECTION_START ? -view.getTranslationY() : view.getTranslationY());
            int startMinDistance = layout.getHeight() - view.getTop() - cnt;
            distanceY = (int) (startMinDistance + layout.getHeight() * Math.random());
            distanceX = (int) (distanceY / slope);
        }
        mCardSwipeModel = new CardSwipeModel(direction, distanceX, distanceY, mVelocityX, view.getResources().getDisplayMetrics().density);
        return mCardSwipeModel;
    }

    private boolean exceed(Context context) {
        if (mMinVelocity == 0) {
            mMinVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity() * 6;
        }
        int velocityX = Math.abs(mVelocityX);
        int velocityY = Math.abs(mVelocityY);
        if (mOrientation == LinearLayout.HORIZONTAL) {
            return velocityX > mMinVelocity * 3 && velocityX >= velocityY;
        } else {
            return velocityY > mMinVelocity * 3 && velocityY >= velocityX;
        }
    }

    private boolean canSwipeOut(View targetView) {
        int x = (int) targetView.getTranslationX();
        int y = (int) targetView.getTranslationY();
        int width = targetView.getWidth();
        int height = targetView.getHeight();
        boolean isOverBounds = mOrientation == LinearLayout.HORIZONTAL ? Math.abs(x) >= width / 2 : Math.abs(y) >= height / 2;
        return isOverBounds || exceed(targetView.getContext());
    }

    void prepare(int target, int orientation, int velocityX, int velocityY, OnAnimationListener onAnimationListener) {
        setTargetPosition(target);
        mOrientation = orientation;
        mVelocityX = velocityX;
        mVelocityY = velocityY;
        mScrollToPosition = SCROLL_NO_POSITION;
        mOnAnimationListener = onAnimationListener;
    }

    void updateAction(Action action, int dx, int dy, int duration, boolean out, Interpolator interpolator) {
        action.update(dx, dy, duration, interpolator);
        if (mOnAnimationListener != null) {
            if (out) {
                mOnAnimationListener.onStartOut();
            } else {
                mOnAnimationListener.onStartIn();
            }
        }
    }
}
