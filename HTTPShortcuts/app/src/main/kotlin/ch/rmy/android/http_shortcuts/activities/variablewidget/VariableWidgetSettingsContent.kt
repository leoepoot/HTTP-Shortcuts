package ch.rmy.android.http_shortcuts.activities.variablewidget

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ch.rmy.android.framework.utils.FilePickerUtil
import ch.rmy.android.http_shortcuts.R
import ch.rmy.android.http_shortcuts.components.ColorPickerDialog
import ch.rmy.android.http_shortcuts.components.FontSize
import ch.rmy.android.http_shortcuts.components.FontStyleDialog
import ch.rmy.android.http_shortcuts.components.SelectionField
import ch.rmy.android.http_shortcuts.components.SettingsButton
import ch.rmy.android.http_shortcuts.components.SettingsGroup
import ch.rmy.android.http_shortcuts.components.Spacing
import ch.rmy.android.http_shortcuts.components.VerticalSpacer
import ch.rmy.android.http_shortcuts.components.formatFontStyleSummary
import ch.rmy.android.http_shortcuts.data.domains.shortcuts.ShortcutId
import ch.rmy.android.http_shortcuts.data.domains.variables.GlobalVariableId
import ch.rmy.android.http_shortcuts.data.enums.FontWeight
import ch.rmy.android.http_shortcuts.data.enums.PaddingType
import ch.rmy.android.http_shortcuts.data.enums.SubtitlePosition
import ch.rmy.android.http_shortcuts.data.enums.WidgetBackgroundType
import ch.rmy.android.http_shortcuts.icons.EditImageContract
import ch.rmy.android.http_shortcuts.widget.WidgetConstants

@Composable
fun VariableWidgetSettingsContent(
    viewState: VariableWidgetSettingsViewState,
    onVariableSelected: (GlobalVariableId?) -> Unit,
    onTitleChanged: (String) -> Unit,
    onBackgroundChanged: (WidgetBackgroundType?) -> Unit,
    onBackgroundImagePicked: (Uri) -> Unit,
    onShortcutSelected: (ShortcutId?) -> Unit,
    onTitleSizeChanged: (Float) -> Unit,
    onTitleColorChanged: (Int) -> Unit,
    onTitleFontWeightChanged: (FontWeight) -> Unit,
    onValueSizeChanged: (Float) -> Unit,
    onValueColorChanged: (Int) -> Unit,
    onValueFontWeightChanged: (FontWeight) -> Unit,
    onUnitChanged: (String) -> Unit,
    onUnitSizeChanged: (Float) -> Unit,
    onUnitColorChanged: (Int) -> Unit,
    onUnitFontWeightChanged: (FontWeight) -> Unit,
    onSubtitleTextChanged: (String) -> Unit,
    onSubtitlePositionChanged: (SubtitlePosition) -> Unit,
    onSubtitleSizeChanged: (Float) -> Unit,
    onSubtitleColorChanged: (Int) -> Unit,
    onSubtitleFontWeightChanged: (FontWeight) -> Unit,
    onPaddingChanged: (PaddingType) -> Unit,
) {
    var styleDialogTarget by remember { mutableStateOf<VariableFontStyleTarget?>(null) }
    val cropImage = rememberLauncherForActivityResult(
        EditImageContract(
            title = stringResource(R.string.title_edit_widget_background),
            maxSize = WidgetConstants.WIDGET_BACKGROUND_MAX_SIZE_PX,
        ),
    ) { result ->
        if (result is EditImageContract.Result.Success) {
            onBackgroundImagePicked(result.imageUri)
        }
    }
    val pickImage = rememberLauncherForActivityResult(FilePickerUtil.PickFile) { uri ->
        uri?.let { cropImage.launch(EditImageContract.Input(it)) }
    }

    if (viewState.selectableVariables.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.empty_state_variable_widget, stringResource(R.string.variable_type_constant)),
                textAlign = TextAlign.Center,
                fontSize = FontSize.MEDIUM,
                lineHeight = FontSize.BIG,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.MEDIUM),
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        SettingsGroup(title = stringResource(R.string.label_widget_section_appearance)) {
            BackgroundSelector(
                background = viewState.background,
                onNoneSelected = { onBackgroundChanged(null) },
                onColorSelected = { colorInt -> onBackgroundChanged(WidgetBackgroundType.Color(colorInt)) },
                onPickImage = { pickImage.launch("image/*") },
            )

            HorizontalDivider()

            PaddingSelector(
                padding = viewState.padding,
                onPaddingChanged = onPaddingChanged,
            )
        }

        HorizontalDivider()

        SelectionField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.MEDIUM),
            title = stringResource(R.string.label_variable_widget_select_variable),
            selectedKey = viewState.selectedVariable?.variableId,
            items = buildList {
                if (viewState.selectableVariables.none { it.variableId == viewState.selectedVariable?.variableId }) {
                    add(null to "---")
                }
                viewState.selectableVariables.forEach { variable ->
                    add(variable.variableId to variable.variableKey)
                }
            },
            onItemSelected = onVariableSelected,
        )

        HorizontalDivider()

        SettingsGroup(title = stringResource(R.string.label_widget_section_title)) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.MEDIUM, vertical = Spacing.SMALL),
                label = { Text(stringResource(R.string.label_variable_widget_title)) },
                value = viewState.title,
                onValueChange = onTitleChanged,
                maxLines = 2,
            )

            SettingsButton(
                title = stringResource(R.string.label_widget_text_style),
                subtitle = formatFontStyleSummary(viewState.titleColor, viewState.titleSize, viewState.titleFontWeight),
                onClick = { styleDialogTarget = VariableFontStyleTarget.TITLE },
            )
        }

        HorizontalDivider()

        SettingsGroup(title = stringResource(R.string.label_widget_section_value)) {
            SettingsButton(
                title = stringResource(R.string.label_widget_text_style),
                subtitle = formatFontStyleSummary(viewState.valueColor, viewState.valueSize, viewState.valueFontWeight),
                onClick = { styleDialogTarget = VariableFontStyleTarget.VALUE },
            )

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.MEDIUM, vertical = Spacing.SMALL),
                label = { Text(stringResource(R.string.label_widget_unit)) },
                value = viewState.unit,
                onValueChange = onUnitChanged,
                singleLine = true,
            )

            SettingsButton(
                title = stringResource(R.string.label_widget_text_style),
                subtitle = formatFontStyleSummary(viewState.unitColor, viewState.unitSize, viewState.unitFontWeight),
                onClick = { styleDialogTarget = VariableFontStyleTarget.UNIT },
            )
        }

        HorizontalDivider()

        SettingsGroup(title = stringResource(R.string.label_widget_section_subtitle)) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.MEDIUM, vertical = Spacing.SMALL),
                label = { Text(stringResource(R.string.label_widget_custom_subtitle)) },
                value = viewState.subtitleText,
                onValueChange = onSubtitleTextChanged,
                singleLine = true,
            )

            Text(
                text = stringResource(R.string.label_widget_subtitle_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = Spacing.MEDIUM, vertical = Spacing.SMALL),
            )

            if (viewState.subtitleText.isNotEmpty()) {
                SelectionField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.MEDIUM),
                    title = stringResource(R.string.label_widget_subtitle_position),
                    selectedKey = viewState.subtitlePosition,
                    items = SubtitlePosition.entries.map { position ->
                        position to stringResource(
                            when (position) {
                                SubtitlePosition.TOP_END -> R.string.label_widget_subtitle_position_top_end
                                SubtitlePosition.BOTTOM_END -> R.string.label_widget_subtitle_position_bottom_end
                            }
                        )
                    },
                    onItemSelected = onSubtitlePositionChanged,
                )
            }

            SettingsButton(
                title = stringResource(R.string.label_widget_text_style),
                subtitle = formatFontStyleSummary(viewState.subtitleColor, viewState.subtitleSize, viewState.subtitleFontWeight),
                onClick = { styleDialogTarget = VariableFontStyleTarget.SUBTITLE },
            )
        }

        HorizontalDivider()

        SelectionField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.MEDIUM),
            title = stringResource(R.string.label_variable_widget_select_shortcut),
            selectedKey = viewState.shortcutId,
            items = buildList {
                add(null to "---")
                viewState.selectableShortcuts.forEach { shortcut ->
                    add(shortcut.shortcutId to shortcut.name)
                }
            },
            onItemSelected = onShortcutSelected,
        )

        styleDialogTarget?.let { target ->
            when (target) {
                VariableFontStyleTarget.TITLE -> FontStyleDialog(
                    title = stringResource(R.string.label_widget_section_title),
                    color = viewState.titleColor,
                    size = viewState.titleSize,
                    weight = viewState.titleFontWeight,
                    onColorChanged = onTitleColorChanged,
                    onSizeChanged = onTitleSizeChanged,
                    onWeightChanged = onTitleFontWeightChanged,
                    onDismiss = { styleDialogTarget = null },
                )
                VariableFontStyleTarget.VALUE -> FontStyleDialog(
                    title = stringResource(R.string.label_widget_section_value),
                    color = viewState.valueColor,
                    size = viewState.valueSize,
                    weight = viewState.valueFontWeight,
                    onColorChanged = onValueColorChanged,
                    onSizeChanged = onValueSizeChanged,
                    onWeightChanged = onValueFontWeightChanged,
                    onDismiss = { styleDialogTarget = null },
                )
                VariableFontStyleTarget.UNIT -> FontStyleDialog(
                    title = stringResource(R.string.label_widget_unit),
                    color = viewState.unitColor,
                    size = viewState.unitSize,
                    weight = viewState.unitFontWeight,
                    onColorChanged = onUnitColorChanged,
                    onSizeChanged = onUnitSizeChanged,
                    onWeightChanged = onUnitFontWeightChanged,
                    onDismiss = { styleDialogTarget = null },
                )
                VariableFontStyleTarget.SUBTITLE -> FontStyleDialog(
                    title = stringResource(R.string.label_widget_section_subtitle),
                    color = viewState.subtitleColor,
                    size = viewState.subtitleSize,
                    weight = viewState.subtitleFontWeight,
                    onColorChanged = onSubtitleColorChanged,
                    onSizeChanged = onSubtitleSizeChanged,
                    onWeightChanged = onSubtitleFontWeightChanged,
                    onDismiss = { styleDialogTarget = null },
                )
            }
        }

        VerticalSpacer(Spacing.MEDIUM)
    }
}

private enum class VariableFontStyleTarget {
    TITLE,
    VALUE,
    UNIT,
    SUBTITLE,
}

@Composable
private fun BackgroundSelector(
    background: WidgetBackgroundType?,
    onNoneSelected: () -> Unit,
    onColorSelected: (Int) -> Unit,
    onPickImage: () -> Unit,
) {
    val isColor = background is WidgetBackgroundType.Color
    val isImage = background is WidgetBackgroundType.Image
    var colorDialogVisible by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.MEDIUM),
        horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL),
    ) {
        BackgroundOption(
            label = stringResource(R.string.label_widget_background_none),
            selected = background == null,
            onClick = onNoneSelected,
        )
        BackgroundOption(
            label = stringResource(R.string.label_widget_background_color),
            selected = isColor,
            color = if (isColor) Color((background as WidgetBackgroundType.Color).color) else null,
            onClick = { colorDialogVisible = true },
        )
        BackgroundOption(
            label = stringResource(R.string.label_widget_background_image),
            selected = isImage,
            onClick = onPickImage,
        )
    }

    if (colorDialogVisible) {
        ColorPickerDialog(
            withAlpha = true,
            onColorSelected = { color ->
                colorDialogVisible = false
                onColorSelected(color)
            },
            onDismissRequested = { colorDialogVisible = false },
        )
    }
}

@Composable
private fun BackgroundOption(
    label: String,
    selected: Boolean,
    color: Color? = null,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickableWithRipple(onClick)
            .padding(Spacing.SMALL),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color ?: Color.Gray.copy(alpha = 0.3f),
                    RoundedCornerShape(8.dp),
                )
                .borderIfSelected(selected),
        )
        VerticalSpacer(Spacing.TINY)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
        )
    }
}

@Composable
private fun PaddingSelector(
    padding: PaddingType,
    onPaddingChanged: (PaddingType) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.MEDIUM, vertical = Spacing.SMALL),
        horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL),
    ) {
        PaddingType.entries.forEach { type ->
            PaddingOption(
                modifier = Modifier.weight(1f),
                label = stringResource(type.labelRes),
                selected = padding == type,
                onClick = { onPaddingChanged(type) },
            )
        }
    }
}

@Composable
private fun PaddingOption(
    modifier: Modifier = Modifier,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clickableWithRipple(onClick)
            .padding(Spacing.SMALL),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun Modifier.clickableWithRipple(onClick: () -> Unit): Modifier =
    this.then(
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(),
            onClick = onClick,
        )
    )

@Composable
private fun Modifier.borderIfSelected(selected: Boolean): Modifier =
    if (selected) {
        this.then(
            Modifier.border(
                width = 3.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp),
            )
        )
    } else {
        this
    }
