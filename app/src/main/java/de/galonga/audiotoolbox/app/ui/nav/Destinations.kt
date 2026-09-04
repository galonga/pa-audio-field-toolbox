package de.galonga.audiotoolbox.app.ui.nav

/** Top-level destinations reachable from the bottom navigation bar. */
sealed class TopLevelDestination(val route: String) {
    data object Tools : TopLevelDestination("tools")
    data object Analyzer : TopLevelDestination("analyzer")
    data object Generator : TopLevelDestination("generator")
    data object Record : TopLevelDestination("record")
}

/** Detail screens pushed on top of the Tools tab. */
object CalculatorDestinations {
    const val DbCalculator = "db_calculator"
    const val DelayCalculator = "delay_calculator"
    const val PowerImpedanceCalculator = "power_impedance_calculator"
    const val Settings = "settings"
}
