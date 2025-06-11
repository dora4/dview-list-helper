package dora.widget.layoutmanager

import androidx.recyclerview.widget.RecyclerView
import kotlin.math.min

class CarouselLayoutManager : RecyclerView.LayoutManager() {

    override fun generateDefaultLayoutParams() =
        RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        detachAndScrapAttachedViews(recycler)
        val mid = width / 2
        for (i in 0 until itemCount) {
            val view = recycler.getViewForPosition(i)
            addView(view)
            measureChildWithMargins(view, 0, 0)
            val w = getDecoratedMeasuredWidth(view)
            val h = getDecoratedMeasuredHeight(view)
            val left = paddingLeft + (i * (w + paddingLeft))
            val top = (height - h) / 2
            val d = (left + w/2) - mid
            val scale = 1 - min(0.5f, d.toFloat() / width)
            view.scaleX = scale
            view.scaleY = scale
            layoutDecorated(view, left, top, left + w, top + h)
        }
    }

    override fun canScrollHorizontally() = true

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        offsetChildrenHorizontal(-dx)
        return dx
    }
}