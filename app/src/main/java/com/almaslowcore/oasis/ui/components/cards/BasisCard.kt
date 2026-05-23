package com.almaslowcore.oasis.ui.components.cards

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.almaslowcore.oasis.ui.theme.AppTheme

enum class BasisCardType {
    Filled,
    Elevated,
    Outlined
}

@Composable
fun BasisCard(
    modifier: Modifier = Modifier,
    type: BasisCardType = BasisCardType.Filled,
    shape: Shape = MaterialTheme.shapes.large,
    colors: CardColors = defaultBasisCardColors(type),
    elevation: CardElevation = defaultBasisCardElevation(type),
    border: BorderStroke? = defaultBasisCardBorder(type),
    contentPadding: PaddingValues = PaddingValues(16.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    when (type) {
        BasisCardType.Filled -> {
            Card(
                modifier = modifier.fillMaxWidth(),
                shape = shape,
                colors = colors,
                elevation = elevation,
                border = border
            ) {
                BasisCardContent(
                    contentPadding = contentPadding,
                    verticalArrangement = verticalArrangement,
                    content = content
                )
            }
        }

        BasisCardType.Elevated -> {
            ElevatedCard(
                modifier = modifier.fillMaxWidth(),
                shape = shape,
                colors = colors,
                elevation = elevation
            ) {
                BasisCardContent(
                    contentPadding = contentPadding,
                    verticalArrangement = verticalArrangement,
                    content = content
                )
            }
        }

        BasisCardType.Outlined -> {
            OutlinedCard(
                modifier = modifier.fillMaxWidth(),
                shape = shape,
                colors = colors,
                elevation = elevation,
                border = border ?: BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                BasisCardContent(
                    contentPadding = contentPadding,
                    verticalArrangement = verticalArrangement,
                    content = content
                )
            }
        }
    }
}

@Composable
private fun BasisCardContent(
    contentPadding: PaddingValues,
    verticalArrangement: Arrangement.Vertical,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.padding(contentPadding),
        verticalArrangement = verticalArrangement,
        content = content
    )
}

@Composable
private fun defaultBasisCardColors(
    type: BasisCardType
): CardColors {
    return when (type) {
        BasisCardType.Filled -> {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        }

        BasisCardType.Elevated -> {
            CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        }

        BasisCardType.Outlined -> {
            CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun defaultBasisCardElevation(
    type: BasisCardType
): CardElevation {
    return when (type) {
        BasisCardType.Filled -> {
            CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
        }

        BasisCardType.Elevated -> {
            CardDefaults.elevatedCardElevation(
                defaultElevation = 4.dp
            )
        }

        BasisCardType.Outlined -> {
            CardDefaults.outlinedCardElevation(
                defaultElevation = 0.dp
            )
        }
    }
}

@Composable
private fun defaultBasisCardBorder(
    type: BasisCardType
): BorderStroke? {
    return when (type) {
        BasisCardType.Filled -> null
        BasisCardType.Elevated -> null

        BasisCardType.Outlined -> BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Preview(
    name = "BasisCard - Light",
    showBackground = true
)
@Composable
private fun BasisCardLightPreview() {
    AppTheme(
        darkTheme = false,
        dynamicColor = false
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BasisCard(
                    type = BasisCardType.Filled
                ) {
                    Text(
                        text = "Filled Card",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Used for normal content blocks.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                BasisCard(
                    type = BasisCardType.Elevated
                ) {
                    Text(
                        text = "Elevated Card",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Used when a card needs more emphasis.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                BasisCard(
                    type = BasisCardType.Outlined
                ) {
                    Text(
                        text = "Outlined Card",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Used for secondary or lighter content.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview(
    name = "BasisCard - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun BasisCardDarkPreview() {
    AppTheme(
        darkTheme = true,
        dynamicColor = false
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BasisCard(
                    type = BasisCardType.Filled
                ) {
                    Text(
                        text = "Filled Card",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Used for normal content blocks.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                BasisCard(
                    type = BasisCardType.Elevated
                ) {
                    Text(
                        text = "Elevated Card",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Used when a card needs more emphasis.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                BasisCard(
                    type = BasisCardType.Outlined
                ) {
                    Text(
                        text = "Outlined Card",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Used for secondary or lighter content.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}