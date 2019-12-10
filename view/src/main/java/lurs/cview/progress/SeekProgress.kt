package lurs.cview.progress

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.cview.R
import kotlin.math.absoluteValue

class SeekProgress : View {

    private var firstLinePaint: Paint? = null
    private var secondLinePaint: Paint? = null
    private var barPaint: Paint? = null
    private var barShapePaint: Paint? = null
    private var textPaint: Paint? = null

    private var firstLineColor = -0x744201
    private var secondLineColor = -0x744201
    private var barColor = -0x744201
    private var barShapeColor = -0x744201
    private var textColor = Color.WHITE

    private var lineHeight = 0
    private var shapeWidth = 0.5f
    private var barRadius = 0
    private var textSize = 0f
    private var textPadding = 0

    private var progress = 0
    private var step = 1
    private var min = 0
    private var max = 100
    private var unit = ""

    private var barPointX = 0f
    private var barPointY = 0f

    var onProgressListener: OnProgressListener? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs!!)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs!!)
    }

    private fun init(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SeekProgress)
        for (i in 0 until typedArray.indexCount) {
            val resId = typedArray.getIndex(i)
            when (resId) {
                R.styleable.SeekProgress_seek_first_color -> {
                    firstLineColor = typedArray.getColor(resId, Color.BLUE)
                }
                R.styleable.SeekProgress_seek_second_color -> {
                    secondLineColor = typedArray.getColor(resId, Color.BLUE)
                }
                R.styleable.SeekProgress_seek_bar_color -> {
                    barColor = typedArray.getColor(resId, Color.BLUE)
                }
                R.styleable.SeekProgress_seek_bar_shape_color -> {
                    barShapeColor = typedArray.getColor(resId, Color.BLUE)
                }
                R.styleable.SeekProgress_seek_text_color -> {
                    textColor = typedArray.getColor(resId, Color.BLUE)
                }
                R.styleable.SeekProgress_seek_height -> {
                    lineHeight = typedArray.getDimension(resId, 10f).toInt()
                }
                R.styleable.SeekProgress_seek_bar_radius -> {
                    barRadius = typedArray.getDimension(resId, 10f).toInt()
                }
                R.styleable.SeekProgress_seek_bar_shape_width -> {
                    shapeWidth = typedArray.getDimension(resId, 1f)
                }
                R.styleable.SeekProgress_seek_text_padding -> {
                    textPadding = typedArray.getDimension(resId, 0f).toInt()
                }
                R.styleable.SeekProgress_seek_progress -> {
                    progress = typedArray.getInt(resId, 50)
                }
                R.styleable.SeekProgress_seek_progress_max -> {
                    max = typedArray.getInt(resId, 100)
                }
                R.styleable.SeekProgress_seek_progress_min -> {
                    min = typedArray.getInt(resId, 0)
                }
                R.styleable.SeekProgress_seek_progress_step -> {
                    step = typedArray.getInt(resId, 1)
                }
                R.styleable.SeekProgress_seek_text_size -> {
                    textSize = typedArray.getDimension(resId, sp2px(10f))
                }
                R.styleable.SeekProgress_seek_text_unit -> {
                    typedArray.getString(resId)?.let {
                        unit = it
                    }
                }
            }
        }
        if (textSize == 0f) {
            textSize = sp2px(12f)
        }
        if (lineHeight == 0) {
            lineHeight = dp2px(2)
        }
        if (barRadius == 0) {
            barRadius = dp2px(2)
        }
        if (progress > max) {
            progress = max
        }
        if (progress < min) {
            progress = min
        }
        if (step <= 0) {
            step = 1
        }
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val width = if (widthMode == MeasureSpec.EXACTLY) {
            widthSize
        } else {
            dp2px(260)
        }
        val height = if (heightMode == MeasureSpec.EXACTLY) {
            heightSize
        } else {
            dp2px(80)
        }
        setMeasuredDimension(width, height)
    }

    fun getProgress(): Int {
        return progress
    }

    fun setProgress(progress: Int) {
        when {
            progress < min -> {
                this.progress = min
            }
            progress > max -> {
                this.progress = max
            }
            (progress - this.progress).absoluteValue > step -> {
                this.progress = getStepProgress(progress - min)
            }
            else -> {
                return
            }
        }
        invalidate()
    }

    fun setStepValue(step: Int) {
        this.step = step
        if (this.step <= 0) {
            this.step = 1
        }
        if (this.step > max) {
            this.step = max
        }
    }

    fun setUnit(unit: String) {
        this.unit = unit
    }

    fun setRange(min: Int, max: Int) {
        this.min = min
        this.max = max
    }

    private var downX = 0f
    private var downY = 0f
    private var isBreak = false

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = it.x
                    downY = it.y
                    return isValuePoint()
                }
                else -> {
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            downX = it.x
            downY = it.y
            when (it.action) {
                MotionEvent.ACTION_MOVE -> {
                    if (isValuePoint() && !isBreak) {
                        drawProgress()
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (isValuePoint() && !isBreak) {
                        drawProgress()
                    }
                    onProgressListener?.onProgress(this.progress, step, true)
                    isBreak = false
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun drawProgress() {
        val minDis = width / (max - min)
        val p = ((downX * (max - min)) / width).toInt()
        when {
            downX < minDis -> {
                progress = min
            }
            width - downX < minDis -> {
                progress = max
            }
            (p - progress).absoluteValue >= step -> {
                progress = getStepProgress(p)
            }
            else -> {
                return
            }
        }
        onProgressListener?.onProgress(this.progress, step, false)
        invalidate()
    }

    private fun getStepProgress(progress: Int): Int {
        when (step == 1) {
            true -> {
                return progress + min
            }
            false -> {
                val offsetP = progress % step
                var count = (progress - offsetP) / step
                if (offsetP >= step / 2) {
                    count++
                }
                return count * step + min
            }
        }
    }

    private fun isValuePoint(): Boolean {
        barPointY = height / 2f
        if (downY < barPointY - barRadius || downY > barPointY + barRadius) {
            isBreak = true
            return false
        }
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            barPointX =
                barRadius + (width - barRadius * 2) * (progress - min) / (max - min).toFloat()
            drawFirstLine(canvas)
            drawSecondLine(canvas)
            drawBar(canvas)
        }
    }

    private fun drawFirstLine(canvas: Canvas) {
        val radius = lineHeight / 2f
        val stopX = barPointX
        val startY = height / 2f
        canvas.drawLine(
            radius,
            startY,
            stopX,
            startY,
            getFirstLinePaint(true)
        )
        canvas.drawCircle(radius, startY, radius, getFirstLinePaint(false))
    }

    private fun drawSecondLine(canvas: Canvas) {
        val radius = lineHeight / 2f
        val startX = barPointX
        val stopX = width - radius
        val startY = height / 2f
        canvas.drawLine(startX, startY, stopX, startY, getSecondLinePaint(true))
        canvas.drawCircle(stopX, startY, radius, getSecondLinePaint(false))
    }

    private fun drawBar(canvas: Canvas) {
        var rx = barPointX
        if (rx > (width - barRadius)) {
            rx = (width - barRadius).toFloat()
        }
        val ry = height / 2f
        canvas.drawCircle(rx, ry, barRadius.toFloat(), getBarPaint())
        canvas.drawCircle(rx, ry, barRadius + shapeWidth, getBarShapePaint())
        val text = "$progress$unit"
        val rect = Rect()
        getTextPaint().getTextBounds(text, 0, text.length, rect)
        var x = barPointX - rect.right / 2
        if (x < 0) x = 0f
        if (x > width - rect.right) x = width - rect.right.toFloat()
        val y = ry + barRadius + textSize + textPadding
        canvas.drawText(text, x, y, getTextPaint())
    }

    private fun getFirstLinePaint(line: Boolean): Paint {
        if (firstLinePaint == null) {
            firstLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        }
        firstLinePaint?.color = firstLineColor
        if (line)
            firstLinePaint?.strokeWidth = lineHeight.toFloat()
        else
            firstLinePaint?.strokeWidth = 1f
        return firstLinePaint!!
    }

    private fun getSecondLinePaint(line: Boolean): Paint {
        if (secondLinePaint == null) {
            secondLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        }
        secondLinePaint?.color = secondLineColor
        if (line)
            secondLinePaint?.strokeWidth = lineHeight.toFloat()
        else
            secondLinePaint?.strokeWidth = 1f
        return secondLinePaint!!
    }

    private fun getBarPaint(): Paint {
        if (barPaint == null) {
            barPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        }
        barPaint?.color = barColor
        barPaint?.strokeWidth = 1f
        return barPaint!!
    }

    private fun getBarShapePaint(): Paint {
        if (barShapePaint == null) {
            barShapePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        }
        barShapePaint?.color = barShapeColor
        barShapePaint?.strokeWidth = 0.1f
        barShapePaint?.style = Paint.Style.STROKE
        return barShapePaint!!
    }

    private fun getTextPaint(): Paint {
        if (textPaint == null) {
            textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        }
        textPaint?.color = textColor
        textPaint?.textSize = textSize
        return textPaint!!
    }

    private fun dp2px(dp: Int): Int {
        return (context.resources.displayMetrics.density * dp + 0.5).toInt()
    }

    private fun sp2px(sp: Float): Float {
        return this.context.resources.displayMetrics.scaledDensity * sp
    }

    interface OnProgressListener {
        fun onProgress(progress: Int, step: Int, keyUp: Boolean)
    }
}