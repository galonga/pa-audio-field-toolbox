package de.galonga.audiotoolbox.app.calculators

/** Distance/delay-time conversions using the speed of sound in dry air at 20°C. */
object DelayMath {
    const val SPEED_OF_SOUND_M_PER_S = 343.0
    private const val METERS_PER_FOOT = 0.3048

    fun metersToMs(meters: Double): Double = meters / SPEED_OF_SOUND_M_PER_S * 1000.0
    fun msToMeters(ms: Double): Double = ms / 1000.0 * SPEED_OF_SOUND_M_PER_S

    fun feetToMs(feet: Double): Double = metersToMs(feet * METERS_PER_FOOT)
    fun msToFeet(ms: Double): Double = msToMeters(ms) / METERS_PER_FOOT
}
