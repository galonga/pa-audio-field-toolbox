package de.galonga.audiotoolbox.app.audio

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sin

/**
 * Pure per-sample oscillator shapes, driven by a phase accumulator (`frac`, always in [0, 1)).
 * Using an accumulated phase rather than `frequency * time` keeps output click-free even when
 * frequency changes between samples (e.g. during a sweep).
 */
object WaveformMath {

    fun advancePhase(currentFrac: Double, frequencyHz: Double, sampleRate: Int): Double {
        val next = (currentFrac + frequencyHz / sampleRate) % 1.0
        return if (next < 0.0) next + 1.0 else next
    }

    fun sine(frac: Double): Float = sin(2.0 * PI * frac).toFloat()

    fun square(frac: Double): Float = if (frac < 0.5) 1f else -1f

    fun triangle(frac: Double): Float = (4.0 * abs(frac - 0.5) - 1.0).toFloat()

    fun sawtooth(frac: Double): Float = (2.0 * frac - 1.0).toFloat()

    /** Logarithmic sweep frequency at position `t` (0..1) of one sweep cycle from startHz to endHz. */
    fun logSweepFrequency(t: Double, startHz: Double, endHz: Double): Double =
        startHz * (endHz / startHz).pow(t.coerceIn(0.0, 1.0))
}
