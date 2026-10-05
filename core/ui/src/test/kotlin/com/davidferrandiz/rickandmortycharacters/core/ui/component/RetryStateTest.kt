package com.davidferrandiz.rickandmortycharacters.core.ui.component

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RetryStateTest {

    private fun TestScope.retrying(): RetryState {
        val state = RetryState(maxRetries = 3, baseDelayMillis = 2_000)
        backgroundScope.launch { state.retryFailures() }
        return state
    }

    @Test
    fun `it is waiting until the first result arrives`() = runTest {
        val state = retrying()

        assertTrue(state.isWaiting)

        state.onSuccess()

        assertFalse(state.isWaiting)
    }

    @Test
    fun `each failure schedules a retry with a longer wait`() = runTest {
        val state = retrying()

        state.onError()
        advanceTimeBy(1_999)
        assertEquals(0, state.attempt)
        advanceTimeBy(2)
        assertEquals(1, state.attempt)

        state.onError()
        advanceTimeBy(3_999)
        assertEquals(1, state.attempt)
        advanceTimeBy(2)
        assertEquals(2, state.attempt)
    }

    @Test
    fun `a failure that arrives right after a retry is not lost`() = runTest {
        val state = retrying()
        state.onError()
        advanceTimeBy(2_001)

        state.onError()
        runCurrent()
        advanceTimeBy(4_001)

        assertEquals(2, state.attempt)
        assertTrue(state.isWaiting)
    }

    @Test
    fun `it stops retrying and stops waiting after the last attempt fails`() = runTest {
        val state = retrying()
        repeat(3) {
            state.onError()
            advanceTimeBy(20_000)
        }
        assertEquals(3, state.attempt)

        state.onError()
        advanceTimeBy(60_000)

        assertEquals(3, state.attempt)
        assertFalse(state.isWaiting)
    }

    @Test
    fun `a success after a retry ends the waiting`() = runTest {
        val state = retrying()
        state.onError()
        advanceTimeBy(2_001)

        state.onSuccess()

        assertFalse(state.isWaiting)
        assertEquals(1, state.attempt)
    }
}
