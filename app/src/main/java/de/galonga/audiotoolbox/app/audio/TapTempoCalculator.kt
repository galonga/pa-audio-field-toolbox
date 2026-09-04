package de.galonga.audiotoolbox.app.audio

/**
 * Manual tap-tempo: averages the intervals between the last few taps. A gap longer than
 * [maxIntervalMs] since the previous tap starts a fresh sequence rather than skewing the average.
 */
class TapTempoCalculator(
    private val maxIntervalMs: Long = 2500L,
    private val maxTaps: Int = 8
) {
    private val tapTimestampsMs = ArrayDeque<Long>()

    /** Register a tap at [timestampMs]; returns the current BPM estimate, or null until at least 2 taps are in. */
    fun onTap(timestampMs: Long): Double? {
        if (tapTimestampsMs.isNotEmpty() && timestampMs - tapTimestampsMs.last() > maxIntervalMs) {
            tapTimestampsMs.clear()
        }
        tapTimestampsMs.addLast(timestampMs)
        while (tapTimestampsMs.size > maxTaps) tapTimestampsMs.removeFirst()

        if (tapTimestampsMs.size < 2) return null
        val avgIntervalMs = tapTimestampsMs.zipWithNext { a, b -> (b - a).toDouble() }.average()
        return if (avgIntervalMs <= 0.0) null else 60_000.0 / avgIntervalMs
    }

    fun reset() {
        tapTimestampsMs.clear()
    }
}
