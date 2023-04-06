package com.rskopyl.dentalab.ui.home

import android.content.Context
import androidx.recyclerview.widget.LinearSmoothScroller

class CenterSmoothScroller(
    context: Context
) : LinearSmoothScroller(context) {

    override fun calculateDtToFit(
        viewStart: Int, viewEnd: Int,
        boxStart: Int, boxEnd: Int,
        snapPreference: Int
    ): Int {
        val viewCenter = (viewStart + viewEnd) / 2
        val boxCenter = (boxStart + boxEnd) / 2
        return boxCenter - viewCenter
    }
}