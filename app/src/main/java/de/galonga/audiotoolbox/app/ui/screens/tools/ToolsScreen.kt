package de.galonga.audiotoolbox.app.ui.screens.tools

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import de.galonga.audiotoolbox.app.R
import de.galonga.audiotoolbox.app.ui.components.about.ScreenContentContainer
import de.galonga.audiotoolbox.app.ui.nav.CalculatorDestinations
import de.galonga.audiotoolbox.design.components.ExpressiveCard
import de.galonga.audiotoolbox.design.token.RbSpacing

private data class ToolItem(
    @StringRes val titleRes: Int,
    @StringRes val subtitleRes: Int,
    val icon: ImageVector,
    val route: String
)

private val tools = listOf(
    ToolItem(
        titleRes = R.string.tool_db_calculator_title,
        subtitleRes = R.string.tool_db_calculator_subtitle,
        icon = Icons.Default.GraphicEq,
        route = CalculatorDestinations.DbCalculator
    ),
    ToolItem(
        titleRes = R.string.tool_delay_calculator_title,
        subtitleRes = R.string.tool_delay_calculator_subtitle,
        icon = Icons.Default.Timer,
        route = CalculatorDestinations.DelayCalculator
    ),
    ToolItem(
        titleRes = R.string.tool_power_calculator_title,
        subtitleRes = R.string.tool_power_calculator_subtitle,
        icon = Icons.Default.Bolt,
        route = CalculatorDestinations.PowerImpedanceCalculator
    ),
    ToolItem(
        titleRes = R.string.settings_screen,
        subtitleRes = R.string.tool_settings_subtitle,
        icon = Icons.Default.Settings,
        route = CalculatorDestinations.Settings
    ),
)

@Composable
fun ToolsScreen(onNavigateToCalculator: (String) -> Unit) {
    ScreenContentContainer {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = RbSpacing.space8)
        )
        Text(
            text = stringResource(R.string.tools_screen_subheadline),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = RbSpacing.space16)
        )
        Text(
            text = stringResource(R.string.tools_screen),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = RbSpacing.space16)
        )
        Column(verticalArrangement = Arrangement.spacedBy(RbSpacing.space12)) {
            tools.forEach { tool ->
                ToolListItem(tool = tool, onClick = { onNavigateToCalculator(tool.route) })
            }
        }
    }
}

@Composable
private fun ToolListItem(tool: ToolItem, onClick: () -> Unit) {
    ExpressiveCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(RbSpacing.space16),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = tool.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = RbSpacing.space16)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stringResource(tool.titleRes), style = MaterialTheme.typography.titleMedium)
                Text(
                    text = stringResource(tool.subtitleRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
