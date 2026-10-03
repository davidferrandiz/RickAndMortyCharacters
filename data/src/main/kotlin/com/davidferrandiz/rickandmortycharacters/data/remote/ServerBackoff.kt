package com.davidferrandiz.rickandmortycharacters.data.remote

import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ServerBackoff @Inject constructor() {

    private val blockedUntil = AtomicLong(0L)

    fun millisUntilOpen(now: Long): Long = (blockedUntil.get() - now).coerceAtLeast(0L)

    fun block(now: Long, millis: Long) {
        blockedUntil.updateAndGet { current -> maxOf(current, now + millis) }
    }
}
