package lurs.cview.recyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import java.lang.Exception

open class CustomRecyclerView : RecyclerView {

    companion object {
        private const val HEAD_VIEW_TYPE = 3000
        private const val FOOT_VIEW_TYPE = 6000
    }

    private var list: List<*>? = null
    private var headList = arrayListOf<HolderEntity>()
    private var footList = arrayListOf<HolderEntity>()
    private var recyclerItemView: RecyclerItemView? = null
    private var enableScroll = true
    private var scroller: TopSmoothScroller? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    fun setList(list: List<*>) {
        this.list = list
        if (layoutManager == null)
            layoutManager = LinearLayoutManager(context)
        initAdapter()
    }

    fun addHeader(headViewHolder: HeadViewHolder<*>) {
        if (containHeader(headViewHolder) == null)
            headList.add(
                HolderEntity(
                    HEAD_VIEW_TYPE + headList.size,
                    headViewHolder
                )
            )
        initAdapter()
    }

    fun removeHeader(headViewHolder: HeadViewHolder<*>) {
        containHeader(headViewHolder)?.run {
            headList.remove(this)
            notifyDataChanged()
        }
    }

    private fun containHeader(headViewHolder: HeadViewHolder<*>): HolderEntity? {
        headList.forEach {
            if (headViewHolder == it.viewHolder) {
                return it
            }
        }
        return null
    }

    fun addFooter(footViewHolder: FootViewHolder<*>) {
        if (containFooter(footViewHolder) == null)
            footList.add(
                HolderEntity(
                    FOOT_VIEW_TYPE + footList.size,
                    footViewHolder
                )
            )
        initAdapter()
    }

    fun removeFooter(footViewHolder: FootViewHolder<*>) {
        containFooter(footViewHolder)?.run {
            footList.remove(this)
            notifyDataChanged()
        }
    }

    private fun containFooter(footViewHolder: FootViewHolder<*>): HolderEntity? {
        footList.forEach {
            if (footViewHolder == it.viewHolder) {
                return it
            }
        }
        return null
    }

    fun setEnableScroll(isEnable: Boolean) {
        enableScroll = isEnable
    }

    fun addRecyclerItemView(recyclerItemView: RecyclerItemView) {
        this.recyclerItemView = recyclerItemView
        initAdapter()
    }

    fun notifyDataChanged() {
        adapter?.notifyDataSetChanged()
    }

    fun scrollPosition(position: Int) {
        scrollPosition(position, false)
    }

    /**
     *  @param untilHead true表示滑动范围不包含头部，false表示滑动范围包含头部
     */
    fun scrollPosition(position: Int, untilHead: Boolean) {
        if (layoutManager == null) return
        if (layoutManager is LinearLayoutManager) {
            if (scroller == null)
                scroller = TopSmoothScroller(context)
            if (untilHead && position - headList.size > 0) {
                scroller!!.targetPosition = position - headList.size
            } else {
                scroller!!.targetPosition = position
            }
            layoutManager!!.startSmoothScroll(scroller)
        }
    }

    private fun initAdapter() {
        if (adapter != null) {
            notifyDataChanged()
            return
        }
        adapter = object : Adapter<CustomViewHolder<*>>() {

            override fun getItemViewType(position: Int): Int {
                if (position < headList.size) {//在headList中
                    return headList[position].viewType
                } else if (recyclerItemView != null &&
                    itemCount - footList.size > position
                    && position >= headList.size
                ) {//在list中,
                    return recyclerItemView!!.getViewType(position)
                } else if (itemCount - position > 0 && position >= itemCount - footList.size) {//在footList中
                    return footList[position - (itemCount - footList.size)].viewType
                } else {//缺少数据，不展示
                    return -1
                }
            }

            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): CustomViewHolder<*> {
                if (viewType >= HEAD_VIEW_TYPE && viewType < HEAD_VIEW_TYPE + headList.size) {
                    return headList[viewType - HEAD_VIEW_TYPE].viewHolder
                }
                if (viewType >= FOOT_VIEW_TYPE && viewType < FOOT_VIEW_TYPE - footList.size) {
                    return footList[viewType - FOOT_VIEW_TYPE].viewHolder
                }
                if (recyclerItemView == null) return getDefaultHolder()
                return recyclerItemView!!.getViewHolder(parent, viewType)
            }

            override fun getItemCount(): Int {
                if (list == null) return headList.size + footList.size
                return list!!.size + headList.size + footList.size
            }

            override fun onBindViewHolder(holder: CustomViewHolder<*>, position: Int) {
                if (headList.size > position || position >= itemCount - footList.size) {
                    holder.show(position)
                    return
                }
                if (recyclerItemView == null) return
                list?.let {
                    holder.getEntity(it, position)
                    holder.show(position - headList.size)
                    holder.recyclerItemView = recyclerItemView
                }
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_MOVE) {
            if (!enableScroll) {
                return true
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun getDefaultHolder(): CustomViewHolder<String> {
        return object : CustomViewHolder<String>(View(context)) {
            override fun show(position: Int) {
            }
        }
    }

    internal class TopSmoothScroller : LinearSmoothScroller {

        constructor(context: Context) : super(context)

        override fun getHorizontalSnapPreference(): Int {
            return SNAP_TO_START
        }

        override fun getVerticalSnapPreference(): Int {
            return SNAP_TO_START
        }
    }

    internal class HolderEntity(viewType: Int, headViewHolder: HeadViewHolder<*>) {
        var viewType = viewType
        var viewHolder = headViewHolder
    }

    abstract class FootViewHolder<T>(itemView: View) : HeadViewHolder<T>(itemView)

    abstract class HeadViewHolder<T>(itemView: View) : CustomViewHolder<T>(itemView) {

        var data: T? = null

        override fun show(position: Int) {
            show()
        }

        abstract fun show()
    }

    abstract class CustomViewHolder<T>(itemView: View) : ViewHolder(itemView) {

        var entity: T? = null

        internal var recyclerItemView: RecyclerItemView? = null

        abstract fun show(position: Int)

        fun getEntity(list: List<*>, position: Int) {
            try {
                entity = list[position] as T
            } catch (e: Exception) {
                entity = null
            }
        }

    }

    interface RecyclerItemView {

        fun getViewType(position: Int): Int

        fun getViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder<*>

        fun doAction(viewHolder: CustomViewHolder<*>, clickView: View, position: Int)
    }

}