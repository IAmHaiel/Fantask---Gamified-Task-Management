package com.fantask.ui.kanban

import com.fantask.data.model.TaskStatus
import kotlin.math.abs

/**
 * Pure mathematical geometry and collision detection helpers for the Kanban Board.
 * Independent of Compose UI views for deterministic unit testing.
 */
object KanbanGeometry {

    /**
     * Determines which Kanban column the user's finger is currently hovering over.
     *
     * @param fingerXInContainer The X position of the finger relative to the scroll container's left edge.
     * @param scrollOffset The current horizontal scroll position in pixels.
     * @param columnWidthPx The width of a single column in pixels.
     * @param columnSpacingPx The horizontal gap between adjacent columns in pixels.
     * @param boardPaddingStartPx The starting padding of the horizontal board in pixels.
     * @param statuses The ordered list of statuses displayed in the board.
     * @return The [TaskStatus] of the column directly under or closest to the finger, or null if empty.
     */
    fun calculateHoveredStatus(
        fingerXInContainer: Float,
        scrollOffset: Int,
        columnWidthPx: Float,
        columnSpacingPx: Float,
        boardPaddingStartPx: Float,
        statuses: List<TaskStatus>
    ): TaskStatus? {
        if (statuses.isEmpty() || columnWidthPx <= 0f) return null

        val contentX = fingerXInContainer + scrollOffset - boardPaddingStartPx

        // If dragged to the left before the board padding, snap to first column
        if (contentX <= 0f) return statuses.first()

        val stride = columnWidthPx + columnSpacingPx
        val approxIndex = (contentX / stride).toInt()

        // Check if finger is within the column width at approxIndex
        if (approxIndex in statuses.indices) {
            val colStart = approxIndex * stride
            val colEnd = colStart + columnWidthPx
            if (contentX in colStart..colEnd) {
                return statuses[approxIndex]
            }
        }

        // If in the gap between columns or beyond, pick nearest column center
        return statuses.minByOrNull { status ->
            val index = statuses.indexOf(status)
            val colCenter = index * stride + columnWidthPx / 2f
            abs(contentX - colCenter)
        } ?: statuses.first()
    }

    /**
     * Computes the horizontal scroll delta per frame when dragging near screen edges.
     *
     * @param fingerXInContainer The X position of the finger inside the container.
     * @param containerWidthPx The total visible width of the container.
     * @param edgeZonePx The width of the edge detection zone in pixels (e.g., ~70dp).
     * @param maxSpeedPx The maximum scroll pixels per frame.
     * @return Positive delta to scroll right, negative delta to scroll left, or 0f if outside edge zone.
     */
    fun calculateEdgeScrollDelta(
        fingerXInContainer: Float,
        containerWidthPx: Float,
        edgeZonePx: Float,
        maxSpeedPx: Float
    ): Float {
        if (containerWidthPx <= 0f || edgeZonePx <= 0f || maxSpeedPx <= 0f) return 0f

        return when {
            fingerXInContainer > containerWidthPx - edgeZonePx -> {
                val excess = (fingerXInContainer - (containerWidthPx - edgeZonePx)).coerceAtLeast(0f)
                val factor = (excess / edgeZonePx).coerceIn(0.15f, 1.5f)
                maxSpeedPx * factor
            }
            fingerXInContainer < edgeZonePx -> {
                val excess = (edgeZonePx - fingerXInContainer).coerceAtLeast(0f)
                val factor = (excess / edgeZonePx).coerceIn(0.15f, 1.5f)
                -maxSpeedPx * factor
            }
            else -> 0f
        }
    }
}
