package com.jack.ycr_rv_cardlayout

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import java.util.Objects

/**
 * @创建者 Jack
 * @创建时间 2023/4/21 0021 15:19
 * @描述
 */
class CustomLayoutManager(
    recyclerView: RecyclerView, itemTouchHelper: ItemTouchHelper, manager: ConfigManager
) : RecyclerView.LayoutManager() {

    private val mRecyclerView: RecyclerView = Objects.requireNonNull(recyclerView)
    private val mItemTouchHelper: ItemTouchHelper = Objects.requireNonNull(itemTouchHelper)
    private val mCManager: ConfigManager = Objects.requireNonNull(manager)

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
        RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

    /**
     * 绘制RecyclerView子View
     * 刚打开页面的时候 onLayoutChildren执行了两次[为什么执行两次 暂时不理解 ]
     */
    override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
        // 先移除所有view
        removeAllViews()
        // 在布局之前，将所有的子 View 先 Detach 掉，放入到 Scrap 缓存中
        detachAndScrapAttachedViews(recycler)
        val itemCount = itemCount

        // 当数据源个数大于最大显示数时
        if (itemCount > mCManager.DEFAULT_SHOW_ITEM) {
            // 把数据源倒着循环，这样，第0个数据就在屏幕最上面了            为什么倒序就可以让第0个数据在屏幕最上面  原理是什么 待研究
            for (position in mCManager.DEFAULT_SHOW_ITEM downTo 0) {
                //从缓冲池中获取到itemView
                val view = recycler.getViewForPosition(position)
                // 将 Item View 加入到 RecyclerView 中
                addView(view)
                // 测量 Item View
                measureChildWithMargins(view, 0, 0)
                // getDecoratedMeasuredWidth(view) 可以得到 Item View 的宽度
                // 所以 widthSpace 就是除了 Item View 剩余的值
                val widthSpace = width - getDecoratedMeasuredWidth(view)
                // 同理
                val heightSpace = height - getDecoratedMeasuredHeight(view)

                // recyclerview布局：在这里默认布局是放在 RecyclerView 中心
                // layoutDecoratedWithMargins: 将child显示在RecyclerView上面，left，top，right，bottom规定了显示的区域
                layoutDecoratedWithMargins(
                    view, widthSpace / 2, heightSpace / 2,
                    widthSpace / 2 + getDecoratedMeasuredWidth(view),
                    heightSpace / 2 + getDecoratedMeasuredHeight(view)
                )

                // 其实屏幕上有 mCManager.DEFAULT_SHOW_ITEM + 1 张卡片，但是我们把第 mCManager.DEFAULT_SHOW_ITEM 张和
                // 第 mCManager.DEFAULT_SHOW_ITEM + 1 张卡片重叠在一起，这样看上去就只有 mCManager.DEFAULT_SHOW_ITEM  张
                // 第CardConfig.DEFAULT_SHOW_ITEM + 1张卡片主要是为了保持动画的连贯性
                if (position == mCManager.DEFAULT_SHOW_ITEM) {
                    view.scaleX = 1 - (position - 1) * mCManager.DEFAULT_SCALE
                    view.scaleY = 1 - (position - 1) * mCManager.DEFAULT_SCALE
                    when (mCManager.getStackDirection()) {
                        //从下往上层叠
                        mCManager.UP ->
                            view.translationY =
                                ((position - 1) * view.measuredHeight / mCManager.DEFAULT_TRANSLATE_Y).toFloat()

                        //从上往下层叠
                        mCManager.DOWN ->
                            view.translationY =
                                (-(position - 1) * view.measuredHeight / mCManager.DEFAULT_TRANSLATE_Y).toFloat()

                        else -> view.translationY =
                            (-(position - 1) * view.measuredHeight / mCManager.DEFAULT_TRANSLATE_Y).toFloat()
                    }
                } else if (position > 0) {
                    view.scaleX = 1 - position * mCManager.DEFAULT_SCALE
                    view.scaleY = 1 - position * mCManager.DEFAULT_SCALE
                    when (mCManager.getStackDirection()) {
                        //从下往上层叠
                        mCManager.UP ->
                            view.translationY =
                                (position * view.measuredHeight / mCManager.DEFAULT_TRANSLATE_Y).toFloat()

                        //从上往下层叠
                        mCManager.DOWN ->
                            view.translationY =
                                (-position * view.measuredHeight / mCManager.DEFAULT_TRANSLATE_Y).toFloat()

                        else -> view.translationY =
                            (-position * view.measuredHeight / mCManager.DEFAULT_TRANSLATE_Y).toFloat()
                    }
                } else {
                    //只有顶层的卡片才能滑动
                    view.setOnTouchListener(mOnTouchListener)
                }
            }
        } else {
            // 当数据源个数小于或等于最大显示数时
            for (position in itemCount - 1 downTo 0) {
                val view = recycler.getViewForPosition(position)
                addView(view)
                measureChildWithMargins(view, 0, 0)
                val widthSpace = width - getDecoratedMeasuredWidth(view)
                val heightSpace = height - getDecoratedMeasuredHeight(view)
                // recyclerview 布局
                layoutDecoratedWithMargins(
                    view, widthSpace / 2, heightSpace / 2,
                    widthSpace / 2 + getDecoratedMeasuredWidth(view),
                    heightSpace / 2 + getDecoratedMeasuredHeight(view)
                )
                if (position > 0) {
                    view.scaleX = 1 - position * mCManager.DEFAULT_SCALE
                    view.scaleY = 1 - position * mCManager.DEFAULT_SCALE
                    when (mCManager.getStackDirection()) {
                        //从下往上层叠
                        mCManager.UP ->
                            view.translationY =
                                (position * view.measuredHeight / mCManager.DEFAULT_TRANSLATE_Y).toFloat()

                        //从上往下层叠
                        mCManager.DOWN ->
                            view.translationY =
                                (-position * view.measuredHeight / mCManager.DEFAULT_TRANSLATE_Y).toFloat()

                        else -> view.translationY =
                            (-position * view.measuredHeight / mCManager.DEFAULT_TRANSLATE_Y).toFloat()
                    }
                } else {
                    view.setOnTouchListener(mOnTouchListener)
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private val mOnTouchListener = OnTouchListener { v, event ->
        val childViewHolder = mRecyclerView.getChildViewHolder(v)
        if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
            // 把触摸事件交给 mItemTouchHelper，让其处理卡片滑动事件
            mItemTouchHelper.startSwipe(childViewHolder)
        }
        false
    }
}