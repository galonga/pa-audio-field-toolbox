package de.galonga.audiotoolbox.app.calculators

/** Formats a computed result for display, or an em dash for undefined results (e.g. log of a non-positive number). */
fun Double.formatOrDash(decimals: Int): String =
    if (this.isNaN() || this.isInfinite()) "—" else "%.${decimals}f".format(this)
