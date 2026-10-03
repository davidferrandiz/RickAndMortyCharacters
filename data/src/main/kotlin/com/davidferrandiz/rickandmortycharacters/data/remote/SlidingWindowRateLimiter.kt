package com.davidferrandiz.rickandmortycharacters.data.remote

internal class SlidingWindowRateLimiter(
    private val maxRequests: Int,
    private val windowMillis: Long,
    private val now: () -> Long = System::currentTimeMillis,
) {
    private val grantedAt = ArrayDeque<Long>()

    @Synchronized
    fun millisUntilGranted(): Long {
        val current = now()
        while (grantedAt.isNotEmpty() && current - grantedAt.first() >= windowMillis) {
            grantedAt.removeFirst()
        }
        if (grantedAt.size < maxRequests) {
            grantedAt.addLast(current)
            return 0L
        }
        return windowMillis - (current - grantedAt.first())
    }
}
