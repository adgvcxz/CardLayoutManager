package com.adgvcxz.cardlayoutmanager;

import android.view.View;

/**
 * zhaowei
 * Created by zhaowei on 2016/11/16.
 */

public interface CardSwipeController {

    CardSwipeModel animationOut();

    boolean canSwipeOut(View view, int dx, int dy, int velocity);

}
