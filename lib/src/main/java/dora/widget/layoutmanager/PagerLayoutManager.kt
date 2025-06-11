package dora.widget.layoutmanager

import androidx.recyclerview.widget.RecyclerView

class PagerLayoutManager : RecyclerView.LayoutManager() {

    override fun generateDefaultLayoutParams() =
        RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.MATCH_PARENT
        )

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        detachAndScrapAttachedViews(recycler)
        if (itemCount == 0) return
        val view = recycler.getViewForPosition(0)
        addView(view)
        measureChildWithMargins(view, 0, 0)
        layoutDecorated(view, paddingLeft, paddingTop, width - paddingRight, height - paddingBottom)
    }

    override fun canScrollHorizontally() = true

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State) = dx
}