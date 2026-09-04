package de.galonga.audiotoolbox.app.audio

import androidx.annotation.StringRes
import de.galonga.audiotoolbox.app.R

enum class WaveformType(@StringRes val labelRes: Int, val isTonal: Boolean) {
    SINE(R.string.waveform_sine, isTonal = true),
    SQUARE(R.string.waveform_square, isTonal = true),
    TRIANGLE(R.string.waveform_triangle, isTonal = true),
    SAWTOOTH(R.string.waveform_sawtooth, isTonal = true),
    SWEEP(R.string.waveform_sweep, isTonal = false),
    WHITE_NOISE(R.string.waveform_white_noise, isTonal = false),
    PINK_NOISE(R.string.waveform_pink_noise, isTonal = false)
}
