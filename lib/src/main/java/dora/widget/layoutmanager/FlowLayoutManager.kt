package dora.widget.layoutmanager

import androidx.recyclerview.widget.RecyclerView

class FlowLayoutManager(private val itemSpacing: Int = 0, private val lineSpacing: Int = 0) : RecyclerView.LayoutManager() {

    override fun generateDefaultLayoutParams() =
        RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        detachAndScrapAttachedViews(recycler)
        var x = paddingLeft
        var y = paddingTop
        var lineHeight = 0
        val widthLimit = width - paddingRight
        for (i in 0 until itemCount) {
            val view = recycler.getViewForPosition(i)
            addView(view)
            measureChildWithMargins(view, 0, 0)
            val w = getDecoratedMeasuredWidth(view)
            val h = getDecoratedMeasuredHeight(view)
            if (x + w > widthLimit) {
                x = paddingLeft
                y += lineHeight + lineSpacing
                lineHeight = 0
            }
            layoutDecorated(view, x, y, x + w, y + h)
            x += w + itemSpacing
            lineHeight = maxOf(lineHeight, h)
        }
    }

    override fun canScrollVertically() = true

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        offsetChildrenVertical(-dy)
        return dy
    }
}