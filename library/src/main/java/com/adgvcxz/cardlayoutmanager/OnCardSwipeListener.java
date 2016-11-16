package com.adgvcxz.cardlayoutmanager;

import android.view.View;

/**
 * zhaowei
 * Created by zhaowei on 2016/11/16.
 */

public interface OnCardSwipeListener {

    void onSwipe(View view, int position, int dx, int dy);
    void onAnimOutStart(View view, int position, int direction);
    void onAnimOutStop(View view, int position, int direction);
    void onAnimInStart(View view, int position);
    void onAnimInStop(View view, int position);

}
