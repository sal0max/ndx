package de.salomax.ndx.widget

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics

/**
 * Adds little dots between all items (use horizontally).
 */
@Suppress("unused")
class DotDividerDecoration(color: Int, width: Float) : RecyclerView.ItemDecoration() {

    private val mPaint: Paint = Paint()
    private val metrics = Resources.getSystem().displayMetrics

    constructor(color: Int) : this(color, 1.5f)
    constructor() : this(Color.LTGRAY)

    init {
        mPaint.color = color
        mPaint.strokeWidth = convertDpToPixel(width)
        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)
        canvas.save()

        val bottom = parent.measuredHeight.toFloat()
        val layoutManager = parent.layoutManager

        val childCount = parent.childCount
        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)
            val itemCenter = layoutManager!!.getDecoratedRight(child).toFloat()
            canvas.drawCircle(
                    itemCenter, bottom / 2f,
                    convertDpToPixel(1f),
                    mPaint
            )
        }
        canvas.restore()
    }

    private fun convertDpToPixel(dp: Float): Float {
        return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

}
