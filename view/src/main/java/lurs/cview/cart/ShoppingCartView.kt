package lurs.cview.cart

import android.content.Context
import android.util.AttributeSet
import lurs.cview.recyclerview.CustomRecyclerView

class ShoppingCartView : CustomRecyclerView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )
}