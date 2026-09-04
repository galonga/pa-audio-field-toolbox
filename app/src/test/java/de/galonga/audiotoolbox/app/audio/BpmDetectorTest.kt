package de.galonga.audiotoolbox.app.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class BpmDetectorTest {

    @Test
    fun `detects tempo from a periodic energy pulse train`() {
        val envelopeRate = 20.0 // Hz
        val detector = BpmDetector(envelopeSampleRateHz = envelopeRate)
        val periodSamples = 10 // 20Hz / 10 samples = 2Hz beat -> 120 BPM

        var lastBpm: Double? = null
        for (i in 0 until 300) {
            val energy = if (i % periodSamples == 0) 1.0 else 0.1
            lastBpm = detector.addEnergySample(energy) ?: lastBpm
        }

        assertNotNull("expected a BPM estimate once enough history accumulated", lastBpm)
        assertEquals(120.0, lastBpm!!, 5.0)
    }

    @Test
    fun `returns null before enough history has accumulated`() {
        val detector = BpmDetector(envelopeSampleRateHz = 20.0)
        assertNull(detector.addEnergySample(1.0))
    }

    @Test
    fun `estimate never falls outside the configured BPM range, even on pure noise`() {
        val minBpm = 50.0
        val maxBpm = 220.0
        val detector = BpmDetector(envelopeSampleRateHz = 20.0, minBpm = minBpm, maxBpm = maxBpm)
        val random = Random(1)

        repeat(400) {
            val bpm = detector.addEnergySample(random.nextDouble())
            if (bpm != null) {
                assertTrue("BPM $bpm was below configured minBpm $minBpm", bpm >= minBpm - 0.01)
                assertTrue("BPM $bpm was above configured maxBpm $maxBpm", bpm <= maxBpm + 0.01)
            }
        }
    }

    @Test
    fun `reset clears accumulated history`() {
        val detector = BpmDetector(envelopeSampleRateHz = 20.0)
        repeat(100) { detector.addEnergySample(if (it % 10 == 0) 1.0 else 0.1) }
        detector.reset()
        assertNull(detector.addEnergySample(1.0))
    }
}
