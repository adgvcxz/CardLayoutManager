package com.adgvcxz.cardlayoutmanager;

import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;

import static com.adgvcxz.cardlayoutmanager.CardLayoutManager.DIRECTION_END;
import static com.adgvcxz.cardlayoutmanager.CardLayoutManager.DIRECTION_START;

/**
 * zhaowei
 * Created by zhaowei on 2016/11/16.
 */

public abstract class BaseCardSwipeController extends CardSwipeController {

    protected int mOrientation;

    public BaseCardSwipeController(int orientation) {
        mOrientation = orientation;
    }

    @Override
    public CardSwipeModel generateSwipeOutModel(View view, int position, int dx, int dy, int velocityX, int velocityY) {
        return generateSwipeOutModel(view, dx, dy, velocityX, velocityY);
    }

    @Override
    public CardSwipeModel generateSwipeInModel(View view, int position, int dx, int dy, int velocityX, int velocityY) {
        return new CardSwipeModel(0, 0, 350, new OvershootInterpolator());
    }

    @Override
    public boolean canSwipeOut(View view, int position, int dx, int dy, int velocityX, int velocityY) {
        int width = view.getWidth();
        int height = view.getHeight();
        boolean isOverBounds = mOrientation == LinearLayout.HORIZONTAL ? Math.abs(dx) >= width / 2 : Math.abs(dy) >= height / 2;
        return isOverBounds || exceed(velocityX, velocityY);
    }

    protected boolean exceed(int velocityX, int velocityY) {
        velocityX = Math.abs(velocityX);
        velocityY = Math.abs(velocityY);
        if (mOrientation == LinearLayout.HORIZONTAL) {
            return velocityX > mMinVelocity * 3 && velocityX >= velocityY;
        } else {
            return velocityY > mMinVelocity * 3 && velocityY >= velocityX;
        }
    }

    protected int calculateDistance(int x, int y, int x1, int y1) {
        return (int) Math.sqrt(Math.pow((x - x1), 2) + Math.pow((y - y1), 2));
    }

    protected CardSwipeModel generateSwipeOutModel(View view, int dx, int dy, int velocityX, int velocityY) {
        if (mOrientation == LinearLayout.HORIZONTAL) {
            int direction = dx > 0 ? DIRECTION_END : DIRECTION_START;
            float slope = dy / dx;
            int x = (int) ((mMaxWidth - view.getLeft()) / Math.cos(Math.PI / 180 * getDegree()) * 1.5f) * (dx > 0 ? 1 : -1);
            int y = (int) (x * slope);
            int duration;
            int distance = calculateDistance(x, y, dx, dy);
            if (Math.abs(velocityX) > mMinVelocity) {
                duration = (int) Math.min((float) distance / Math.abs(velocityX) * 1800, 450);
            } else {
                duration = Math.max(350, distance / 2);
            }
            return new CardSwipeModel(x, y, duration, new DecelerateInterpolator(), direction);
        } else {
            int direction = dy > 0 ? DIRECTION_END : DIRECTION_START;
            float slope = dx / dy;
            int y = (int) ((mMaxHeight - view.getTop()) / Math.cos(Math.PI / 180 * getDegree()) * 1.5f) * (dy > 0 ? 1 : -1);
            int x = (int) (y * slope);
            int duration;
            int distance = calculateDistance(x, y, dx, dy);
            if (Math.abs(velocityY) > mMinVelocity) {
                duration = (int) Math.min((float) distance / Math.abs(velocityY) * 1800, 450);
            } else {
                duration = Math.max(350, distance / 2);
            }
            return new CardSwipeModel(x, y, duration, new DecelerateInterpolator(), direction);
        }
    }
}
