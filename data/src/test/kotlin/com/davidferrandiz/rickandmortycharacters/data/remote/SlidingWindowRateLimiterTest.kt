package com.davidferrandiz.rickandmortycharacters.data.remote

import org.junit.Assert.assertEquals
import org.junit.Test

class SlidingWindowRateLimiterTest {

    private var now = 0L
    private val limiter = SlidingWindowRateLimiter(maxRequests = 3, windowMillis = 10_000, now = { now })

    @Test
    fun `requests inside the budget are granted straight away`() {
        repeat(3) { assertEquals(0L, limiter.millisUntilGranted()) }
    }

    @Test
    fun `a request over the budget is told how long until the oldest one leaves the window`() {
        repeat(3) { limiter.millisUntilGranted() }
        now = 4_000

        assertEquals(6_000L, limiter.millisUntilGranted())
    }

    @Test
    fun `a denied request does not use up budget`() {
        repeat(3) { limiter.millisUntilGranted() }
        repeat(5) { limiter.millisUntilGranted() }
        now = 10_000

        assertEquals(0L, limiter.millisUntilGranted())
    }

    @Test
    fun `the window slides so budget comes back one request at a time`() {
        limiter.millisUntilGranted()
        now = 5_000
        limiter.millisUntilGranted()
        limiter.millisUntilGranted()
        now = 10_000

        assertEquals(0L, limiter.millisUntilGranted())
        assertEquals(5_000L, limiter.millisUntilGranted())
    }
}
