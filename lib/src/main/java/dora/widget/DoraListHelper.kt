package dora.widget

import android.annotation.SuppressLint
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.*
import dora.widget.decoration.DividerDecoration
import dora.widget.decoration.FlexboxDecoration
import dora.widget.decoration.GridDecoration
import dora.widget.decoration.StickyHeaderDecoration
import dora.widget.layoutmanager.ArcLayoutManager
import dora.widget.layoutmanager.CarouselLayoutManager
import dora.widget.layoutmanager.CircleLayoutManager
import dora.widget.layoutmanager.FlexboxLayoutManager
import dora.widget.layoutmanager.FlowLayoutManager
import dora.widget.layoutmanager.PagerLayoutManager
import dora.widget.layoutmanager.StackLayoutManager
import dora.widget.layoutmanager.WaterfallLayoutManager

/**
 * DoraListHelper: 一站式 RecyclerView 列表工具组合。
 * 支持线性装饰 / 网格装饰 / 弹性布局 + 装饰 / 瀑布流 / 环形 / 跑马灯 / 分页 / 堆叠 / 弧形 / DiffUtil。
 */
object DoraListHelper {

    @JvmStatic
    fun <T, VH : RecyclerView.ViewHolder> attach(
        recyclerView: RecyclerView,
        listAdapter: ListAdapter<T, VH>,
        block: Builder<T, VH>.() -> Unit
    ): ListController<T, VH> {
        val builder = Builder(recyclerView, listAdapter)
        builder.block()
        return builder.build()
    }

    class Builder<T, VH : RecyclerView.ViewHolder>(
        private val recyclerView: RecyclerView,
        private val listAdapter: ListAdapter<T, VH>
    ) {
        private var layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(recyclerView.context)
        private val decorations = mutableListOf<RecyclerView.ItemDecoration>()

        /**
         * 线性布局，默认垂直。
         */
        fun linear(vertical: Boolean = true) = apply {
            layoutManager = LinearLayoutManager(
                recyclerView.context,
                if (vertical) RecyclerView.VERTICAL else RecyclerView.HORIZONTAL,
                false
            )
        }

        /**
         * 添加分割线装饰。
         */
        fun divider(
            thickness: Int = 1,
            @ColorInt color: Int = 0xFFDDDDDD.toInt()
        ) = apply {
            decorations.add(
                DividerDecoration(
                    recyclerView.context,
                    (layoutManager as? LinearLayoutManager)?.orientation
                        ?: RecyclerView.VERTICAL,
                    thickness,
                    color
                )
            )
        }

        /**
         * 网格布局 + 间距装饰。
         */
        fun grid(
            spanCount: Int,
            spacing: Int = 0,
            includeEdge: Boolean = false
        ) = apply {
            layoutManager = GridLayoutManager(recyclerView.context, spanCount)
            decorations.add(GridDecoration(spanCount, spacing, includeEdge))
        }

        /**
         * 弹性布局（Flexbox） + 间距装饰。
         */
        fun flex(
            horizontalSpacing: Int = 0,
            verticalSpacing: Int = 0
        ) = apply {
            layoutManager = FlexboxLayoutManager().apply {
                flexDirection = FlexboxLayoutManager.FlexDirection.ROW
                justifyContent = FlexboxLayoutManager.JustifyContent.FLEX_START
            }
            decorations.add(FlexboxDecoration(horizontalSpacing, verticalSpacing))
        }

        /**
         * 瀑布流布局（Masonry） + 间距装饰。
         */
        fun waterfall(
            spanCount: Int,
            spacing: Int = 0,
            lineSpacing: Int = 0
        ) = apply {
            layoutManager = WaterfallLayoutManager(spanCount, spacing, lineSpacing)
        }

        /**
         * 环形布局。
         */
        fun circle(radius: Int) = apply {
            layoutManager = CircleLayoutManager(radius)
        }

        /**
         * 走马灯/封面流布局。
         */
        fun carousel() = apply {
            layoutManager = CarouselLayoutManager()
        }

        /**
         * 分页布局。
         */
        fun pager() = apply {
            layoutManager = PagerLayoutManager()
        }

        /**
         * 堆叠卡片布局。
         */
        fun stack(offsetStep: Int = 30) = apply {
            layoutManager = StackLayoutManager(offsetStep)
        }

        /**
         * 弧形布局。
         */
        fun arc(arcDegrees: Float = 180f) = apply {
            layoutManager = ArcLayoutManager(arcDegrees)
        }

        /**
         * 流式换行布局。
         */
        fun flow(
            itemSpacing: Int = 0,
            lineSpacing: Int = 0
        ) = apply {
            layoutManager = FlowLayoutManager(itemSpacing, lineSpacing)
        }

        /**
         * 粘性头部装饰，isHeader 判断哪些位置为 Header。
         */
        fun stickyHeader(
            isSticky: Boolean = true,
            isHeader: (position: Int) -> Boolean
        ) = apply {
            decorations.add(
                StickyHeaderDecoration(
                    recyclerView.context,
                    listAdapter,
                    isSticky,
                    isHeader
                )
            )
        }

        fun build(): ListController<T, VH> {
            recyclerView.layoutManager = layoutManager
            decorations.forEach { recyclerView.addItemDecoration(it) }
            recyclerView.adapter = listAdapter
            return ListController(recyclerView, listAdapter)
        }
    }

    class ListController<T, VH : RecyclerView.ViewHolder>(
        private val recyclerView: RecyclerView,
        private val adapter: ListAdapter<T, VH>
    ) {
        fun submitList(list: List<T>) = adapter.submitList(list)
    }

    /**
     * DiffUtil 回调，适用于模型实现 DiffItem 接口或拥有 id 字段。
     */
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DiffItem>() {
        override fun areItemsTheSame(old: DiffItem, new: DiffItem): Boolean {
            return old.id == new.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(old: DiffItem, new: DiffItem): Boolean {
            return old == new
        }
    }
}
