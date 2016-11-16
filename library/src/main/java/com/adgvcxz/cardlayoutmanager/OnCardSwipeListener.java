package com.adgvcxz.cardlayoutmanager;

import android.view.View;

/**
 * zhaowei
 * Created by zhaowei on 2016/11/16.
 */

public interface OnCardSwipeListener {

    void onSwipe(View view, int position, int dx, int dy);
    void animationOutStart(View view, int position);
    void animationOutStop(View view, int position);
    void animationInStart(View view, int position);
    void animationInStop(View view, int position);

}
