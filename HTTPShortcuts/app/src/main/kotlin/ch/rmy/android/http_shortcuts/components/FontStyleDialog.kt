package ch.rmy.android.http_shortcuts.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.res.stringResource
import ch.rmy.android.http_shortcuts.R
import ch.rmy.android.http_shortcuts.data.enums.FontWeight

@Composable
fun FontStyleDialog(
    title: String,
    color: Int,
    size: Float,
    weight: FontWeight,
    onColorChanged: (Int) -> Unit,
    onSizeChanged: (Float) -> Unit,
    onWeightChanged: (FontWeight) -> Unit,
    onDismiss: () -> Unit,
) {
    var colorPickerVisible by remember { mutableStateOf(false) }

    AlertDialog(
        modifier = Modifier.padding(Spacing.MEDIUM),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            FontStylePanel(
                color = color,
                onColorButtonClicked = { colorPickerVisible = true },
                size = size,
                onSizeChanged = onSizeChanged,
                weight = weight,
                onWeightChanged = onWeightChanged,
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_ok))
            }
        },
    )

    if (colorPickerVisible) {
        ColorPickerDialog(
            initialColor = color,
            withAlpha = true,
            onColorSelected = { newColor ->
                onColorChanged(newColor)
                colorPickerVisible = false
            },
            onDismissRequested = { colorPickerVisible = false },
        )
    }
}
