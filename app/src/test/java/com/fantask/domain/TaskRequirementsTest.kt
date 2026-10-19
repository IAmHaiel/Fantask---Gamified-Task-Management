package com.fantask.domain

import com.fantask.data.model.TaskEntity
import com.fantask.data.model.TaskRequirement
import com.fantask.data.model.areAllRequirementsDone
import com.fantask.data.model.getRequirements
import com.fantask.data.model.withRequirements
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskRequirementsTest {

    @Test
    fun testEmptyRequirements_isDoneTrue() {
        val task = TaskEntity(name = "Clean Room")
        assertTrue(task.areAllRequirementsDone())
        assertTrue(task.getRequirements().isEmpty())
    }

    @Test
    fun testUnfinishedRequirements_isDoneFalse() {
        val reqs = listOf(
            TaskRequirement(text = "Pick up clothes", isDone = true),
            TaskRequirement(text = "Vacuum floor", isDone = false)
        )
        val task = TaskEntity(name = "Clean Room").withRequirements(reqs)

        assertEquals(2, task.getRequirements().size)
        assertFalse(task.areAllRequirementsDone())
    }

    @Test
    fun testAllFinishedRequirements_isDoneTrue() {
        val reqs = listOf(
            TaskRequirement(text = "Pick up clothes", isDone = true),
            TaskRequirement(text = "Vacuum floor", isDone = true)
        )
        val task = TaskEntity(name = "Clean Room").withRequirements(reqs)

        assertEquals(2, task.getRequirements().size)
        assertTrue(task.areAllRequirementsDone())
    }
}
