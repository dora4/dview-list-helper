package dora.widget.layoutmanager

import android.util.SparseArray
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * A Flexbox-like LayoutManager: lays out items in rows, wraps to next line when out of space,
 * supports justifyContent and alignItems.
 */
class FlexboxLayoutManager : RecyclerView.LayoutManager() {

    enum class FlexDirection { ROW, COLUMN }
    enum class FlexWrap { NOWRAP, WRAP }
    enum class JustifyContent { FLEX_START, FLEX_END, CENTER, SPACE_BETWEEN, SPACE_AROUND }
    enum class AlignItems { FLEX_START, FLEX_END, CENTER, STRETCH }

    var flexDirection = FlexDirection.ROW
    var flexWrap = FlexWrap.WRAP
    var justifyContent = JustifyContent.FLEX_START
    var alignItems = AlignItems.FLEX_START

    private val viewLines = SparseArray<Line>()
    private var totalHeight = 0
    private var verticalOffset = 0

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams = RecyclerView.LayoutParams(
        RecyclerView.LayoutParams.WRAP_CONTENT,
        RecyclerView.LayoutParams.WRAP_CONTENT
    )

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        detachAndScrapAttachedViews(recycler)
        viewLines.clear()
        totalHeight = 0
        verticalOffset = 0

        val parentWidth = width - paddingLeft - paddingRight
        var offsetY = paddingTop
        var line = Line(offsetY)
        var usedLineWidth = 0

        for (i in 0 until itemCount) {
            val view = recycler.getViewForPosition(i)
            addView(view)
            measureChildWithMargins(view, 0, 0)
            val w = getDecoratedMeasuredWidth(view)
            val h = getDecoratedMeasuredHeight(view)

            if (flexWrap == FlexWrap.WRAP && usedLineWidth + w > parentWidth && line.items.isNotEmpty()) {
                line.height = line.maxHeight
                viewLines.put(viewLines.size(), line)
                offsetY += line.height
                totalHeight = offsetY
                line = Line(offsetY)
                usedLineWidth = 0
            }
            line.add(view, w, h)
            usedLineWidth += w
        }
        line.height = line.maxHeight
        viewLines.put(viewLines.size(), line)
        totalHeight = offsetY + line.height + paddingBottom

        fillVisibleViews(recycler)
    }

    private fun fillVisibleViews(recycler: RecyclerView.Recycler) {
        val parentWidth = width - paddingLeft - paddingRight
        for (i in 0 until viewLines.size()) {
            val line = viewLines.valueAt(i)
            val usedLineWidth = line.items.sumBy { it.width }
            val extraSpace = parentWidth - usedLineWidth
            val (offsetStart, spaceBetween) = calculateJustifyOffsets(line.items.size, extraSpace)
            var xPos = offsetStart
            for (item in line.items) {
                val view = item.view
                val w = item.width
                val h = item.height
                val top = calculateAlignOffset(item.top, line.height, h)
                layoutDecorated(
                    view,
                    paddingLeft + xPos,
                    top - verticalOffset,
                    paddingLeft + xPos + w,
                    top + h - verticalOffset
                )
                xPos += w + spaceBetween
            }
        }
    }

    private fun calculateJustifyOffsets(count: Int, extraSpace: Int): Pair<Int, Int> {
        return when (justifyContent) {
            JustifyContent.FLEX_START -> Pair(0, 0)
            JustifyContent.FLEX_END -> Pair(extraSpace, 0)
            JustifyContent.CENTER -> Pair(extraSpace / 2, 0)
            JustifyContent.SPACE_BETWEEN ->
                if (count > 1) Pair(0, extraSpace / (count - 1)) else Pair(0, 0)
            JustifyContent.SPACE_AROUND ->
                Pair(extraSpace / (count * 2), (extraSpace / count))
        }
    }

    private fun calculateAlignOffset(itemTop: Int, lineHeight: Int, itemHeight: Int): Int {
        return when (alignItems) {
            AlignItems.FLEX_START -> itemTop
            AlignItems.FLEX_END -> itemTop + (lineHeight - itemHeight)
            AlignItems.CENTER -> itemTop + (lineHeight - itemHeight) / 2
            AlignItems.STRETCH -> itemTop
        }
    }

    override fun canScrollVertically(): Boolean = true

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        val delta = when {
            verticalOffset + dy < 0 -> -verticalOffset
            verticalOffset + dy + height > totalHeight -> totalHeight - height - verticalOffset
            else -> dy
        }
        verticalOffset += delta
        offsetChildrenVertical(-delta)
        return delta
    }

    private data class Line(var top: Int) {
        var height = 0
        var maxHeight = 0
        val items = mutableListOf<LineItem>()
        fun add(view: View, w: Int, h: Int) {
            items.add(LineItem(view, top, w, h))
            if (h > maxHeight) maxHeight = h
        }
    }

    private data class LineItem(
        val view: View,
        val top: Int,
        val width: Int,
        val height: Int
    )
}