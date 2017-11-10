package com.adgvcxz.cardlayoutmanager;

import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import java.util.Random;

/**
 * zhaowei
 * Created by zhaowei on 2016/11/10.
 */

public class CardLayoutManager extends RecyclerView.LayoutManager implements
        RecyclerView.SmoothScroller.ScrollVectorProvider, OnAnimationListener {

    public enum TransX {
        NONE, LEFT, RIGHT
    }

    public enum TransY {
        NONE, TOP, BOTTOM
    }

    public static final int DIRECTION_START = 1;
    public static final int DIRECTION_END = 2;

    private static final int NO_TARGET_POSITION = -1;
    private static final int CARD_COUNT = 4;
    private static final int MAX_DEGREE = 27;
    private static final float SCALE_INTERVAL = 0.05f;
    private static final int NO_ANIMATION = 0;
    private static final int ANIMATION_OUT = 1;
    private static final int ANIMATION_IN = 2;

    private int mTopPosition = 0;
    private int mTargetPosition = NO_TARGET_POSITION;
    private int mDx = 0;
    private int mDy = 0;
    private boolean mIsSwipe;
    private float mTouchProportion;
    private SparseArray<View> mViewCaches = new SparseArray<>();
    private boolean mIsScrollEnabled = true;
    private RecyclerView.Recycler mRecycler;
    private int mOrientation;
    private int mMinDistance;
    private CardSwipeController mCardSwipeController;
    private OnCardSwipeListener mOnCardSwipeListener;
    private int mAnimStatus = NO_ANIMATION;
    private int mAnimDirection = DIRECTION_START;
    private boolean mAnimPre;
    private int mCount = CARD_COUNT;
    private int mYInterval = 112;
    private int mXInterval = 112;
    private TransX mTranxX;
    private TransY mTranxY;

    private boolean verticalScroll = true;
    private boolean horizontalScroll = true;

    public CardLayoutManager() {
        this(LinearLayout.HORIZONTAL);
    }

    public CardLayoutManager(int orientation) {
        this(orientation, TransX.NONE, TransY.BOTTOM, null);
    }

    public CardLayoutManager(TransX transX, TransY transY) {
        this(LinearLayout.HORIZONTAL, transX, transY, null);
    }

    public CardLayoutManager(int orientation, TransX transX, TransY transY,
                             CardSwipeController cardSwipeController) {
        mOrientation = orientation;
        mTranxX = transX;
        mTranxY = transY;
        if (cardSwipeController != null) {
            mCardSwipeController = cardSwipeController;
        } else {
            mCardSwipeController = new RandomCardSwipeController(mOrientation);
        }
    }

    public void setInterval(int xInterval, int yInterval) {
        setXInterval(xInterval);
        setYInterval(yInterval);
    }

    public void setYInterval(int interval) {
        mYInterval = interval;
    }
    public void setXInterval(int interval) {
        mXInterval = interval;
    }

    public void setShowCardCount(int count) {
        mCount = count;
        if (getChildCount() > 0) {
            requestLayout();
        }
    }

    public void setOnCardSwipeListener(OnCardSwipeListener onCardSwipeListener) {
        mOnCardSwipeListener = onCardSwipeListener;
    }

    public int getTopPosition() {
        return mTopPosition;
    }

    public void setSwipeMinVelocity(int velocity) {
        mCardSwipeController.setMinVelocity(velocity);
    }


    @Override
    public void onAttachedToWindow(final RecyclerView view) {
        super.onAttachedToWindow(view);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (getWidth() > 0 && getHeight() > 0) {
                    mCardSwipeController.setWidthAndHeight(getWidth(), getHeight());
                    if (mCardSwipeController.getMinVelocity() == 0) {
                        mCardSwipeController.setMinVelocity(ViewConfiguration.get(view.getContext()).getScaledMinimumFlingVelocity() * 3);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            }
        });
    }

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
        fill(recycler);
    }

    private void fill(RecyclerView.Recycler recycler) {
        fillCache();
        for (int i = 0; i < mViewCaches.size(); i++) {
            detachView(mViewCaches.valueAt(i));
        }

        if (mTargetPosition == NO_TARGET_POSITION || mTargetPosition > mTopPosition) {
            findTopView();
        }

        fillChild(recycler);

        for (int i = 0; i < mViewCaches.size(); i++) {
            removeAndRecycleView(mViewCaches.valueAt(i), recycler);
        }
        mViewCaches.clear();
    }

    private void fillChild(RecyclerView.Recycler recycler) {
        float proportion = 1;
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        float perHeight = mYInterval / (mCount - 1);
        float perWidth = mXInterval / (mCount - 1);
        perHeight += perHeight / (mCount - 1);
        perWidth += perWidth / (mCount - 1);
        for (int i = mTopPosition; i < mTopPosition + mCount && i < getItemCount(); i++) {
            View child = mViewCaches.get(i);
            if (child == null) {
                child = recycler.getViewForPosition(i);
                child.setTranslationX(0);
                child.setTranslationY(0);
                child.setRotation(0);
                addView(child, 0);
                measureChildWithMargins(child, 0, 0);
                int width = getDecoratedMeasurementHorizontal(child);
                int height = getDecoratedMeasurementVertical(child);

                int left = (getWidth() - width + paddingLeft - paddingRight) / 2;
                int right = left + width;
                int top = (getHeight() - height + paddingTop - paddingBottom) / 2;
                int bottom = top + height;

                switch (mTranxX) {
                    case LEFT:
                        left += mXInterval;
                        break;
                    case RIGHT:
                        right -= mXInterval;
                        break;
                }
                switch (mTranxY) {
                    case TOP:
                        top += mYInterval;
                        break;
                    case BOTTOM:
                        bottom -= mYInterval;
                        break;
                }
                layoutDecoratedWithMargins(child, left, top, right, bottom);
            } else {
                attachView(child, 0);
                mViewCaches.remove(i);
            }
            if (i == mTopPosition) {
                proportion = layoutTopChild(child);
            } else {
                int number = i - mTopPosition;
                if (i == mTopPosition + mCount - 1) {
                    proportion = 0;
                    number -= 1;
                }
                float origin = 1 - number * SCALE_INTERVAL;
                final float target = origin + proportion * SCALE_INTERVAL;
                float translationY = (child.getHeight()) * (1 - target) / 2 + (number - proportion) * perHeight;
                float translationX = (child.getWidth()) * (1 - target) / 2 + (number - proportion) * perWidth;
                child.setScaleX(target);
                child.setScaleY(target);
                switch (mTranxY) {
                    case TOP:
                        translationY *= -1;
                    case BOTTOM:
                        child.setTranslationY(translationY);
                }
                switch (mTranxX) {
                    case LEFT:
                        translationX *= -1;
                    case RIGHT:
                        child.setTranslationX(translationX);
                }
            }
        }
    }

    private float layoutTopChild(View child) {
        float proportion;
        child.setTranslationX(mDx);
        child.setTranslationY(mDy);
        child.setRotation(getRotation());
        child.setScaleX(1);
        child.setScaleY(1);
        mMinDistance = getMinDistance();
        proportion = getProportion(child);
        if (mOnCardSwipeListener != null && mIsSwipe) {
            mOnCardSwipeListener.onSwipe(child, mTopPosition, mDx, mDy);
        }
        return proportion;
    }

    private void fillCache() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            int position = getPosition(view);
            mViewCaches.put(position, view);
        }
    }

    public float getScale(int position) {
        if (position >= mTopPosition && position < mTopPosition + mCount && position < getItemCount()) {
            if (position == mTopPosition + mCount - 1) {
                return 1 - (position - mTopPosition - 1) * SCALE_INTERVAL;
            }
            return 1 - (position - mTopPosition) * SCALE_INTERVAL;
        }
        return 0;
    }

    public float getTranslationY(int position, int height) {
        float perHeight = mYInterval / (mCount - 1);
        perHeight += perHeight / (mCount - 1);
        if (position >= mTopPosition && position < mTopPosition + mCount && position < getItemCount()) {
            if (position == mTopPosition + mCount - 1) {
                position -= 1;
            }
            return height * (1 - getScale(position)) / 2 + (position - mTopPosition) * perHeight;
        }
        return 0;
    }

    private float getRotation() {
        float degree;
        if (mOrientation == LinearLayout.HORIZONTAL) {
            degree = MAX_DEGREE * mDx / getWidth() * mTouchProportion;
        } else {
            degree = MAX_DEGREE * mDy / getHeight() * mTouchProportion;
        }
        mCardSwipeController.setDegree(degree);
        return degree;
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
            proportion = Math.abs(mDx) / (view.getWidth() / 2.0f);
        } else {
            proportion = Math.abs(mDy) / (view.getHeight() / 2.0f);
        }
        return Math.min(proportion, 1);
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

    private void findTopView() {
        if ((mOrientation == LinearLayout.HORIZONTAL && Math.abs(mDx) > mMinDistance) ||
                (mOrientation == LinearLayout.VERTICAL && Math.abs(mDy) > mMinDistance)) {
            if (mAnimStatus == ANIMATION_IN) {
                if (mOnCardSwipeListener != null) {
                    mOnCardSwipeListener.onAnimInStop(getViewByPosition(mTopPosition), mTopPosition);
                }
            } else if (mAnimStatus == ANIMATION_OUT) {
                if (mOnCardSwipeListener != null) {
                    mOnCardSwipeListener.onAnimOutStop(getViewByPosition(mTopPosition), mTopPosition, mAnimDirection);
                }
            }
            mAnimStatus = NO_ANIMATION;
            mDx = 0;
            mDy = 0;
            mTopPosition++;
            mIsScrollEnabled = false;
            mIsSwipe = false;
            mCardSwipeController.setDegree(0);
        }
    }

    public void setVerticalSwipe(boolean verticalSwipe) {
        this.verticalScroll = verticalSwipe;
    }

    public void setHorizontalSwipe(boolean horizontalSwipe) {
        this.horizontalScroll = horizontalSwipe;
    }

    @Override
    public boolean canScrollVertically() {
        return verticalScroll;
    }

    @Override
    public boolean canScrollHorizontally() {
        return horizontalScroll;
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            if (mTargetPosition != NO_TARGET_POSITION) {
                if (mTargetPosition > mTopPosition) {
                    smoothScrollNext(mTargetPosition);
                } else if (mTargetPosition < mTopPosition) {
                    smoothScrollPre(mTargetPosition);
                } else {
                    if (mAnimPre) {
                        mAnimPre = false;
                        if (mOnCardSwipeListener != null) {
                            mOnCardSwipeListener.onAnimInStop(getViewByPosition(mTopPosition), mTopPosition);
                        }
                    }
                    mTargetPosition = NO_TARGET_POSITION;
                }
            }
            mIsScrollEnabled = true;
        }
    }


    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (mIsScrollEnabled) {
            mDy -= dy;
            fill(recycler);
            return dy;
        }
        return 0;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (mIsScrollEnabled) {
            mDx -= dx;
            fill(recycler);
            return dx;
        }
        return 0;
    }


    void fling(int velocityX, int velocityY) {
        if (findViewByPosition(mTopPosition) != null) {
            CardSmoothScroller scroller = new CardSmoothScroller(mCardSwipeController);
            scroller.prepare(mTopPosition, velocityX, velocityY, this);
            startSmoothScroll(scroller);
        }
    }


    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        if (position != mTopPosition && position <= getItemCount() && position >= 0) {
            if (position > mTopPosition) {
                smoothScrollNext(position);
            } else {
                if (mAnimPre) {
                    mTargetPosition = position;
                } else {
                    smoothScrollPre(position);
                }
            }
        } else {
            mTargetPosition = NO_TARGET_POSITION;
            mAnimPre = false;
        }
    }

    @Override
    public void scrollToPosition(int position) {
        if (position != mTopPosition && position < getItemCount() && position >= 0) {
            mTopPosition = position;
            mDy = 0;
            mDx = 0;
            mAnimStatus = NO_ANIMATION;
            mIsScrollEnabled = false;
            mIsSwipe = false;
            mCardSwipeController.setDegree(0);
            requestLayout();
        }
    }

    private void smoothScrollNext(int position) {
        View view = findViewByPosition(mTopPosition);
        mIsSwipe = true;
        int top = view != null ? view.getTop() : 0;
        int left = view != null ? view.getLeft() : 0;
        int[] downPosition = mCardSwipeController.getDownPosition();
        if (downPosition != null && downPosition.length == 2 && downPosition[0] != -1 && downPosition[1] != -1) {
            setDownPoint(view, downPosition[0], downPosition[1]);
        } else {
            Random random = new Random();
            setDownPoint(view, random.nextInt(view != null ? view.getWidth() : getWidth()) + left, random.nextInt(view != null ?
                    view.getHeight() : getHeight()) + top);
        }
        mTargetPosition = position;
        CardSmoothScroller scroller = new CardSmoothScroller(mCardSwipeController);
        scroller.prepareNext(mTopPosition, this);
        startSmoothScroll(scroller);
        mAnimPre = false;
    }

    private void smoothScrollPre(int position) {
        View view = findViewByPosition(mTopPosition);
        if (mOnCardSwipeListener != null && mAnimPre) {
            mOnCardSwipeListener.onAnimInStop(getViewByPosition(mTopPosition), mTopPosition);
        }
        mIsSwipe = true;
        int top = view != null ? view.getTop() : 0;
        int left = view != null ? view.getLeft() : 0;
        int[] downPosition = mCardSwipeController.getDownPosition();
        if (downPosition != null && downPosition.length == 2 && downPosition[0] != -1 && downPosition[1] != -1) {
            setDownPoint(view, downPosition[0], downPosition[1]);
        } else {
            Random random = new Random();
            setDownPoint(view, random.nextInt(view != null ? view.getWidth() : getWidth()) + left, random.nextInt(view != null ?
                    view.getHeight() : getHeight()) + top);
        }
        mTargetPosition = position;
        CardSmoothScroller scroller = new CardSmoothScroller(mCardSwipeController);
        CardSwipeModel model = mCardSwipeController.generateAnimInModel(mTopPosition);
        model.updateDxAndDy(0, 0);
        mDx = model.getDx();
        mDy = model.getDy();
        mTopPosition -= 1;
        scroller.preparePre(mTopPosition, model, this);
        startSmoothScroll(scroller);
        mAnimPre = true;
    }

    @Override
    public void onItemsChanged(RecyclerView recyclerView) {
        super.onItemsChanged(recyclerView);
        if (mRecycler != null) {
            removeAllViews();
            fill(mRecycler);
        }
    }

    void setDownPoint(View view, int x, int y) {
        if (view != null) {
            Rect rect = new Rect();
            view.getLocalVisibleRect(rect);
            mIsScrollEnabled = rect.contains(x, y);
        } else {
            mIsScrollEnabled = true;
        }
        if (mIsScrollEnabled) {
            if (mOrientation == LinearLayout.HORIZONTAL) {
                float half = getHeight() / 2;
                mTouchProportion = -(y - half - (view != null ? view.getTop() : 0)) / half;
            } else {
                float half = getWidth() / 2;
                mTouchProportion = (x - half - (view != null ? view.getLeft() : 0)) / half;
            }
            mIsSwipe = true;
        }
    }

    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        return null;
    }

    @Override
    public void onStartOut(int direction) {
        mAnimStatus = ANIMATION_OUT;
        mAnimDirection = direction;
        mOnCardSwipeListener.onAnimOutStart(getViewByPosition(mTopPosition), mTopPosition, mAnimDirection);
    }

    @Override
    public void onStartIn() {
        mAnimStatus = ANIMATION_IN;
        mOnCardSwipeListener.onAnimInStart(getViewByPosition(mTopPosition), mTopPosition);
    }

    @Override
    public void onStopIn() {
        mOnCardSwipeListener.onAnimInStop(getViewByPosition(mTopPosition), mTopPosition);
    }

    private View getViewByPosition(int position) {
        View view = findViewByPosition(position);
        if (view != null) {
            return view;
        }
        return mViewCaches.get(position);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState();
        savedState.mTopPosition = mTopPosition;
        return savedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        if (state != null && state instanceof SavedState) {
            mTopPosition = ((SavedState) state).mTopPosition;
        }
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }


    //    @Override
//    public View findViewByPosition(int position) {
//        View view = super.findViewByPosition(position);
//        if (view != null) {
//            Rect rect = new Rect(0, 0, getWidth(), getHeight());
//            int vLeft = (int) view.getTranslationX() + view.getLeft();
//            int vTop = (int) view.getTranslationY() + view.getTop();
//            Rect rect1 = new Rect(vLeft, vTop, vLeft + view.getWidth(), vTop + view.getHeight());
//            if (rect.intersect(rect1)) {
//                return view;
//            }
//        }
//        return null;
//    }
}
