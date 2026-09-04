package de.galonga.audiotoolbox.app.audio

import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class PinkNoiseGeneratorTest {

    @Test
    fun `output stays bounded and is not constant`() {
        val generator = PinkNoiseGenerator(Random(42))
        val samples = FloatArray(10000) { generator.next() }

        assertTrue(samples.all { it in -1f..1f })

        val min = samples.min()
        val max = samples.max()
        assertTrue("expected varying output, got a flat $min", max - min > 0.01f)
    }

    @Test
    fun `is deterministic for a fixed seed`() {
        val a = PinkNoiseGenerator(Random(7))
        val b = PinkNoiseGenerator(Random(7))
        repeat(1000) {
            assertTrue(a.next() == b.next())
        }
    }
}
