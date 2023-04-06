package com.rskopyl.dentalab.ui.formula

import android.graphics.Point
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.min

class FormulaLayoutManager : RecyclerView.LayoutManager() {

    override fun generateDefaultLayoutParams() =
        RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.MATCH_PARENT
        )

    override fun onLayoutChildren(
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ) {
        if (recycler == null) return
        detachAndScrapAttachedViews(recycler)
        if (itemCount == 0) return

        val relativeQuarterDimensions = calculateClosestQuarterDimensions()
        val itemSize = calculateItemSize(relativeQuarterDimensions)
        val point = calculateStartPoint(relativeQuarterDimensions, itemSize)

        val topPositions = relativeQuarterDimensions.run {
            val topLeftPositions = (itemCount - horizontal) until itemCount
            val topRightPositions = 0 until (horizontal - 1)
            topLeftPositions + topRightPositions
        }
        val rightPositions = relativeQuarterDimensions.run {
            val start = topPositions.last() + 1
            start until (start + vertical * 2 - 1)
        }
        val bottomPositions = relativeQuarterDimensions.run {
            val start = rightPositions.last() + 1
            start until (start + horizontal * 2 - 1)
        }
        val leftPositions = relativeQuarterDimensions.run {
            val start = bottomPositions.last() + 1
            start until topPositions.first()
        }

        for (position in 0 until itemCount) {
            val view = recycler.getViewForPosition(position)
            addView(view)
            view.layoutParams.run {
                width = itemSize
                height = itemSize
            }
            measureChild(view, 0, 0)
            layoutDecorated(
                view,
                point.x,
                point.y,
                point.x + view.measuredWidth,
                point.y + view.measuredHeight
            )
            when (position) {
                in topPositions -> point.x += itemSize
                in rightPositions -> point.y += itemSize
                in bottomPositions -> point.x -= itemSize
                in leftPositions -> point.y -= itemSize
            }
        }
    }

    private fun calculateClosestQuarterDimensions(): RelativeDimensions {
        require(itemCount % 4 == 0) {
            "Formula must consist of 4 equal quarters"
        }
        val absoluteProportion = width.toFloat() / height.toFloat()
        val quarterRelativeDimensionsSum = (itemCount / 4)
            .plus(1) // Corner item is overlapped
        val quarterDimensionsVariants = (1 until quarterRelativeDimensionsSum)
            .map { width ->
                RelativeDimensions(
                    horizontal = width,
                    vertical = quarterRelativeDimensionsSum - width
                )
            }
        return quarterDimensionsVariants.reduce { closest, variant ->
            val error = abs(variant.proportion - absoluteProportion)
            val closestError = abs(closest.proportion - absoluteProportion)
            if (error < closestError) variant
            else closest
        }
    }

    private fun calculateItemSize(
        quarterRelativeDimensions: RelativeDimensions
    ): Int {
        val relativeDimensions = quarterRelativeDimensions * 2
        return min(
            width / relativeDimensions.horizontal,
            height / relativeDimensions.vertical
        )
    }

    private fun calculateStartPoint(
        quarterRelativeDimensions: RelativeDimensions,
        itemSize: Int
    ): Point {
        val quarterUsedHeight = itemSize *
                quarterRelativeDimensions.vertical
        return Point(
            width / 2,
            height / 2 - quarterUsedHeight
        )
    }

    data class RelativeDimensions(
        val horizontal: Int,
        val vertical: Int
    ) {
        val proportion: Float
            get() = horizontal.toFloat() / vertical.toFloat()

        operator fun times(value: Int) = RelativeDimensions(
            horizontal = horizontal * value,
            vertical = vertical * value
        )
    }
}