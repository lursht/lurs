package lurs.cview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

class CustomViewPager : ViewPager {

    private var list = arrayListOf<View>()

    //是否可以左右滑动？true 可以，像Android原生ViewPager一样。
    // false 禁止ViewPager左右滑动。
    private var scrollable = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    fun setScrollable(scrollable: Boolean) {
        this.scrollable = scrollable
    }

    fun addViewToList(layoutRes: Int) {
        if (layoutRes == 0) return
        addViewToList(LayoutInflater.from(context).inflate(layoutRes, this, false))
    }

    fun addViewToList(view: View) {
        list.add(view)
        if (adapter == null) {
            initAdapter()
        } else {
            adapter?.notifyDataSetChanged()
        }
    }

    private fun initAdapter() {
        adapter = object : PagerAdapter() {
            override fun isViewFromObject(view: View, any: Any): Boolean {
                return view == any
            }

            override fun getCount(): Int {
                return list.size
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                container.addView(list[position])
                return list[position]
            }

            override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
                container.removeView(list[position])
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return scrollable
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return scrollable
    }

}