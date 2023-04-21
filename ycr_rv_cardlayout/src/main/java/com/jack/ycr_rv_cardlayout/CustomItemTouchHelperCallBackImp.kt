package com.jack.ycr_rv_cardlayout

import android.annotation.SuppressLint
import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.Objects
import kotlin.math.abs

/**
 * @创建者 Jack
 * @创建时间 2023/4/21 0021 15:00
 * @描述
 */
class CustomItemTouchHelperCallBackImp<T>(
    adapter: RecyclerView.Adapter<*>,
    dataList: MutableList<T>,
    manager: ConfigManager
) : ItemTouchHelper.Callback() {
    private var mAdapter: RecyclerView.Adapter<*> = Objects.requireNonNull(adapter)
    private var mDataList: MutableList<T> = Objects.requireNonNull(dataList)
    private var mListener: OnItemSwipeListener<T>? = null
    private var mCManager: ConfigManager = Objects.requireNonNull(manager)

    fun setOnSwipedListener(mListener: OnItemSwipeListener<T>?) {
        this.mListener = mListener
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = 0
        var swipeFlags = 0
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is CustomLayoutManager) {
            swipeFlags = mCManager.getSwipeDirection()
        }
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) = false

    @SuppressLint("NotifyDataSetChanged")
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // 移除onTouchListener,防止触摸 滑动之间冲突
        viewHolder.itemView.setOnTouchListener(null)

        val layoutPosition = viewHolder.layoutPosition
        val remove: T = mDataList.removeAt(layoutPosition)

        if (mCManager.isLoopCard()) {
            mDataList.add(remove)
        }

        //主动调用刷新，否则会出现只有顶层卡片才能滑动
        mAdapter.notifyDataSetChanged()

        //使用接口回调进行拓展1
        if (mListener != null) {
            when (direction) {
                ItemTouchHelper.UP -> mListener!!.onItemSwiped(
                    viewHolder,
                    remove,
                    mCManager.SWIPED_UP
                )

                ItemTouchHelper.DOWN -> mListener!!.onItemSwiped(
                    viewHolder,
                    remove,
                    mCManager.SWIPED_DOWN
                )

                ItemTouchHelper.LEFT -> mListener!!.onItemSwiped(
                    viewHolder,
                    remove,
                    mCManager.SWIPED_LEFT
                )

                ItemTouchHelper.RIGHT -> mListener!!.onItemSwiped(
                    viewHolder,
                    remove,
                    mCManager.SWIPED_RIGHT
                )

                else -> mListener!!.onItemSwiped(viewHolder, remove, mCManager.SWIPED_NONE)
            }
        }

        //使用接口回调进行拓展2
        // 当没有数据时回调 mListener
        if (mAdapter.itemCount == 0 && mListener != null && !mCManager.isLoopCard()) {
            mListener!!.onSwipedAllItem()
        }
    }

    override fun isItemViewSwipeEnabled() = false

    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView = viewHolder.itemView
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            //滑动的比例
            var ratio: Float = dX / mCManager.getThreshold(recyclerView)

            // ratio 最大为 1 或 -1
            if (ratio > 1) {
                ratio = 1f
            } else if (ratio < -1) {
                ratio = -1f
            }

            //旋转的角度
            itemView.rotation = ratio * mCManager.DEFAULT_ROTATE_DEGREE
            val childCount = recyclerView.childCount

            //卡片滑动过程中   对view进行缩放处理  [这里的逻辑需要跟自定义的RecyclerView.LayoutManager实现类onLayoutChildren方法对应]        具体的缩放效果可以自行通过计算来尝试
            // 当数据源个数大于最大显示数时
            if (childCount > mCManager.DEFAULT_SHOW_ITEM) {
                //position：从1开始       for循环中定义position的初始值以及其边界，目的是为了让第一张不做处理
                for (position in 1 until childCount - 1) {
                    val index = childCount - position - 1
                    val view = recyclerView.getChildAt(position)
                    //通过调用setScaleX()和setScaleY()方法，可以实现View的缩放
                    view.scaleX =
                        1 - index * mCManager.DEFAULT_SCALE + abs(ratio) * mCManager.DEFAULT_SCALE
                    view.scaleY =
                        1 - index * mCManager.DEFAULT_SCALE + abs(ratio) * mCManager.DEFAULT_SCALE
                    when (mCManager.getStackDirection()) {
                        //从下往上层叠
                        mCManager.UP -> view.translationY =
                            (index - abs(ratio)) * itemView.measuredHeight / mCManager.DEFAULT_TRANSLATE_Y
                        //从上往下层叠
                        mCManager.DOWN -> view.translationY =
                            -(index - abs(ratio)) * itemView.measuredHeight / mCManager.DEFAULT_TRANSLATE_Y

                        else -> view.translationY =
                            -(index - abs(ratio)) * itemView.measuredHeight / mCManager.DEFAULT_TRANSLATE_Y
                    }
                }
            } else {
                // 当数据源个数小于或等于最大显示数时      for循环中定义position的初始值以及其边界，目的是为了让最后一张不做处理
                for (position in 0 until childCount - 1) {
                    val index = childCount - position - 1
                    val view = recyclerView.getChildAt(position)
                    view.scaleX =
                        1 - index * mCManager.DEFAULT_SCALE + abs(ratio) * mCManager.DEFAULT_SCALE
                    view.scaleY =
                        1 - index * mCManager.DEFAULT_SCALE + abs(ratio) * mCManager.DEFAULT_SCALE
                    when (mCManager.getStackDirection()) {
                        //从下往上层叠
                        mCManager.UP ->
                            view.translationY =
                                (index - abs(ratio)) * itemView.measuredHeight / mCManager.DEFAULT_TRANSLATE_Y

                        //从上往下层叠
                        mCManager.DOWN ->
                            view.translationY =
                                -(index - abs(ratio)) * itemView.measuredHeight / mCManager.DEFAULT_TRANSLATE_Y

                        else -> view.translationY =
                            -(index - abs(ratio)) * itemView.measuredHeight / mCManager.DEFAULT_TRANSLATE_Y
                    }
                }
            }
            //由于增加了上下方向 这里 可以按需添加业务逻辑
            if (ratio != 0f) {
                if (mListener != null) {
                    mListener!!.onItemSwiping(
                        viewHolder,
                        ratio,
                        if (ratio < 0) mCManager.SWIPING_LEFT else mCManager.SWIPING_RIGHT
                    )
                }
            } else {
                if (mListener != null) {
                    mListener!!.onItemSwiping(viewHolder, ratio, mCManager.SWIPING_NONE)
                }
            }
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        //因为Item View 重用机制[这块没有去研究]，第一层卡片滑出去之后，第二层卡片会出现偏移，处理方案：clearView方法中添加如下代码[重置]
        viewHolder.itemView.rotation = 0f
    }
}