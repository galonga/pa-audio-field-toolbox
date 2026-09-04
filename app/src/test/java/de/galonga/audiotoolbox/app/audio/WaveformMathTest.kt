package de.galonga.audiotoolbox.app.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class WaveformMathTest {

    @Test
    fun `advancePhase wraps around 1`() {
        val next = WaveformMath.advancePhase(currentFrac = 0.9, frequencyHz = 4410.0, sampleRate = 44100)
        // 4410/44100 = 0.1, so 0.9 + 0.1 wraps to 0.0
        assertEquals(0.0, next, 1e-9)
    }

    @Test
    fun `sine at key phase points`() {
        assertEquals(0f, WaveformMath.sine(0.0), 1e-4f)
        assertEquals(1f, WaveformMath.sine(0.25), 1e-4f)
        assertEquals(0f, WaveformMath.sine(0.5), 1e-4f)
        assertEquals(-1f, WaveformMath.sine(0.75), 1e-4f)
    }

    @Test
    fun `square is bipolar and flips at the midpoint`() {
        assertEquals(1f, WaveformMath.square(0.0))
        assertEquals(1f, WaveformMath.square(0.49))
        assertEquals(-1f, WaveformMath.square(0.5))
        assertEquals(-1f, WaveformMath.square(0.99))
    }

    @Test
    fun `triangle stays within range and hits its peak and trough`() {
        assertEquals(1f, WaveformMath.triangle(0.0), 1e-6f)
        assertEquals(-1f, WaveformMath.triangle(0.5), 1e-6f)
        for (i in 0..99) {
            val frac = i / 100.0
            assertTrue(abs(WaveformMath.triangle(frac)) <= 1f + 1e-6f)
        }
    }

    @Test
    fun `sawtooth ramps linearly from -1 to 1`() {
        assertEquals(-1f, WaveformMath.sawtooth(0.0), 1e-6f)
        assertEquals(0f, WaveformMath.sawtooth(0.5), 1e-6f)
        assertTrue(WaveformMath.sawtooth(0.99) > WaveformMath.sawtooth(0.5))
    }

    @Test
    fun `log sweep interpolates geometrically between start and end`() {
        assertEquals(20.0, WaveformMath.logSweepFrequency(0.0, 20.0, 20000.0), 1e-6)
        assertEquals(20000.0, WaveformMath.logSweepFrequency(1.0, 20.0, 20000.0), 1e-6)
        // Halfway on a log sweep from 20 to 20000 (3 decades) should be the geometric mean.
        val mid = WaveformMath.logSweepFrequency(0.5, 20.0, 20000.0)
        assertEquals(kotlin.math.sqrt(20.0 * 20000.0), mid, 1e-6)
    }
}
