package de.galonga.audiotoolbox.app.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI
import kotlin.math.sin

class FftMathTest {

    private val sampleRate = 44100
    private val fftSize = 2048
    private val window = FftMath.hannWindow(fftSize)

    private fun sineWave(frequencyHz: Double, amplitude: Float = 1f): FloatArray =
        FloatArray(fftSize) { i -> (amplitude * sin(2.0 * PI * frequencyHz * i / sampleRate)).toFloat() }

    private fun binToFrequency(bin: Int): Double = bin.toDouble() * sampleRate / fftSize

    @Test
    fun `pure tone peaks at its own frequency`() {
        val toneHz = 1000.0
        val spectrum = FftMath.magnitudeSpectrumDb(sineWave(toneHz), window)

        val peakBin = spectrum.indices.maxBy { spectrum[it] }
        val peakFreq = binToFrequency(peakBin)

        // Bin resolution is ~21.5 Hz at this sample rate/FFT size — allow one bin of slack.
        assertTrue("expected peak near ${toneHz}Hz, got ${peakFreq}Hz", kotlin.math.abs(peakFreq - toneHz) < sampleRate.toDouble() / fftSize * 1.5)
    }

    @Test
    fun `higher frequency tone peaks at a higher bin`() {
        val lowSpectrum = FftMath.magnitudeSpectrumDb(sineWave(500.0), window)
        val highSpectrum = FftMath.magnitudeSpectrumDb(sineWave(4000.0), window)

        val lowPeakBin = lowSpectrum.indices.maxBy { lowSpectrum[it] }
        val highPeakBin = highSpectrum.indices.maxBy { highSpectrum[it] }

        assertTrue(highPeakBin > lowPeakBin)
    }

    @Test
    fun `silence stays near the noise floor`() {
        val spectrum = FftMath.magnitudeSpectrumDb(FloatArray(fftSize), window)
        spectrum.forEach { db -> assertTrue("expected silence below -60dB, got $db", db < -60f) }
    }

    @Test
    fun `spectrum length is half the fft size`() {
        val spectrum = FftMath.magnitudeSpectrumDb(sineWave(1000.0), window)
        assertEquals(fftSize / 2, spectrum.size)
    }
}
