package com.jack.ycr_rv_cardlayout

import androidx.recyclerview.widget.RecyclerView

/**
 * @创建者 Jack
 * @创建时间 2023/4/21 0021 15:20
 * @描述
 */
interface OnItemSwipeListener<T> {
    /**
     * RV的item正在滑动时回调
     */
    fun onItemSwiping(viewHolder: RecyclerView.ViewHolder, ratio: Float, direction: Int)

    /**
     * RV的item完全滑出时触发的回调
     */
    fun onItemSwiped(viewHolder: RecyclerView.ViewHolder, t: T, direction: Int)

    /**
     * RV所有的item全部滑出时触发的回调
     */
    fun onSwipedAllItem()
}