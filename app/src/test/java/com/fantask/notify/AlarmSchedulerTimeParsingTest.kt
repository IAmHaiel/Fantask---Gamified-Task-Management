package com.fantask.notify

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class AlarmSchedulerTimeParsingTest {

    @Test
    fun testParse12HourAm() {
        val result = AlarmScheduler.parseTimeString("08:00 AM")
        assertNotNull(result)
        assertEquals(8 to 0, result)
    }

    @Test
    fun testParse12HourPm() {
        val result = AlarmScheduler.parseTimeString("10:30 PM")
        assertNotNull(result)
        assertEquals(22 to 30, result)
    }

    @Test
    fun testParse12HourMidnight() {
        val result = AlarmScheduler.parseTimeString("12:00 AM")
        assertNotNull(result)
        assertEquals(0 to 0, result)
    }

    @Test
    fun testParse12HourNoon() {
        val result = AlarmScheduler.parseTimeString("12:00 PM")
        assertNotNull(result)
        assertEquals(12 to 0, result)
    }

    @Test
    fun testParse24HourFallback() {
        val result = AlarmScheduler.parseTimeString("14:45")
        assertNotNull(result)
        assertEquals(14 to 45, result)
    }
}
