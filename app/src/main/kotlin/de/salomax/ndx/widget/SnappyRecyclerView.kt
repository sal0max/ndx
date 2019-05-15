package de.salomax.ndx.widget

import android.content.Context
import android.graphics.Rect
import android.os.Parcelable
import androidx.core.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView

class SnappyRecyclerView : RecyclerView {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    val snapped = MutableLiveData<Int>()
    private val snapHelper = MyLinearSnapHelper()
    private fun getSnappedPosition() = snapHelper.getSnappedPosition()

    init {
        this.layoutManager = MyLayoutManager()
        this.snapHelper.attachToRecyclerView(this)
    }

    // add ItemDecoration here, as it needs the width of the view, which is available here
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        addItemDecoration(MyItemDecoration())
    }

    // store currently selected item
    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putInt("snappedPosition", getSnappedPosition())
        bundle.putParcelable("superState", super.onSaveInstanceState())
        return bundle
    }

    // restore selected item
    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            post {
                scrollToPosition(state.getInt("snappedPosition"))
            }
            super.onRestoreInstanceState(state.getParcelable<Parcelable>("superState"))
        } else
            super.onRestoreInstanceState(state)
    }

    @Suppress("unused")
    fun snap() {
        snapHelper.findSnapView(layoutManager!!)?.let {
            snapHelper.calculateDistanceToFinalSnap(layoutManager!!, it)?.let { distance ->
                scrollBy(distance[0], 0)
            }
        }
    }

    /**
     * A horizontal layout manager that supports scrollToPosition for this SnappyRecyclerView
     */
    inner class MyLayoutManager : LinearLayoutManager(context) {

        init {
            orientation = HORIZONTAL
            reverseLayout = false
        }

        override fun scrollToPosition(position: Int) {
            val center = width / 2

            val view = this.findViewByPosition(position)
            view?.let {
                scrollToPositionWithOffset(position, center - it.width / 2)
            }
        }
    }

    /**
     * Custom SnapHelper, as the regular one can't snap to the first and last item, when offsets are
     * used
     */
    inner class MyLinearSnapHelper : LinearSnapHelper() {

        /**
         * add a listener to get notified when a filter is snapped
         */
        override fun attachToRecyclerView(recyclerView: RecyclerView?) {
            var viewCache: View? = null
            recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val view = findSnapView(layoutManager!!)
                    if (viewCache != view) {
                        viewCache = view
                        snapped.value = layoutManager!!.getPosition(view!!)
                    }
                }
            })
            super.attachToRecyclerView(recyclerView)
        }

        /**
         * correctly calculate the center of each filter, including first and last ones
         */
        override fun findSnapView(layoutManager: LayoutManager): View? {
            val helper = OrientationHelper.createHorizontalHelper(layoutManager)

            // those are only the visible children!
            val totalChildren = layoutManager.childCount
            if (totalChildren == 0)
                return null
            // center position of parent
            val center = if (layoutManager.clipToPadding) {
                helper.startAfterPadding + helper.totalSpace / 2
            } else {
                helper.end / 2
            }
            // var for child closest to the center of display
            var closestChild: View? = null

            var absClosest = Integer.MAX_VALUE
            for (i in 0 until totalChildren) {
                val child = layoutManager.getChildAt(i)
                // if child center is closer than previous closest, set it as closest
                val childCenter = helper.getTransformedStartWithDecoration(child) + child!!.width / 2
                val distToCenter = Math.abs(childCenter - center)
                if (distToCenter < absClosest) {
                    absClosest = distToCenter
                    closestChild = child
                }
            }
            return closestChild
        }

        fun getSnappedPosition(): Int {
            val view = findSnapView(layoutManager!!)
            return if (view != null) layoutManager!!.getPosition(view) else -1
        }
    }

    /**
     * Add offset to the beginning and end of list, so that also the first and last items are
     * centered. Works with margins instead of outRect, as the LinearSnapHelper has problems with
     * outRect.
     */
    inner class MyItemDecoration : RecyclerView.ItemDecoration() {

        private var parentWidth = width

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
            super.getItemOffsets(outRect, view, parent, state)

            // only add offset BEFORE FIRST and AFTER LAST item
            val position = parent.getChildAdapterPosition(view)
            val total = parent.adapter!!.itemCount
            if (position == 0 || position == total - 1) {
                // item width
                view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
                val viewWidth = view.measuredWidth
                // calculate offset
                val offset = (parentWidth - viewWidth) / 2
                //determine if rtl
                val ltr = ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_LTR
                // add offset
                if ((position == 0 && ltr) || (position == total - 1 && !ltr))
                    (view.layoutParams as MarginLayoutParams).leftMargin = offset //outRect.left = offset
                else if ((position == 0 && !ltr) || (position == total - 1 && ltr))
                    (view.layoutParams as MarginLayoutParams).rightMargin = offset //outRect.right = offset
            } else {
                (view.layoutParams as MarginLayoutParams).leftMargin = 0
                (view.layoutParams as MarginLayoutParams).rightMargin = 0
            }
        }
    }

}
