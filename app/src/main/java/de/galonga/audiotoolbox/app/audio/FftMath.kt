package de.galonga.audiotoolbox.app.audio

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.log10
import kotlin.math.sin
import kotlin.math.sqrt

/** Radix-2 Cooley-Tukey FFT and magnitude-spectrum helpers. No external DSP dependency needed for this. */
object FftMath {

    fun hannWindow(size: Int): DoubleArray = DoubleArray(size) { i ->
        0.5 - 0.5 * cos(2.0 * PI * i / (size - 1))
    }

    /** Windowed real input in, magnitude spectrum in dB out (first half of bins: 0 Hz to Nyquist). */
    fun magnitudeSpectrumDb(samples: FloatArray, window: DoubleArray): FloatArray {
        val n = samples.size
        val real = DoubleArray(n) { samples[it] * window[it] }
        val imag = DoubleArray(n)
        fft(real, imag)
        val half = n / 2
        return FloatArray(half) { i ->
            val magnitude = sqrt(real[i] * real[i] + imag[i] * imag[i]) / n
            (20.0 * log10(magnitude.coerceAtLeast(1e-9))).toFloat()
        }
    }

    /** In-place iterative radix-2 FFT. `real`/`imag` must have equal, power-of-two length. */
    fun fft(real: DoubleArray, imag: DoubleArray) {
        val n = real.size
        require(n and (n - 1) == 0) { "FFT size must be a power of two, was $n" }

        // Bit-reversal permutation
        var j = 0
        for (i in 1 until n) {
            var bit = n shr 1
            while (j and bit != 0) {
                j = j xor bit
                bit = bit shr 1
            }
            j = j or bit
            if (i < j) {
                val tr = real[i]; real[i] = real[j]; real[j] = tr
                val ti = imag[i]; imag[i] = imag[j]; imag[j] = ti
            }
        }

        // Iterative butterfly passes
        var len = 2
        while (len <= n) {
            val angle = -2.0 * PI / len
            val wr = cos(angle)
            val wi = sin(angle)
            var i = 0
            while (i < n) {
                var curWr = 1.0
                var curWi = 0.0
                for (k in 0 until len / 2) {
                    val evenIdx = i + k
                    val oddIdx = evenIdx + len / 2
                    val oddR = real[oddIdx] * curWr - imag[oddIdx] * curWi
                    val oddI = real[oddIdx] * curWi + imag[oddIdx] * curWr
                    real[oddIdx] = real[evenIdx] - oddR
                    imag[oddIdx] = imag[evenIdx] - oddI
                    real[evenIdx] += oddR
                    imag[evenIdx] += oddI
                    val nextWr = curWr * wr - curWi * wi
                    val nextWi = curWr * wi + curWi * wr
                    curWr = nextWr
                    curWi = nextWi
                }
                i += len
            }
            len = len shl 1
        }
    }
}
