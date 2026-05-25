package com.almaslowcore.oasis.ui.components.buttons

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.almaslowcore.oasis.ui.theme.AppTheme

@Composable
fun IconButton(
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription
        )
    }
}

@Composable
fun FilledIconButton(
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription
        )
    }
}

@Composable
fun FilledTonalIconButton(
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    FilledTonalIconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription
        )
    }
}

@Composable
fun OutlinedIconButton(
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    OutlinedIconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription
        )
    }
}

@Composable
fun ToggleIconButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    checkedIcon: ImageVector,
    uncheckedIcon: ImageVector,
    checkedContentDescription: String,
    uncheckedContentDescription: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    IconToggleButton(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled
    ) {
        Icon(
            imageVector = if (checked) checkedIcon else uncheckedIcon,
            contentDescription = if (checked) {
                checkedContentDescription
            } else {
                uncheckedContentDescription
            }
        )
    }
}

@Composable
private fun AppIconButtonPreviewContent() {
    Surface {
        Row(
            modifier = Modifier.padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                icon = Icons.Default.Search,
                contentDescription = "Search",
                onClick = {}
            )

            FilledIconButton(
                icon = Icons.Default.Favorite,
                contentDescription = "Favorite",
                onClick = {}
            )

            FilledTonalIconButton(
                icon = Icons.Default.Bookmark,
                contentDescription = "Bookmark",
                onClick = {}
            )

            OutlinedIconButton(
                icon = Icons.Default.Delete,
                contentDescription = "Delete",
                onClick = {}
            )

            IconButton(
                icon = Icons.Default.MoreVert,
                contentDescription = "More options",
                onClick = {}
            )
        }
    }
}

@Preview(
    name = "Icon Buttons - Light Theme",
    showBackground = true
)
@Composable
private fun AppIconButtonLightPreview() {
    AppTheme {
        AppIconButtonPreviewContent()
    }
}

@Preview(
    name = "Icon Buttons - Dark Theme",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun AppIconButtonDarkPreview() {
    AppTheme {
        AppIconButtonPreviewContent()
    }
}

@Preview(
    name = "Toggle Icon Buttons - Light Theme",
    showBackground = true
)
@Composable
private fun AppToggleIconButtonLightPreview() {
    AppTheme {
        Surface {
            Row(
                modifier = Modifier.padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ToggleIconButton(
                    checked = false,
                    onCheckedChange = {},
                    checkedIcon = Icons.Default.Favorite,
                    uncheckedIcon = Icons.Default.FavoriteBorder,
                    checkedContentDescription = "Remove from favorites",
                    uncheckedContentDescription = "Add to favorites"
                )

                ToggleIconButton(
                    checked = true,
                    onCheckedChange = {},
                    checkedIcon = Icons.Default.Bookmark,
                    uncheckedIcon = Icons.Default.BookmarkBorder,
                    checkedContentDescription = "Remove bookmark",
                    uncheckedContentDescription = "Add bookmark"
                )
            }
        }
    }
}

@Preview(
    name = "Toggle Icon Buttons - Dark Theme",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun AppToggleIconButtonDarkPreview() {
    AppTheme {
        Surface {
            Row(
                modifier = Modifier.padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ToggleIconButton(
                    checked = false,
                    onCheckedChange = {},
                    checkedIcon = Icons.Default.Favorite,
                    uncheckedIcon = Icons.Default.FavoriteBorder,
                    checkedContentDescription = "Remove from favorites",
                    uncheckedContentDescription = "Add to favorites"
                )

                ToggleIconButton(
                    checked = true,
                    onCheckedChange = {},
                    checkedIcon = Icons.Default.Bookmark,
                    uncheckedIcon = Icons.Default.BookmarkBorder,
                    checkedContentDescription = "Remove bookmark",
                    uncheckedContentDescription = "Add bookmark"
                )
            }
        }
    }
}