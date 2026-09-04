package de.galonga.audiotoolbox.app.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TapTempoCalculatorTest {

    @Test
    fun `single tap gives no estimate`() {
        val calculator = TapTempoCalculator()
        assertNull(calculator.onTap(0L))
    }

    @Test
    fun `two taps 500ms apart estimate 120 BPM`() {
        val calculator = TapTempoCalculator()
        calculator.onTap(0L)
        val bpm = calculator.onTap(500L)
        assertEquals(120.0, bpm!!, 0.001)
    }

    @Test
    fun `steady taps at 128 BPM average correctly`() {
        val calculator = TapTempoCalculator()
        val intervalMs = 60_000.0 / 128.0
        var time = 0.0
        var last: Double? = null
        repeat(8) {
            last = calculator.onTap(time.toLong())
            time += intervalMs
        }
        assertEquals(128.0, last!!, 0.5)
    }

    @Test
    fun `a long gap resets the sequence instead of averaging across it`() {
        val calculator = TapTempoCalculator(maxIntervalMs = 2000L)
        calculator.onTap(0L)
        calculator.onTap(500L) // 120 BPM so far
        val bpmAfterGap = calculator.onTap(10_000L) // huge gap — should reset, not average
        assertNull(bpmAfterGap) // only one tap in the new sequence
    }

    @Test
    fun `reset clears the tap history`() {
        val calculator = TapTempoCalculator()
        calculator.onTap(0L)
        calculator.onTap(500L)
        calculator.reset()
        assertNull(calculator.onTap(1000L))
    }
}
