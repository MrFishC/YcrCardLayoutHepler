package com.jack.ycrcardlayouthepler

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.jack.ycr_rv_cardlayout.ConfigManager
import com.jack.ycr_rv_cardlayout.CustomItemTouchHelperCallBackImp
import com.jack.ycr_rv_cardlayout.CustomLayoutManager
import com.jack.ycr_rv_cardlayout.OnItemSwipeListener
import java.util.Objects

class MainActivity : AppCompatActivity() {
    private val list: MutableList<Int> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initData()
    }

    private fun initView() {
        val recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
        val adapter = MyAdapter()
        recyclerView.adapter = adapter
        val manager = ConfigManager()
        val callBackImp = CustomItemTouchHelperCallBackImp(adapter, list, manager)
        callBackImp.setOnSwipedListener(object : OnItemSwipeListener<Int> {
            override fun onItemSwiping(
                viewHolder: RecyclerView.ViewHolder,
                ratio: Float,
                direction: Int
            ) {
                when (direction) {
                    manager.SWIPING_LEFT -> {
                        println("向左侧滑动")
                    }
                    manager.SWIPING_RIGHT -> {
                        println("向右侧滑动")
                    }
                    else -> {
                        println("向未知方向滑动")
                    }
                }
            }

            override fun onItemSwiped(viewHolder: RecyclerView.ViewHolder, t: Int, direction: Int) {
                when (direction) {
                    manager.SWIPED_UP -> {
                        println("从上方滑出")
                    }
                    manager.SWIPED_DOWN -> {
                        println("从下方滑出")
                    }
                    manager.SWIPED_LEFT -> {
                        println("从左侧滑出")
                    }
                    manager.SWIPED_RIGHT -> {
                        println("从右侧滑出")
                    }
                    else -> {
                        println("从未知方向滑出")
                    }
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onSwipedAllItem() {
                println("卡片全部滑出")
                //根据实际业务来实现 加载更多
                recyclerView.postDelayed({
                    initData()
                    Objects.requireNonNull(recyclerView.adapter).notifyDataSetChanged()
                }, 1500L)
            }
        })
        val touchHelper = ItemTouchHelper(callBackImp)
        val cardLayoutManager = CustomLayoutManager(recyclerView, touchHelper, manager)
        recyclerView.layoutManager = cardLayoutManager
        touchHelper.attachToRecyclerView(recyclerView)
    }

    private fun initData() {
        list.add(R.drawable.icon_common_bg)
        list.add(R.drawable.icon_common_bg)
        list.add(R.drawable.icon_common_bg)
        list.add(R.drawable.icon_common_bg)
        list.add(R.drawable.icon_common_bg)
        list.add(R.drawable.icon_common_bg)
        list.add(R.drawable.icon_common_bg)
        list.add(R.drawable.icon_common_bg)
        list.add(R.drawable.icon_common_bg)
    }

    private inner class MyAdapter : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.simple_rv_item, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {}
        override fun getItemCount(): Int {
            return list.size
        }

        inner class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(
            itemView!!
        )
    }
}