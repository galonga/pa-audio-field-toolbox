package de.galonga.audiotoolbox.app.calculators

import kotlin.math.sqrt

enum class ElectricalQuantity { VOLTAGE, CURRENT, RESISTANCE, POWER }

/** Ohm's law + power law solver: given any two known quantities, derives the other two. */
object PowerImpedanceMath {
    fun solve(a: ElectricalQuantity, aValue: Double, b: ElectricalQuantity, bValue: Double): Map<ElectricalQuantity, Double>? {
        if (a == b) return null
        val known = mapOf(a to aValue, b to bValue)
        val v = known[ElectricalQuantity.VOLTAGE]
        val i = known[ElectricalQuantity.CURRENT]
        val r = known[ElectricalQuantity.RESISTANCE]
        val p = known[ElectricalQuantity.POWER]

        return when {
            v != null && i != null -> mapOf(
                ElectricalQuantity.VOLTAGE to v, ElectricalQuantity.CURRENT to i,
                ElectricalQuantity.RESISTANCE to v / i, ElectricalQuantity.POWER to v * i
            )
            v != null && r != null -> mapOf(
                ElectricalQuantity.VOLTAGE to v, ElectricalQuantity.CURRENT to v / r,
                ElectricalQuantity.RESISTANCE to r, ElectricalQuantity.POWER to (v * v) / r
            )
            v != null && p != null -> mapOf(
                ElectricalQuantity.VOLTAGE to v, ElectricalQuantity.CURRENT to p / v,
                ElectricalQuantity.RESISTANCE to (v * v) / p, ElectricalQuantity.POWER to p
            )
            i != null && r != null -> mapOf(
                ElectricalQuantity.VOLTAGE to i * r, ElectricalQuantity.CURRENT to i,
                ElectricalQuantity.RESISTANCE to r, ElectricalQuantity.POWER to i * i * r
            )
            i != null && p != null -> mapOf(
                ElectricalQuantity.VOLTAGE to p / i, ElectricalQuantity.CURRENT to i,
                ElectricalQuantity.RESISTANCE to p / (i * i), ElectricalQuantity.POWER to p
            )
            r != null && p != null -> {
                val voltage = sqrt(p * r)
                mapOf(
                    ElectricalQuantity.VOLTAGE to voltage, ElectricalQuantity.CURRENT to voltage / r,
                    ElectricalQuantity.RESISTANCE to r, ElectricalQuantity.POWER to p
                )
            }
            else -> null
        }
    }
}
