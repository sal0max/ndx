package de.salomax.ndx.widget

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics

/**
 * Adds a vertical line to the center of the [RecyclerView] (use horizontally).
 */
@Suppress("unused")
class CenterLineDecoration(color: Int, width: Float) : RecyclerView.ItemDecoration() {

    private val mPaint: Paint = Paint()
    private val metrics = Resources.getSystem().displayMetrics

    constructor(color: Int) : this(color, 1.5f)
    constructor() : this(Color.LTGRAY)

    init {
        mPaint.color = color
        mPaint.strokeWidth = convertDpToPixel(width)
        mPaint.style = Paint.Style.FILL
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        super.onDrawOver(canvas, parent, state)
        canvas.save()

        val center = parent.measuredWidth / 2f
        val bottom = parent.measuredHeight.toFloat()

        // center line
        canvas.drawLine(
                center, convertDpToPixel(0f),
                center, bottom - convertDpToPixel(0f),
                mPaint)

        // triangle
//        val path = Path()
//        path.fillType = Path.FillType.EVEN_ODD
//        path.moveTo(center - convertDpToPixel(5f), bottom)
//        path.lineTo(center, bottom - convertDpToPixel(5f))
//        path.lineTo(center + convertDpToPixel(5f), bottom)
//        path.lineTo(center - convertDpToPixel(5f), bottom)
//        path.close()
//        canvas.drawPath(path, mPaint)

        canvas.restore()
    }

    private fun convertDpToPixel(dp: Float): Float {
        return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}
