package de.salomax.ndx.widget

import android.content.Context
import android.graphics.Rect
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

@Suppress("unused")
class SnappyRecyclerView : RecyclerView {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    private val snappedSubject = PublishSubject.create<Int>()
    private var subscription: Disposable? = null
    private var snapHelper: MyLinearSnapHelper

    val snappedEvent: Observable<Int> = snappedSubject

    init {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        snapHelper = MyLinearSnapHelper()
        snapHelper.attachToRecyclerView(this)
        addItemDecoration(MyItemDecoration())
    }

    fun getSnappedPosition() = snapHelper.getSnappedPosition()

    /**
     * Custom SnapHelper, as the regular one can't snap to the first and last item, when offsets are used
     */
    inner class MyLinearSnapHelper : LinearSnapHelper() {

        private lateinit var layoutManager: LayoutManager

        /**
         * add a listener to get notified when a filter is snapped
         */
        override fun attachToRecyclerView(recyclerView: RecyclerView?) {
            var viewCache: View? = null
            layoutManager = recyclerView?.layoutManager!!
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    val view = findSnapView(layoutManager)
                    if (viewCache != view) {
                        viewCache = view
                        snappedSubject.onNext(layoutManager.getPosition(view))
                    }
                }
            })
            super.attachToRecyclerView(recyclerView)
        }

        /**
         * correctly calculate the center of each filter, including first and last ones
         */
        override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
            val helper = OrientationHelper.createHorizontalHelper(layoutManager)

            // those are only the visible children!
            val totalChildren = layoutManager.childCount
            if (totalChildren == 0)
                return null
            // center position of display
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
                val childCenter = helper.getTransformedStartWithDecoration(child) + child.width / 2
                val distToCenter = Math.abs(childCenter - center)
                if (distToCenter < absClosest) {
                    absClosest = distToCenter
                    closestChild = child
                }
            }
            return closestChild
        }

        fun getSnappedPosition(): Int {
            val view = super.findSnapView(layoutManager)
            return if (view != null) layoutManager.getPosition(view) else -1
        }
    }

    /**
     * AddOrUpdate offset to the beginning and end of list, so that also the first and last items are centered.
     * Works with margins instead of outRect, as the LinearSnapHelper has problems with outRect.
     */
    inner class MyItemDecoration : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
            super.getItemOffsets(outRect, view, parent, state)

            // only add offset BEFORE FIRST and AFTER LAST item
            val position = parent.getChildAdapterPosition(view)
            val total = parent.adapter.itemCount
            if (position == 0 || position == total - 1) {
                // item width
                view.measure(0, 0)
                val viewWidth = view.measuredWidth
                // container width
                val parentWidth = parent.layoutManager.width
                // calc offset
                val offset = (parentWidth - viewWidth) / 2
                //determine if rtl
                val ltr = ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_LTR
                // add offset
                if ((position == 0 && ltr) || (position == total - 1 && !ltr))
                    (view.layoutParams as ViewGroup.MarginLayoutParams).leftMargin = offset //outRect.left = offset
                else if ((position == 0 && !ltr) || (position == total - 1 && ltr))
                    (view.layoutParams as ViewGroup.MarginLayoutParams).rightMargin = offset //outRect.right = offset
            } else {
                (view.layoutParams as ViewGroup.MarginLayoutParams).leftMargin = 0
                (view.layoutParams as ViewGroup.MarginLayoutParams).rightMargin = 0
            }
        }
    }

}