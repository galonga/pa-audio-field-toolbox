package de.galonga.audiotoolbox.app.calculators

import kotlin.math.log10
import kotlin.math.pow

/** dB/dBu/dBV/ratio conversions. dBu and dBV share the same underlying voltage. */
object DbMath {
    const val DBU_REFERENCE_VOLTS = 0.7746
    const val DBV_REFERENCE_VOLTS = 1.0

    fun voltsToDbu(volts: Double): Double = 20.0 * log10(volts / DBU_REFERENCE_VOLTS)
    fun dbuToVolts(dbu: Double): Double = DBU_REFERENCE_VOLTS * 10.0.pow(dbu / 20.0)

    fun voltsToDbv(volts: Double): Double = 20.0 * log10(volts / DBV_REFERENCE_VOLTS)
    fun dbvToVolts(dbv: Double): Double = DBV_REFERENCE_VOLTS * 10.0.pow(dbv / 20.0)

    fun voltageRatioToDb(ratio: Double): Double = 20.0 * log10(ratio)
    fun dbToVoltageRatio(db: Double): Double = 10.0.pow(db / 20.0)

    fun powerRatioToDb(ratio: Double): Double = 10.0 * log10(ratio)
    fun dbToPowerRatio(db: Double): Double = 10.0.pow(db / 10.0)
}
