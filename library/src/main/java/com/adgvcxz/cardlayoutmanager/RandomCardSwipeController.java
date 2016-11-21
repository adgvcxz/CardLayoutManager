package com.adgvcxz.cardlayoutmanager;

import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.LinearLayout;

import java.util.Random;

import static com.adgvcxz.cardlayoutmanager.CardLayoutManager.DIRECTION_END;
import static com.adgvcxz.cardlayoutmanager.CardLayoutManager.DIRECTION_START;

/**
 * zhaowei
 * Created by zhaowei on 2016/11/16.
 */

public class RandomCardSwipeController extends BaseCardSwipeController {

    public RandomCardSwipeController(int orientation) {
        super(orientation);
    }

    @Override
    public CardSwipeModel generateAnimOutModel(View view, int position) {
        return generateAnimModel();
    }

    @Override
    public CardSwipeModel generateAnimInModel(int position) {
        return generateAnimModel();
    }

    @Override
    public int[] getDownPosition() {
        return new int[]{-1, -1};
    }

    protected CardSwipeModel generateAnimModel() {
        Random random = new Random();
        int direction = random.nextInt(DIRECTION_END) + DIRECTION_START;
        if (mOrientation == LinearLayout.HORIZONTAL) {
            int x = (int) (mMaxWidth / Math.cos(12)) * (direction == DIRECTION_END ? 1 : -1);
            int y = 7;
            return new CardSwipeModel(x, y, random.nextInt(Math.abs(x) / 3) + Math.abs(x) / 3, new AnticipateOvershootInterpolator(), direction);
        } else {
            int y = (int) (mMaxHeight / Math.cos(12)) * (direction == DIRECTION_END ? 1 : -1);
            int x = random.nextInt(mMaxWidth / 4) * (random.nextBoolean() ? 1 : -1);
            return new CardSwipeModel(x, y, random.nextInt(Math.abs(y) / 4) + Math.abs(y) / 4, new AnticipateOvershootInterpolator(), direction);
        }
    }
}
