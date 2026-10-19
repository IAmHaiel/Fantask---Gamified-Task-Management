package com.fantask.ui.kanban

import com.fantask.data.model.TaskStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class KanbanGeometryTest {

    private val statuses = listOf(
        TaskStatus.PENDING,
        TaskStatus.TODO,
        TaskStatus.IN_PROGRESS,
        TaskStatus.DONE
    )
    private val columnWidth = 280f
    private val columnSpacing = 20f
    private val paddingStart = 10f

    @Test
    fun testHitTestingColumn0_Pending() {
        val hovered = KanbanGeometry.calculateHoveredStatus(
            fingerXInContainer = 50f,
            scrollOffset = 0,
            columnWidthPx = columnWidth,
            columnSpacingPx = columnSpacing,
            boardPaddingStartPx = paddingStart,
            statuses = statuses
        )
        assertEquals(TaskStatus.PENDING, hovered)
    }

    @Test
    fun testHitTestingColumn1_Todo() {
        // Column 1 starts at paddingStart (10) + columnWidth (280) + spacing (20) = 310
        val hovered = KanbanGeometry.calculateHoveredStatus(
            fingerXInContainer = 350f,
            scrollOffset = 0,
            columnWidthPx = columnWidth,
            columnSpacingPx = columnSpacing,
            boardPaddingStartPx = paddingStart,
            statuses = statuses
        )
        assertEquals(TaskStatus.TODO, hovered)
    }

    @Test
    fun testHitTestingWithHorizontalScroll() {
        // Scrolled 300px to the right: finger at 100px in container corresponds to contentX = 100 + 300 - 10 = 390
        // 390 is within Column 1 (310 to 590)
        val hovered = KanbanGeometry.calculateHoveredStatus(
            fingerXInContainer = 100f,
            scrollOffset = 300,
            columnWidthPx = columnWidth,
            columnSpacingPx = columnSpacing,
            boardPaddingStartPx = paddingStart,
            statuses = statuses
        )
        assertEquals(TaskStatus.TODO, hovered)
    }

    @Test
    fun testHitTestingColumn3_Done() {
        // Column 3 starts at 10 + 3 * 300 = 910
        val hovered = KanbanGeometry.calculateHoveredStatus(
            fingerXInContainer = 950f,
            scrollOffset = 0,
            columnWidthPx = columnWidth,
            columnSpacingPx = columnSpacing,
            boardPaddingStartPx = paddingStart,
            statuses = statuses
        )
        assertEquals(TaskStatus.DONE, hovered)
    }

    @Test
    fun testHitTestingPastFarLeftSnapsToFirstColumn() {
        val hovered = KanbanGeometry.calculateHoveredStatus(
            fingerXInContainer = -20f,
            scrollOffset = 0,
            columnWidthPx = columnWidth,
            columnSpacingPx = columnSpacing,
            boardPaddingStartPx = paddingStart,
            statuses = statuses
        )
        assertEquals(TaskStatus.PENDING, hovered)
    }

    @Test
    fun testEdgeAutoScrollDelta_CenterZoneIsZero() {
        val delta = KanbanGeometry.calculateEdgeScrollDelta(
            fingerXInContainer = 500f,
            containerWidthPx = 1000f,
            edgeZonePx = 100f,
            maxSpeedPx = 20f
        )
        assertEquals(0f, delta, 0.001f)
    }

    @Test
    fun testEdgeAutoScrollDelta_RightEdgeIsPositive() {
        val delta = KanbanGeometry.calculateEdgeScrollDelta(
            fingerXInContainer = 950f,
            containerWidthPx = 1000f,
            edgeZonePx = 100f,
            maxSpeedPx = 20f
        )
        assertTrue("Delta should be positive when dragging near right edge", delta > 0f)
    }

    @Test
    fun testEdgeAutoScrollDelta_LeftEdgeIsNegative() {
        val delta = KanbanGeometry.calculateEdgeScrollDelta(
            fingerXInContainer = 30f,
            containerWidthPx = 1000f,
            edgeZonePx = 100f,
            maxSpeedPx = 20f
        )
        assertTrue("Delta should be negative when dragging near left edge", delta < 0f)
    }
}
