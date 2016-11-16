package com.adgvcxz.cardlayoutmanager;

import android.graphics.PointF;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Random;

/**
 * zhaowei
 * Created by zhaowei on 2016/11/10.
 */

public class CardLayoutManager extends RecyclerView.LayoutManager implements RecyclerView.SmoothScroller.ScrollVectorProvider {

    private static final int NO_TARGET_POSITION = -1;
    private static final int COUNT = 4;
    private static final int MAX_DEGREE = 27;
    private static final float SCALE_INTERVAL = 0.05f;

    private int mStartPosition = 0;
    private int mTargetPosition = NO_TARGET_POSITION;
    private int mHorizontally = 0;
    private int mVertically = 0;
    private float mTouchProportion;
    private SparseArray<View> mCacheViews = new SparseArray<>();
    private boolean mIsScrollEnabled = true;
    private RecyclerView.Recycler mRecycler;
    private int mOrientation = LinearLayout.HORIZONTAL;
    private int mMinDistance;
    private CardSwipeController mCardSwipeController;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getHeight());
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        mRecycler = recycler;
        if (getItemCount() == 0) {
            detachAndScrapAttachedViews(recycler);
            return;
        }

        if (getChildCount() == 0 && state.isPreLayout()) {
            detachAndScrapAttachedViews(recycler);
            return;
        }
//        if (!mIsAmination) {
        fill(recycler);
//        }
    }

    private void fill(RecyclerView.Recycler recycler) {

        fillCache();
        for (int i = 0; i < mCacheViews.size(); i++) {
            detachView(mCacheViews.valueAt(i));
        }

        if ((mTargetPosition == NO_TARGET_POSITION || mTargetPosition > mStartPosition) && Math.abs(mHorizontally) > mMinDistance) {
            mHorizontally = 0;
            mVertically = 0;
            mStartPosition++;
            mIsScrollEnabled = false;
        }

        float proportion = 1;
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();


        for (int i = mStartPosition; i < mStartPosition + COUNT && i < getItemCount(); i++) {
            View child = mCacheViews.get(i);
            if (child == null) {
                child = recycler.getViewForPosition(i);
                addView(child, 0);
                measureChildWithMargins(child, 0, 0);
                int width = getDecoratedMeasurementHorizontal(child);
                int height = getDecoratedMeasurementVertical(child);
                int left = (getWidth() - width + paddingLeft - paddingRight) / 2;
                int right = left + width;
                int top = (getHeight() - height + paddingTop - paddingBottom) / 2;
                int bottom = top + height - 112;
                layoutDecoratedWithMargins(child, left, top, right, bottom);
            } else {
                attachView(child, 0);
                mCacheViews.remove(i);
            }

            if (i == mStartPosition) {
                ViewCompat.setTranslationX(child, -mHorizontally);
                ViewCompat.setTranslationY(child, -mVertically);
                ViewCompat.setRotation(child, getRotation());
                ViewCompat.setScaleX(child, 1);
                ViewCompat.setScaleY(child, 1);
                mMinDistance = getMinDistance();
                proportion = getProportion(child);
            } else {
                int number = i - mStartPosition;
                if (i == mStartPosition + COUNT - 1) {
                    proportion = 0;
                    number -= 1;
                }
                child.setScaleX(1 - number * SCALE_INTERVAL + proportion * SCALE_INTERVAL);
                child.setScaleY(1 - number * SCALE_INTERVAL + proportion * SCALE_INTERVAL);
                child.setTranslationY((number - proportion) * 56);
            }
        }
        for (int i = 0; i < mCacheViews.size(); i++) {
            removeAndRecycleView(mCacheViews.valueAt(i), recycler);
        }
        mCacheViews.clear();
    }

    private void fillCache() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            int position = getPosition(view);
            mCacheViews.put(position, view);
        }
    }

    private float getRotation() {
        if (mOrientation == LinearLayout.HORIZONTAL) {
            return MAX_DEGREE * mHorizontally / getWidth() * mTouchProportion;
        } else {
            return -MAX_DEGREE * mVertically / getHeight() * mTouchProportion;
        }
    }

    private int getMinDistance() {
        if (mOrientation == LinearLayout.HORIZONTAL) {
            return getWidth() - getPaddingLeft() - getPaddingRight();
        } else {
            return getHeight() - getPaddingTop() - getPaddingBottom();
        }
    }

    private float getProportion(View view) {
        float proportion;
        if (mOrientation == LinearLayout.HORIZONTAL) {
            proportion = Math.abs(mHorizontally) / (view.getWidth() / 2.0f);
        } else {
            proportion = Math.abs(mVertically) / (view.getHeight() / 2.0f);
        }
        return Math.min(proportion, 1);
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            if (mTargetPosition != NO_TARGET_POSITION) {
                if (mTargetPosition > mStartPosition) {
                    smoothScrollNext(mTargetPosition);
                } else if (mTargetPosition < mStartPosition) {
                    smoothScrollPre(mTargetPosition);
                } else {
                    mTargetPosition = NO_TARGET_POSITION;
                }
            }
            mIsScrollEnabled = true;
        }
    }


    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (mIsScrollEnabled) {
            mVertically += dy;
            fill(recycler);
            return dy;
        }
        return 0;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (mIsScrollEnabled) {
            mHorizontally += dx;
            fill(recycler);
            return dx;
        }
        return 0;
    }

    private int getDecoratedMeasurementHorizontal(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        return getDecoratedMeasuredWidth(view) + params.leftMargin
                + params.rightMargin;
    }

    private int getDecoratedMeasurementVertical(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        return getDecoratedMeasuredHeight(view) + params.topMargin
                + params.bottomMargin;
    }

    public int getStartPosition() {
        return mStartPosition;
    }

    void fling(int velocityX, int velocityY) {
        CardSmoothScroller scroller = new CardSmoothScroller();
        scroller.setTargetPosition(mStartPosition);
        scroller.setVelocity(velocityX, velocityY);
        startSmoothScroll(scroller);
    }


    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        if (position != mStartPosition && position < getItemCount()) {
            if (position > mStartPosition) {
                smoothScrollNext(position);
            } else {
                smoothScrollPre(position);
            }
        } else {
            mTargetPosition = NO_TARGET_POSITION;
        }
    }

    private void smoothScrollNext(int position) {
        View view = findViewByPosition(mStartPosition);
        if (view != null) {
            int top = view.getTop();
            int left = view.getLeft();
            Random random = new Random();
            setDownPoint(view, random.nextInt(view.getWidth()) + left, random.nextInt(view.getHeight()) + top);
            mTargetPosition = position;
            CardSmoothScroller scroller = new CardSmoothScroller();
            scroller.setTargetPosition(mStartPosition);
            scroller.setOrientation(mOrientation);
            scroller.scrollNext();
            startSmoothScroll(scroller);
        }
    }

    private void smoothScrollPre(int position) {
        View view = findViewByPosition(mStartPosition);
        if (view != null) {
            int top = view.getTop();
            int left = view.getLeft();
            Random random = new Random();
            setDownPoint(view, random.nextInt(view.getWidth()) + left, random.nextInt(view.getHeight()) + top);
            mTargetPosition = position;
            CardSmoothScroller scroller = new CardSmoothScroller();
            CardSwipeModel model = scroller.randomEndPoint(view);
            mHorizontally = model.getDx();
            mVertically = model.getDy();
            mStartPosition -= 1;
            scroller.setTargetPosition(mStartPosition);
            scroller.setOrientation(mOrientation);
            scroller.scrollPre();
            startSmoothScroll(scroller);
        }
    }

    @Override
    public void onItemsChanged(RecyclerView recyclerView) {
        super.onItemsChanged(recyclerView);
        removeAllViews();
        fill(mRecycler);
    }

    void setDownPoint(View view, int x, int y) {
        float half = getHeight() / 2;
        Rect rect = new Rect();
        view.getLocalVisibleRect(rect);
        mIsScrollEnabled = rect.contains(x, y);
        if (mIsScrollEnabled) {
            mTouchProportion = (y - half - view.getTop()) / half;
        }
    }

    public int getTopPosition() {
        return mStartPosition;
    }

    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        return null;
    }
}
