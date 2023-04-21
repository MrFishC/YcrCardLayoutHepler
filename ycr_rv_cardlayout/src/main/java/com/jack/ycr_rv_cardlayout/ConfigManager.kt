package com.jack.ycr_rv_cardlayout

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * @创建者 Jack
 * @创建时间 2023/4/21 0021 14:44
 * @描述
 */
class ConfigManager {
    /**
     * 显示可见的ITEM数量
     */
    var DEFAULT_SHOW_ITEM = 3
        get() = field
        set(value) {
            field = value
        }

    /**
     * 默认缩放的比例
     */
    var DEFAULT_SCALE = 0.15f
        get() = field
        set(value) {
            field = value
        }

    /**
     * ITEM Y轴偏移量时按照12等分计算
     */
    var DEFAULT_TRANSLATE_Y = 12
        get() = field
        set(value) {
            field = value
        }

    /**
     * ITEM 滑动时默认倾斜的角度
     */
    var DEFAULT_ROTATE_DEGREE = 15f
        get() = field
        set(value) {
            field = value
        }

    /**
     * ITEM 从上边滑出
     */
    var SWIPED_UP = 1 shl 1
        get() = field

    /**
     * ITEM 从下边滑出
     */
    var SWIPED_DOWN = 1 shl 2
        get() = field

    /**
     * ITEM 从左边滑出
     */
    var SWIPED_LEFT = 1 shl 3
        get() = field

    /**
     * ITEM 从右边滑出
     */
    var SWIPED_RIGHT = 1 shl 4
        get() = field

    /**
     * ITEM 从未知方向滑出
     */
    var SWIPED_NONE = 1 shl 5
        get() = field

    /**
     * ITEM 向左边滑动
     */
    var SWIPING_LEFT = 1 shl 6
        get() = field

    /**
     * ITEM 向右边滑动
     */
    var SWIPING_RIGHT = 1 shl 7
        get() = field

    /**
     * ITEM 从未知方向滑动[这里是指既不偏左也不偏右]
     */
    var SWIPING_NONE = 1 shl 8
        get() = field

    /**
     * ITEM的层叠方式
     */
    val UP = 2
        get() = field

    val DOWN = 2 shl 1
        get() = field

    open fun getSwipeDirection(): Int {
        return ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.UP or ItemTouchHelper.DOWN
    }

    /**
     * 不使用系统默认的方法[系统的方法默认返回0.5f，RecyclerView的item移动50%将被视为滑动。] 单独抽离出来 方便自定义该值
     */
    open fun getSwipeThreshold(): Float {
        return 0.5f
    }

    open fun getThreshold(recyclerView: RecyclerView): Float {
        return recyclerView.width * getSwipeThreshold()
    }

    /**
     * 是否无限循环
     */
    open fun isLoopCard(): Boolean {
        return false
    }

    /**
     * ITEM默认堆叠的方向           目前只支持上下方向
     */
    open fun getStackDirection(): Int {
        return DOWN
    }

}