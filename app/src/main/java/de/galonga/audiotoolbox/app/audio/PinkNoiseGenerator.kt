package de.galonga.audiotoolbox.app.audio

import kotlin.random.Random

/**
 * Paul Kellet's "refined" pink noise filter — a bank of IIR filters approximating a 1/f
 * (equal energy per octave) spectrum from white noise. Output is roughly in [-1, 1].
 */
class PinkNoiseGenerator(private val random: Random = Random.Default) {
    private var b0 = 0.0
    private var b1 = 0.0
    private var b2 = 0.0
    private var b3 = 0.0
    private var b4 = 0.0
    private var b5 = 0.0
    private var b6 = 0.0

    fun next(): Float {
        val white = random.nextDouble(-1.0, 1.0)
        b0 = 0.99886 * b0 + white * 0.0555179
        b1 = 0.99332 * b1 + white * 0.0750759
        b2 = 0.96900 * b2 + white * 0.1538520
        b3 = 0.86650 * b3 + white * 0.3104856
        b4 = 0.55000 * b4 + white * 0.5329522
        b5 = -0.7616 * b5 - white * 0.0168980
        val pink = b0 + b1 + b2 + b3 + b4 + b5 + b6 + white * 0.5362
        b6 = white * 0.115926
        return (pink * 0.11).toFloat().coerceIn(-1f, 1f)
    }
}
