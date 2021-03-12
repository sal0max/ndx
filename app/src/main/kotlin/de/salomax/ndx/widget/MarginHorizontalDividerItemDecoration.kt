package de.salomax.ndx.widget

import android.content.Context
import android.graphics.Canvas
import androidx.recyclerview.widget.RecyclerView
import android.graphics.drawable.Drawable
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import de.salomax.ndx.R

class MarginHorizontalDividerItemDecoration(context: Context, @DimenRes private val marginLeft: Int, @DimenRes private val marginRight: Int)
    : RecyclerView.ItemDecoration() {

    @Suppress("unused")
    constructor(context: Context) : this(context, 0, 0)

    @Suppress("unused")
    constructor(context: Context, @DimenRes margin: Int) : this(context, margin, margin)

    private var divider: Drawable? = null

    init {
        divider = ContextCompat.getDrawable(context, R.drawable.divider_list)
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft + marginLeft
        val right = parent.width - parent.paddingRight - marginRight

        val childCount = parent.childCount
        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            divider?.setBounds(left, top, right, divider?.intrinsicHeight?.plus(top) ?: 0)
            divider?.draw(c)
        }
    }

}
