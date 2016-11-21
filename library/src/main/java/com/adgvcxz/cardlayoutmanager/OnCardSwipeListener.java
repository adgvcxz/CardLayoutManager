package com.adgvcxz.cardlayoutmanager;

import android.view.View;

/**
 * zhaowei
 * Created by zhaowei on 2016/11/16.
 */

public interface OnCardSwipeListener {

    /**
     * 滑动过程中的时间
     */
    void onSwipe(View view, int position, int dx, int dy);

    /**
     * 卡片滑出开始
     * @param direction CardLayoutManager.DIRECTION_START 代表左和上 CardLayoutManager.DIRECTION_END 代表右和下
     */
    void onAnimOutStart(View view, int position, int direction);

    /**
     * 卡片滑出结束
     * 需要注意的是 卡片滑出以后 可能已经被recycler回收了。
     * @param direction CardLayoutManager.DIRECTION_START 代表左和上 CardLayoutManager.DIRECTION_END 代表右和下
     */
    void onAnimOutStop(View view, int position, int direction);

    /**
     * 卡片滑入开始
     */
    void onAnimInStart(View view, int position);

    /**
     * 卡片滑入结束
     */
    void onAnimInStop(View view, int position);

}
