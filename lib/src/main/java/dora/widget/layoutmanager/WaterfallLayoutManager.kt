package dora.widget.layoutmanager

import androidx.recyclerview.widget.RecyclerView

class WaterfallLayoutManager(
    private val spanCount: Int,
    private val itemSpacing: Int = 0,
    private val lineSpacing: Int = 0
) : RecyclerView.LayoutManager() {

    private val columnHeights = IntArray(spanCount) { paddingTop }

    override fun generateDefaultLayoutParams() =
        RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        detachAndScrapAttachedViews(recycler)
        columnHeights.fill(paddingTop)
        val parentWidth = width - paddingLeft - paddingRight
        val columnWidth = (parentWidth - itemSpacing * (spanCount - 1)) / spanCount

        for (i in 0 until itemCount) {
            val view = recycler.getViewForPosition(i)
            addView(view)
            measureChildWithMargins(view, 0, 0)
            getDecoratedMeasuredWidth(view).let { /* ignore */ }
            val h = getDecoratedMeasuredHeight(view)
            val col = columnHeights.indices.minByOrNull { columnHeights[it] }!!
            val left = paddingLeft + col * (columnWidth + itemSpacing)
            val top = columnHeights[col]
            layoutDecorated(view, left, top, left + columnWidth, top + h)
            columnHeights[col] = top + h + lineSpacing
        }
    }

    override fun canScrollVertically() = true

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        val delta = dy
        offsetChildrenVertical(-delta)
        return delta
    }
}