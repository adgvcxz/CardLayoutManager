package com.adgvcxz.cardlayoutmanager;

/**
 * zhaowei
 * Created by zhaowei on 2016/11/16.
 */

public class RandomCardSwipeController implements CardSwipeController {
    @Override
    public CardSwipeModel animationOut() {
        return null;
    }

    @Override
    public CardSwipeModel animationIn() {
        return null;
    }

    @Override
    public boolean canSwipeOut() {
        return false;
    }
}
