package com.almaslowcore.oasis.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class BottomBarAction(
    val label: String,
    val icon: ImageVector? = null,
    val enabled: Boolean = true,
    val isPrimary: Boolean = false,
    val onClick: () -> Unit
)

data class BottomBarConfig(
    val actions: List<BottomBarAction> = emptyList()
)

class BottomBarController {

    var config by mutableStateOf<BottomBarConfig?>(null)
        private set

    fun updateConfig(config: BottomBarConfig) {
        this.config = config
    }

    fun clear() {
        config = null
    }
}

val LocalBottomBarController = compositionLocalOf<BottomBarController> {
    error("BottomBarController is not provided")
}

@Composable
fun OasisBottomBar(
    config: BottomBarConfig,
    modifier: Modifier = Modifier
) {
    if (config.actions.isEmpty()) return

    BottomAppBar(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(
                space = 12.dp,
                alignment = Alignment.End
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            config.actions.forEach { action ->
                if (action.isPrimary) {
                    Button(
                        enabled = action.enabled,
                        onClick = action.onClick
                    ) {
                        BottomBarActionContent(action)
                    }
                } else {
                    OutlinedButton(
                        enabled = action.enabled,
                        onClick = action.onClick
                    ) {
                        BottomBarActionContent(action)
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomBarActionContent(
    action: BottomBarAction
) {
    action.icon?.let { icon ->
        Icon(
            imageVector = icon,
            contentDescription = action.label
        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )
    }

    Text(action.label)
}