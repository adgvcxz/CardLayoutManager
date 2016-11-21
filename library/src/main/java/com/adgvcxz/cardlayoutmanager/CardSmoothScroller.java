package com.adgvcxz.cardlayoutmanager;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

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
    private int mScrollToPosition;
    private CardSwipeModel mCardSwipeModel;
    private OnAnimationListener mOnAnimationListener;
    private CardSwipeController mCardSwipeController;

    public CardSmoothScroller(CardSwipeController cardSwipeController) {
        mCardSwipeController = cardSwipeController;
    }


    @Override
    protected void onStart() {
    }

    @Override
    protected void onStop() {
    }

    @Override
    protected void onSeekTargetStep(int dx, int dy, RecyclerView.State state, Action action) {
        if (mScrollToPosition == SCROLL_PRE) {
            action.update(-mCardSwipeModel.getDx(), -mCardSwipeModel.getDy(), 100, new AccelerateInterpolator());
        }
    }

    @Override
    protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {

        if (mScrollToPosition == SCROLL_NEXT) {
            CardSwipeModel model = mCardSwipeController.generateAnimOutModel(targetView, getTargetPosition());
            model.updateDxAndDy(0, 0);
            updateAction(action, model, true);
            return;
        }

        if (mScrollToPosition == SCROLL_PRE) {
            mCardSwipeModel.setDxAndDy((int) -targetView.getTranslationX(), (int) -targetView.getTranslationY());
            updateAction(action, mCardSwipeModel, false);
            return;
        }

        int x = (int) targetView.getTranslationX();
        int y = (int) targetView.getTranslationY();
        if (x != 0 || y != 0) {
            if (mCardSwipeController.canSwipeOut(targetView, getTargetPosition(), x, y, mVelocityX, mVelocityY)) {
                CardSwipeModel model = mCardSwipeController.generateSwipeOutModel(targetView, getTargetPosition(), x, y, mVelocityX, mVelocityY);
                model.updateDxAndDy(x, y);
                updateAction(action, model, true);
            } else {
                CardSwipeModel model = mCardSwipeController.generateSwipeInModel(targetView, getTargetPosition(), x, y, mVelocityX, mVelocityY);
                model.updateDxAndDy(x, y);
                updateAction(action, model, false);
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

    void prepare(int target, int velocityX, int velocityY, OnAnimationListener onAnimationListener) {
        setTargetPosition(target);
        mVelocityX = velocityX;
        mVelocityY = velocityY;
        mScrollToPosition = SCROLL_NO_POSITION;
        mOnAnimationListener = onAnimationListener;
    }

    void prepareNext(int target, OnAnimationListener onAnimationListener) {
        setTargetPosition(target);
        mScrollToPosition = SCROLL_NEXT;
        mOnAnimationListener = onAnimationListener;
    }

    void preparePre(int target, CardSwipeModel cardSwipeModel, OnAnimationListener onAnimationListener) {
        setTargetPosition(target);
        mCardSwipeModel = cardSwipeModel;
        mScrollToPosition = SCROLL_PRE;
        mOnAnimationListener = onAnimationListener;
    }


    void updateAction(Action action, CardSwipeModel model, boolean out) {
        action.update(-model.getDx(), -model.getDy(), model.getDuration(), model.getInterpolator());
        if (mOnAnimationListener != null) {
            if (out) {
                mOnAnimationListener.onStartOut(model.getDirection());
            } else {
                mOnAnimationListener.onStartIn();
            }
        }
    }
}
