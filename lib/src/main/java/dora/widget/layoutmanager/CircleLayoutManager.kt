package dora.widget.layoutmanager

import androidx.recyclerview.widget.RecyclerView
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class CircleLayoutManager(private val radius: Int) : RecyclerView.LayoutManager() {

    override fun generateDefaultLayoutParams() =
        RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        detachAndScrapAttachedViews(recycler)
        val cx = width / 2
        val cy = height / 2
        val count = itemCount
        for (i in 0 until count) {
            val view = recycler.getViewForPosition(i)
            addView(view)
            measureChildWithMargins(view, 0, 0)
            val w = getDecoratedMeasuredWidth(view)
            val h = getDecoratedMeasuredHeight(view)
            val angle = 2 * PI * i / count
            val x = (cx + radius * cos(angle) - w / 2).toInt()
            val y = (cy + radius * sin(angle) - h / 2).toInt()
            layoutDecorated(view, x, y, x + w, y + h)
        }
    }

    override fun canScrollHorizontally() = false

    override fun canScrollVertically() = false
}
