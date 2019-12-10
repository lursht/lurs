package lurs.cview

import android.content.Context
import android.text.InputType
import android.text.TextUtils
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.ImageView

class PwdEditView : EditText {

    private var showIv: ImageView? = null
    private var clearIv: ImageView? = null

    private var showRes = 0
    private var hideRes = 0

    private var showPwd = false

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun addShowImage(view: ImageView, showRes: Int, hideRes: Int) {
        showIv = view
        this.showRes = showRes
        this.hideRes = hideRes
        showIv?.setOnClickListener {
            showPwd = !showPwd
            this.inputType = if (showPwd) {
                showIv?.setImageResource(showRes)
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                showIv?.setImageResource(hideRes)
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            this.setSelection(this.text.length)
            this.keyListener =
                DigitsKeyListener.getInstance(resources.getString(R.string.digits))
        }

    }

    fun addClearImage(view: ImageView) {
        clearIv = view
        view.visibility = View.GONE
        clearIv?.setOnClickListener {
            this.setText("")
            clearIv?.visibility = View.GONE
        }
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        if (!TextUtils.isEmpty(this.text)) {
            clearIv?.let {
                if (it.visibility != View.VISIBLE) {
                    it.visibility = View.VISIBLE
                }
            }
        }
    }

}