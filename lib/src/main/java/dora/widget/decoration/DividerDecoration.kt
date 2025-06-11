package dora.widget.decoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView

class DividerDecoration(
    context: Context,
    private val orientation: Int = RecyclerView.VERTICAL,
    private val thickness: Int = 1,
    @ColorInt private val color: Int = 0xFFDDDDDD.toInt()
    ) : RecyclerView.ItemDecoration() {
        private val paint = Paint().apply {
            this.color = this@DividerDecoration.color
            style = Paint.Style.FILL
        }

        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            if (orientation == RecyclerView.VERTICAL) {
                val left = parent.paddingLeft.toFloat()
                val right = (parent.width - parent.paddingRight).toFloat()
                for (i in 0 until parent.childCount - 1) {
                    val child = parent.getChildAt(i)
                    val params = child.layoutParams as RecyclerView.LayoutParams
                    val y = child.bottom + params.bottomMargin
                    c.drawRect(left, y.toFloat(), right, (y + thickness).toFloat(), paint)
                }
            } else {
                val top = parent.paddingTop.toFloat()
                val bottom = (parent.height - parent.paddingBottom).toFloat()
                for (i in 0 until parent.childCount - 1) {
                    val child = parent.getChildAt(i)
                    val params = child.layoutParams as RecyclerView.LayoutParams
                    val x = child.right + params.rightMargin
                    c.drawRect(x.toFloat(), top, (x + thickness).toFloat(), bottom, paint)
                }
            }
        }

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            if (orientation == RecyclerView.VERTICAL) outRect.set(0, 0, 0, thickness)
            else outRect.set(0, 0, thickness, 0)
        }
    }