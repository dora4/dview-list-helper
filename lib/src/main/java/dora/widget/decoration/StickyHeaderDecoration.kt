package dora.widget.decoration;

import android.content.Context
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class StickyHeaderDecoration<VH : RecyclerView.ViewHolder>(
    private val context: Context,
    private val adapter: RecyclerView.Adapter<VH>,
    private val isSticky: Boolean,
    private val isHeader: (position: Int) -> Boolean
) : RecyclerView.ItemDecoration() {

    private var headerView: View? = null
    private var headerPosition = -1
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val topChild = parent.getChildAt(0) ?: return
        val topPos = parent.getChildAdapterPosition(topChild)
        val newHeaderPos = findHeaderPosition(topPos)
        if (newHeaderPos < 0) return
        if (newHeaderPos != headerPosition || headerView == null) {
            headerPosition = newHeaderPos
            headerView = createHeaderView(parent, newHeaderPos)
            fixLayoutSize(parent, headerView!!)
        }
        val contactPoint = headerView!!.height
        val nextChild = getNextHeaderView(parent, topPos)
        var offsetY = 0
        if (nextChild != null) {
            val nextPos = parent.getChildAdapterPosition(nextChild)
            if (isHeader(nextPos) && nextChild.top < contactPoint) {
                offsetY = nextChild.top - contactPoint
            }
        }
        c.save()
        val translateY = if (isSticky) offsetY.toFloat() else 0f
        c.translate(0f, translateY)
        headerView!!.draw(c)
        c.restore()
    }

    private fun findHeaderPosition(itemPos: Int): Int {
        for (pos in itemPos downTo 0) if (isHeader(pos)) return pos
        return -1
    }

    private fun getNextHeaderView(parent: RecyclerView, currentPos: Int): View? {
        for (i in 1 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (isHeader(parent.getChildAdapterPosition(child))) return child
        }
        return null
    }

    private fun createHeaderView(parent: RecyclerView, position: Int): View {
        val vh = adapter.createViewHolder(parent, adapter.getItemViewType(position))
        adapter.bindViewHolder(vh, position)
        return vh.itemView
    }

    private fun fixLayoutSize(parent: RecyclerView, view: View) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec =
            View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)
        val childWidth = ViewGroup.getChildMeasureSpec(
            widthSpec,
            parent.paddingLeft + parent.paddingRight,
            view.layoutParams?.width ?: ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val childHeight = ViewGroup.getChildMeasureSpec(
            heightSpec,
            parent.paddingTop + parent.paddingBottom,
            view.layoutParams?.height ?: ViewGroup.LayoutParams.WRAP_CONTENT
        )
        view.measure(childWidth, childHeight)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }
}
