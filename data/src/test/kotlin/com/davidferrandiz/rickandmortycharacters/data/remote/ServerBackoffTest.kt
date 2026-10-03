package com.davidferrandiz.rickandmortycharacters.data.remote

import org.junit.Assert.assertEquals
import org.junit.Test

class ServerBackoffTest {

    private val backoff = ServerBackoff()

    @Test
    fun `nothing is blocked until the server asks to wait`() {
        assertEquals(0L, backoff.millisUntilOpen(now = 1_000))
    }

    @Test
    fun `after a penalty every request waits until it ends`() {
        backoff.block(now = 1_000, millis = 9_000)

        assertEquals(9_000L, backoff.millisUntilOpen(now = 1_000))
        assertEquals(4_000L, backoff.millisUntilOpen(now = 6_000))
        assertEquals(0L, backoff.millisUntilOpen(now = 10_000))
    }

    @Test
    fun `a shorter penalty never shortens one already running`() {
        backoff.block(now = 1_000, millis = 9_000)
        backoff.block(now = 2_000, millis = 1_000)

        assertEquals(8_000L, backoff.millisUntilOpen(now = 2_000))
    }
}
