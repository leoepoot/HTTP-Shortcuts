package ch.rmy.android.http_shortcuts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ch.rmy.android.http_shortcuts.R
import ch.rmy.android.http_shortcuts.data.enums.FontWeight
import ch.rmy.android.http_shortcuts.widget.WidgetConstants

@Composable
fun FontStylePanel(
    color: Int,
    onColorButtonClicked: () -> Unit,
    size: Float,
    onSizeChanged: (Float) -> Unit,
    weight: FontWeight,
    onWeightChanged: (FontWeight) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.MEDIUM),
        verticalArrangement = Arrangement.spacedBy(Spacing.SMALL),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onColorButtonClicked)
                .padding(vertical = Spacing.SMALL),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(color))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(6.dp),
                    ),
            )
            Column(
                modifier = Modifier.padding(start = Spacing.MEDIUM),
            ) {
                Text(
                    text = stringResource(R.string.label_widget_text_color),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = String.format("#%08x", color),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.label_widget_text_size),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = formatSize(size),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Slider(
            modifier = Modifier.fillMaxWidth(),
            value = size,
            onValueChange = { onSizeChanged(snapFontSize(it)) },
            valueRange = WidgetConstants.FONT_SIZE_MIN..WidgetConstants.FONT_SIZE_MAX,
            steps = ((WidgetConstants.FONT_SIZE_MAX - WidgetConstants.FONT_SIZE_MIN) / WidgetConstants.FONT_SIZE_STEP).toInt() - 1,
        )

        SelectionField(
            title = stringResource(R.string.label_widget_text_weight),
            selectedKey = weight,
            items = FontWeight.entries.map { it to stringResource(it.labelRes) },
            onItemSelected = onWeightChanged,
        )
    }
}

private fun formatSize(size: Float): String =
    "${snapFontSize(size).roundToInt()} sp"

private fun snapFontSize(value: Float): Float {
    val snapped = (value / WidgetConstants.FONT_SIZE_STEP).roundToInt() * WidgetConstants.FONT_SIZE_STEP
    return snapped.coerceIn(WidgetConstants.FONT_SIZE_MIN, WidgetConstants.FONT_SIZE_MAX)
}
