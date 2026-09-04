package de.galonga.audiotoolbox.app.audio

import kotlin.math.ceil

/**
 * Estimates tempo from a stream of per-buffer energy (RMS) samples using a simple
 * onset/novelty function (half-wave-rectified deviation from a slow energy average)
 * followed by autocorrelation over a sliding history window.
 *
 * This is a broadband energy novelty function, not full per-bin spectral flux — good
 * enough for material with a clear percussive beat, less reliable on sparse/ambient audio.
 *
 * @param envelopeSampleRateHz how many energy samples per second are fed via [addEnergySample]
 *   (i.e. the mic engine's buffer rate, e.g. SAMPLE_RATE / FFT_SIZE).
 */
class BpmDetector(
    private val envelopeSampleRateHz: Double,
    private val historySeconds: Double = 8.0,
    private val minBpm: Double = 50.0,
    private val maxBpm: Double = 220.0
) {
    // Ceil (not truncate) so minLag never represents a BPM higher than maxBpm — a lag rounded
    // *down* would correspond to a faster tempo than the configured ceiling allows.
    private val minLag = ceil(envelopeSampleRateHz * 60.0 / maxBpm).toInt().coerceAtLeast(1)
    private val maxLag = (envelopeSampleRateHz * 60.0 / minBpm).toInt().coerceAtLeast(minLag + 1)
    private val maxHistorySize = (envelopeSampleRateHz * historySeconds).toInt().coerceAtLeast(maxLag * 2)
    private val minSamplesForEstimate = (maxLag * 2).coerceAtMost(maxHistorySize)

    private val novelty = ArrayDeque<Double>()
    private var runningAverage = 0.0

    /** Feed one energy sample; returns the current BPM estimate, or null while still warming up. */
    fun addEnergySample(rms: Double): Double? {
        runningAverage = if (novelty.isEmpty() && runningAverage == 0.0) rms else runningAverage * 0.9 + rms * 0.1
        val flux = (rms - runningAverage).coerceAtLeast(0.0)

        novelty.addLast(flux)
        while (novelty.size > maxHistorySize) novelty.removeFirst()

        if (novelty.size < minSamplesForEstimate) return null
        return estimateBpm(novelty)
    }

    fun reset() {
        novelty.clear()
        runningAverage = 0.0
    }

    private fun estimateBpm(signal: Collection<Double>): Double? {
        val centered = signal.toDoubleArray()
        val mean = centered.average()
        for (i in centered.indices) centered[i] -= mean

        val effectiveMaxLag = minOf(maxLag, centered.size - 1)
        if (effectiveMaxLag <= minLag) return null

        fun correlationAt(lag: Int): Double {
            var sum = 0.0
            for (i in 0 until centered.size - lag) sum += centered[i] * centered[i + lag]
            return sum
        }

        var bestLag = -1
        var bestScore = 0.0
        for (lag in minLag..effectiveMaxLag) {
            val score = correlationAt(lag)
            if (score > bestScore) {
                bestScore = score
                bestLag = lag
            }
        }
        if (bestLag <= 0) return null

        // Parabolic interpolation around the best lag for sub-sample precision.
        val refinedLag = if (bestLag - 1 >= minLag && bestLag + 1 <= effectiveMaxLag) {
            val sMinus = correlationAt(bestLag - 1)
            val s0 = bestScore
            val sPlus = correlationAt(bestLag + 1)
            val denom = sMinus - 2 * s0 + sPlus
            if (denom == 0.0) bestLag.toDouble() else bestLag + 0.5 * (sMinus - sPlus) / denom
        } else {
            bestLag.toDouble()
        }.coerceIn(minLag.toDouble(), effectiveMaxLag.toDouble())

        val periodSeconds = refinedLag / envelopeSampleRateHz
        if (periodSeconds <= 0.0) return null
        return 60.0 / periodSeconds
    }
}
