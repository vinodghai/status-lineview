package com.example.titanstatusline.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.FontRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import com.example.titanstatusline.R
import java.util.*


class StatusLineView : View {

    private val DEFAULT_CIRCLE_RADIUS = 4f
    private val DEFAULT_TEXT_MARGIN_BOTTOM = 8f
    private val DEFAULT_MIN_TEXT_GAP = 8f
    private val DEFAULT_TEXT_FONT_SIZE = 10f
    private val DEFAULT_LINE_STROKE_WIDTH = 1f

    private var stateList: List<LineState>? = null
    private var statePaint: Paint? = null
    private var linePaint: Paint? = null
    private var circleRadius = dpToPx(DEFAULT_CIRCLE_RADIUS)
    private var textMarginBottom = dpToPx(DEFAULT_TEXT_MARGIN_BOTTOM)
    private var minTextGap = dpToPx(DEFAULT_MIN_TEXT_GAP)
    private val lineSpace = dpToPx(3f)


    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        if (attrs != null)
            init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        if (attrs != null)
            init(context, attrs)
    }

    constructor(context: Context?) : super(context)

    fun setStateList(stateList: List<LineState>) {
        this.stateList = stateList
        setTextWidths()
        requestLayout()
    }

    private fun init(context: Context, attrs: AttributeSet) {
        this.initPaint()
        this.initNewUserStatusList()

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.StatusLineView)

        try {

            val fontStyle =
                typedArray.getResourceId(R.styleable.StatusLineView_status_font_family, -1)
            if (fontStyle != -1)
                setFontFamily(fontStyle)

            val scaleFactor = typedArray.getFloat(R.styleable.StatusLineView_scale_size, 0f)
            if (scaleFactor > 0)
                scaleViews(scaleFactor)

            val fontSize = typedArray.getDimensionPixelSize(R.styleable.StatusLineView_font_size, 0)
            if (fontSize > 0)
                statePaint!!.textSize = fontSize.toFloat()

            val circleRadius =
                typedArray.getDimensionPixelSize(R.styleable.StatusLineView_circle_radius, 0)
            if (circleRadius > 0)
                this.circleRadius = circleRadius

            val textMgBtm =
                typedArray.getDimensionPixelSize(R.styleable.StatusLineView_text_margin_bottom, 0)
            if (textMgBtm > 0)
                this.textMarginBottom = textMgBtm

            val minTextGap =
                typedArray.getDimensionPixelSize(R.styleable.StatusLineView_min_text_gap, 0)
            if (minTextGap > 0)
                this.minTextGap = minTextGap

            val lineStrokeWidth =
                typedArray.getDimensionPixelSize(R.styleable.StatusLineView_line_stroke_width, 0)
            if (lineStrokeWidth > 0)
                this.linePaint!!.strokeWidth = lineStrokeWidth.toFloat()

            setTextWidths()

        } finally {

            typedArray.recycle()
        }
    }

    private fun initPaint() {
        this.statePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        this.linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        this.linePaint!!.strokeWidth = dpToPx(DEFAULT_LINE_STROKE_WIDTH).toFloat()
        this.statePaint!!.textSize = spToPx(DEFAULT_TEXT_FONT_SIZE).toFloat()
    }

    private fun setFontFamily(@FontRes fontRes: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            this.statePaint?.typeface = ResourcesCompat.getFont(getContext(), fontRes)
        else
            this.statePaint?.typeface = getResources().getFont(fontRes)
    }

    private fun initNewUserStatusList() {
        val initList: MutableList<LineState> = ArrayList()
        for (i in 0..4)
            initList.add(
                LineState(
                    "",
                    LineState.State.LOCKED()
                )
            )
        this.stateList = initList
    }

    private fun setTextWidths() {
        val rect = Rect()
        for (state in stateList!!) {
            var maxWidth = 0
            val texts =
                state.getStatus().split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (text in texts) {
                val width = getTextWidth(rect, text)
                if (width > maxWidth)
                    maxWidth = width
            }

            state.setTextWidth(maxWidth)
        }
    }

    private fun getTextWidth(bounds: Rect, text: String): Int {
        statePaint!!.getTextBounds(text, 0, text.length, bounds)
        return bounds.width()
    }

    private fun getAllTextHeights(bounds: Rect, texts: Array<String>): Int {
        var height = 0
        for (text in texts)
            height += getTextHeight(bounds, text) + lineSpace
        return height
    }

    private fun getTextHeight(bounds: Rect, text: String): Int {
        statePaint!!.getTextBounds(text, 0, text.length, bounds)
        return bounds.height()
    }

    private fun scaleViews(scaleFactor: Float) {
        circleRadius = dpToPx(DEFAULT_CIRCLE_RADIUS * scaleFactor)
        textMarginBottom = dpToPx(DEFAULT_TEXT_MARGIN_BOTTOM * scaleFactor)
        minTextGap = dpToPx(DEFAULT_MIN_TEXT_GAP * scaleFactor)
        statePaint!!.textSize = spToPx(DEFAULT_TEXT_FONT_SIZE * scaleFactor).toFloat()
        linePaint!!.strokeWidth = dpToPx(DEFAULT_LINE_STROKE_WIDTH * scaleFactor).toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        var width: Int
        var height: Int

        when (widthMode) {

            MeasureSpec.UNSPECIFIED -> width = calculateWidth()

            MeasureSpec.AT_MOST -> {
                width = calculateWidth()
                if (width > widthSize)
                    width = widthSize
            }
            MeasureSpec.EXACTLY -> width = widthSize

            else -> width = widthSize
        }


        when (heightMode) {

            MeasureSpec.UNSPECIFIED -> height = calculateHeight()

            MeasureSpec.AT_MOST -> {
                height = calculateHeight()
                if (height > heightSize)
                    height = heightSize
            }
            MeasureSpec.EXACTLY -> height = heightSize

            else -> height = heightSize
        }

        this.setMeasuredDimension(width, height)
    }

    private fun calculateWidth(): Int {
        val totalWidth =
            getTextWidths().coerceAtLeast(circleRadius * 2) + (stateList!!.size - 1) * minTextGap
        return (ViewCompat.getPaddingStart(this)
                + totalWidth
                + ViewCompat.getPaddingEnd(this))
    }

    private fun getTextWidths(): Int {
        var totalWidth = 0

        for (status in stateList!!)
            totalWidth += status.getTextWidth()

        return totalWidth
    }

    private fun calculateHeight(): Int {
        return (circleRadius * 2
                + calculateMaxHeightOfStatusTexts()
                + textMarginBottom
                + paddingTop
                + paddingBottom)
    }

    private fun calculateMaxHeightOfStatusTexts(): Int {
        val bounds = Rect()
        var maxHeight = 0
        for (state in stateList!!) {
            val height = getAllTextHeights(
                bounds,
                state.getStatus().split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            )
            if (height > maxHeight)
                maxHeight = height
        }
        return maxHeight
    }

    private fun isLtr(): Boolean {
        return ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_LTR
    }

    override fun onDraw(canvas: Canvas) {
        if (isLtr())
            onDrawLtr(canvas)
        else
            onDrawRtl(canvas)

    }

    private fun onDrawLtr(canvas: Canvas) {

        val maxHeight = calculateMaxHeightOfStatusTexts()

        var startX = ViewCompat.getPaddingStart(this)

        val startY = Math.abs(paddingTop - paddingBottom)

        val availableWidth = width - ViewCompat.getPaddingEnd(this) - startX

        val bounds = Rect()

        var textGap = (availableWidth - getTextWidths()) / (stateList!!.size - 1)

        if (textGap < minTextGap)
            textGap = minTextGap


        for (i in stateList!!.indices) {

            val status = stateList!![i]

            statePaint!!.color = getColor(status.getState().color)

            val texts = status.getStatus().split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()

            var centerY = startY + (maxHeight - getAllTextHeights(bounds, texts)) / 2

            var centerX: Int

            for (currentText in texts) {
                val height = getTextHeight(bounds, currentText)
                centerX = startX + (status.getTextWidth() - getTextWidth(bounds, currentText)) / 2
                canvas.drawText(
                    currentText,
                    centerX.toFloat(),
                    (centerY + height).toFloat(),
                    statePaint!!
                )
                centerY += height + lineSpace
            }


            val cx = startX + status.getTextWidth() / 2
            val cy = startY + maxHeight + circleRadius + textMarginBottom


            canvas.drawCircle(cx.toFloat(), cy.toFloat(), circleRadius.toFloat(), statePaint!!)

            if (i + 1 < stateList!!.size) {

                this.linePaint!!.color = getColor(stateList!![i + 1].getState().color)

                val nextTextMiddlePixel =
                    status.getTextWidth() / 2 + textGap + stateList!![i + 1].getTextWidth() / 2

                val stopXLine = cx + nextTextMiddlePixel - circleRadius

                canvas.drawLine(
                    (cx + circleRadius).toFloat(),
                    cy.toFloat(),
                    stopXLine.toFloat(),
                    cy.toFloat(),
                    linePaint!!
                )
            }

            startX += status.getTextWidth() + textGap
        }
    }

    private fun onDrawRtl(canvas: Canvas) {

        val maxHeight = calculateMaxHeightOfStatusTexts()

        var startX = width - ViewCompat.getPaddingEnd(this)

        val startY = Math.abs(paddingTop - paddingBottom)

        val availableWidth = startX - ViewCompat.getPaddingStart(this)

        val bounds = Rect()

        var textGap = (availableWidth - getTextWidths()) / (stateList!!.size - 1)

        if (textGap < minTextGap)
            textGap = minTextGap


        for (i in stateList!!.indices) {

            val status = stateList!![i]

            statePaint!!.color = getColor(status.getState().color)

            val texts = status.getStatus().split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()

            var centerY = startY + (maxHeight - getAllTextHeights(bounds, texts)) / 2

            var centerX: Int

            for (currentText in texts) {
                val height = getTextHeight(bounds, currentText)
                val width = getTextWidth(bounds, currentText)
                centerX = startX - width - (status.getTextWidth() - width) / 2
                canvas.drawText(
                    currentText,
                    centerX.toFloat(),
                    (centerY + height).toFloat(),
                    statePaint!!
                )
                centerY += height + lineSpace
            }


            val cx = startX - status.getTextWidth() / 2
            val cy = startY + maxHeight + circleRadius + textMarginBottom

            canvas.drawCircle(cx.toFloat(), cy.toFloat(), circleRadius.toFloat(), statePaint!!)

            if (i + 1 < stateList!!.size) {

                linePaint!!.color = getColor(stateList!![i + 1].getState().color)

                val nextTextMiddlePixel =
                    status.getTextWidth() / 2 + textGap + stateList!![i + 1].getTextWidth() / 2

                val stopXLine = cx - nextTextMiddlePixel + circleRadius

                canvas.drawLine(
                    (cx - circleRadius).toFloat(),
                    cy.toFloat(),
                    stopXLine.toFloat(),
                    cy.toFloat(),
                    linePaint!!
                )
            }

            startX = startX - status.getTextWidth() - textGap
        }
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    private fun spToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    private fun getColor(@ColorRes res: Int): Int {
        return ContextCompat.getColor(context, res)
    }
}