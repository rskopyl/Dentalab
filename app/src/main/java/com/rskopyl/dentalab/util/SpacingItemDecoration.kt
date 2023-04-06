package com.rskopyl.dentalab.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SpacingItemDecoration(
    spacingSizePx: Int
) : RecyclerView.ItemDecoration() {

    private val spacingHalfSizePx = spacingSizePx / 2

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val layoutManager = parent.layoutManager ?: return
        val adapter = parent.adapter ?: return

        val position = parent.getChildAdapterPosition(view)
        layoutManager.run {
            if ((position >= spanCount && !reverseLayout) ||
                (position < adapter.itemCount - spanCount && reverseLayout)
            ) {
                outRect.top = spacingHalfSizePx
            }
            if (position % spanCount != 0) {
                outRect.left = spacingHalfSizePx
            }
            if ((position + 1) % spanCount != 0) {
                outRect.right = spacingHalfSizePx
            }
            if ((position < adapter.itemCount - spanCount && !reverseLayout) ||
                (position >= spanCount && reverseLayout)
            ) {
                outRect.bottom = spacingHalfSizePx
            }
            if (orientation == LinearLayoutManager.HORIZONTAL) {
                outRect.apply {
                    val temp = top
                    top = right
                    right = bottom
                    bottom = left
                    left = temp
                }
            }
        }
    }
}

private val RecyclerView.LayoutManager.reverseLayout: Boolean
    get() = when (this) {
        is LinearLayoutManager -> reverseLayout
        else -> throw UnsupportedOperationException()
    }

private val RecyclerView.LayoutManager.orientation: Int
    get() = when (this) {
        is LinearLayoutManager -> orientation
        else -> throw UnsupportedOperationException()
    }

private val RecyclerView.LayoutManager.spanCount: Int
    get() = when (this) {
        is GridLayoutManager -> spanCount
        is LinearLayoutManager -> 1
        else -> throw IllegalArgumentException()
    }