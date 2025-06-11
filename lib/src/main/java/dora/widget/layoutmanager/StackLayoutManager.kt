package dora.widget.layoutmanager

import androidx.recyclerview.widget.RecyclerView
import kotlin.math.min

class StackLayoutManager(private val offsetStep: Int = 30) : RecyclerView.LayoutManager() {

    override fun generateDefaultLayoutParams() =
        RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        detachAndScrapAttachedViews(recycler)
        val count = min(itemCount, 3)
        for (i in 0 until count) {
            val view = recycler.getViewForPosition(i)
            addView(view)
            measureChildWithMargins(view, 0, 0)
            val w = getDecoratedMeasuredWidth(view)
            val h = getDecoratedMeasuredHeight(view)
            val left = (width - w) / 2
            val top = (height - h) / 2 + i * offsetStep
            view.alpha = 1 - i * 0.2f
            layoutDecorated(view, left, top, left + w, top + h)
        }
    }

    override fun canScrollVertically() = false
}