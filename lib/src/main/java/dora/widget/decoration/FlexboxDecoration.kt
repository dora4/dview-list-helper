package dora.widget.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class FlexboxDecoration(
        private val horizontalSpacing: Int,
        private val verticalSpacing: Int
    ) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.set(
                horizontalSpacing / 2,
                verticalSpacing / 2,
                horizontalSpacing / 2,
                verticalSpacing / 2
            )
        }
    }