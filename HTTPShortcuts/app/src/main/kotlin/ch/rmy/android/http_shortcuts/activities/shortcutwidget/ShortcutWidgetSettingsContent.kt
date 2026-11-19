package ch.rmy.android.http_shortcuts.activities.shortcutwidget

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
import ch.rmy.android.http_shortcuts.components.Checkbox
import ch.rmy.android.http_shortcuts.components.ColorPickerDialog
import ch.rmy.android.http_shortcuts.components.FontSize
import ch.rmy.android.http_shortcuts.components.FontStyleDialog
import ch.rmy.android.http_shortcuts.components.OrderedOptionsSlider
import ch.rmy.android.http_shortcuts.components.SelectionField
import ch.rmy.android.http_shortcuts.components.SettingsButton
import ch.rmy.android.http_shortcuts.components.SettingsGroup
import ch.rmy.android.http_shortcuts.components.Spacing
import ch.rmy.android.http_shortcuts.components.VerticalSpacer
import ch.rmy.android.http_shortcuts.components.formatFontStyleSummary
import ch.rmy.android.http_shortcuts.data.domains.shortcuts.ShortcutId
import ch.rmy.android.http_shortcuts.data.enums.FontWeight
import ch.rmy.android.http_shortcuts.data.enums.PaddingType
import ch.rmy.android.http_shortcuts.data.enums.WidgetBackgroundType
import ch.rmy.android.http_shortcuts.data.enums.WidgetTextSource
import ch.rmy.android.http_shortcuts.icons.EditImageContract
import ch.rmy.android.http_shortcuts.widget.WidgetConstants

private val ICON_SCALE_OPTIONS = arrayOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f)

@Composable
fun ShortcutWidgetSettingsContent(
    viewState: ShortcutWidgetSettingsViewState,
    onShortcutSelected: (ShortcutId?) -> Unit,
    onShowLabelChanged: (Boolean) -> Unit,
    onShowIconChanged: (Boolean) -> Unit,
    onLabelColorButtonClicked: () -> Unit,
    onIconScaleChanged: (Float) -> Unit,
    onBackgroundSelected: (WidgetBackgroundType?) -> Unit,
    onBackgroundImagePicked: (Uri) -> Unit,
    onTitleSizeChanged: (Float) -> Unit,
    onTitleColorChanged: (Int) -> Unit,
    onTitleFontWeightChanged: (FontWeight) -> Unit,
    onTitleSourceChanged: (WidgetTextSource) -> Unit,
    onTitleTextChanged: (String) -> Unit,
    onSubtitleSourceChanged: (WidgetTextSource) -> Unit,
    onSubtitleTextChanged: (String) -> Unit,
    onSubtitleSizeChanged: (Float) -> Unit,
    onSubtitleColorChanged: (Int) -> Unit,
    onSubtitleFontWeightChanged: (FontWeight) -> Unit,
    onPaddingChanged: (PaddingType) -> Unit,
) {
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

    if (viewState.selectableShortcuts.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.empty_state_shortcut_widget),
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

    val selectedShortcut = viewState.selectableShortcuts.find { it.shortcutId == viewState.selectedShortcutId }
    var styleDialogTarget by remember { mutableStateOf<ShortcutFontStyleTarget?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        SelectionField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.MEDIUM),
            title = stringResource(R.string.label_shortcut_widget_select_shortcut),
            selectedKey = viewState.selectedShortcutId,
            items = viewState.selectableShortcuts.map { it.shortcutId to it.name },
            onItemSelected = onShortcutSelected,
        )

        HorizontalDivider()

        SettingsGroup(title = stringResource(R.string.label_widget_section_appearance)) {
            BackgroundSelector(
                background = viewState.background,
                onNoneSelected = { onBackgroundSelected(null) },
                onColorSelected = { colorInt -> onBackgroundSelected(WidgetBackgroundType.Color(colorInt)) },
                onPickImage = { pickImage.launch("image/*") },
            )

            HorizontalDivider()

            PaddingSelector(
                padding = viewState.padding,
                onPaddingChanged = onPaddingChanged,
            )
        }

        HorizontalDivider()

        SettingsGroup(title = stringResource(R.string.label_widget_section_icon)) {
            Checkbox(
                checked = viewState.showIcon,
                label = stringResource(R.string.label_show_widget_icon),
                onCheckedChange = onShowIconChanged,
            )

            if (viewState.showIcon) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.SMALL),
                    modifier = Modifier.padding(Spacing.MEDIUM),
                ) {
                    Text(text = stringResource(R.string.label_widget_icon_size))
                    OrderedOptionsSlider(
                        modifier = Modifier.fillMaxWidth(),
                        options = ICON_SCALE_OPTIONS,
                        value = viewState.iconScale,
                        onValueChange = onIconScaleChanged,
                    )
                }
            }
        }

        HorizontalDivider()

        SettingsGroup(title = stringResource(R.string.label_widget_section_title)) {
            TextSourceSelector(
                title = stringResource(R.string.label_widget_title_source),
                source = viewState.titleSource,
                onSourceChanged = onTitleSourceChanged,
            )

            if (viewState.titleSource == WidgetTextSource.CUSTOM) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.MEDIUM, vertical = Spacing.SMALL),
                    value = viewState.titleText,
                    onValueChange = onTitleTextChanged,
                    label = { Text(stringResource(R.string.label_widget_custom_title)) },
                    singleLine = true,
                )
            }

            SettingsButton(
                title = stringResource(R.string.label_widget_text_style),
                subtitle = formatFontStyleSummary(viewState.titleColor, viewState.titleSize, viewState.titleFontWeight),
                onClick = { styleDialogTarget = ShortcutFontStyleTarget.TITLE },
            )
        }

        HorizontalDivider()

        SettingsGroup(title = stringResource(R.string.label_widget_section_subtitle)) {
            TextSourceSelector(
                title = stringResource(R.string.label_widget_subtitle_source),
                source = viewState.subtitleSource,
                onSourceChanged = onSubtitleSourceChanged,
                shortcutName = selectedShortcut?.name,
                shortcutDescription = selectedShortcut?.description,
            )

            if (viewState.subtitleSource == WidgetTextSource.CUSTOM) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.MEDIUM, vertical = Spacing.SMALL),
                    value = viewState.subtitleText,
                    onValueChange = onSubtitleTextChanged,
                    label = { Text(stringResource(R.string.label_widget_custom_subtitle)) },
                    singleLine = true,
                )
            }

            Text(
                text = stringResource(R.string.label_widget_subtitle_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = Spacing.MEDIUM, vertical = Spacing.SMALL),
            )

            SettingsButton(
                title = stringResource(R.string.label_widget_text_style),
                subtitle = formatFontStyleSummary(viewState.subtitleColor, viewState.subtitleSize, viewState.subtitleFontWeight),
                onClick = { styleDialogTarget = ShortcutFontStyleTarget.SUBTITLE },
            )
        }

        HorizontalDivider()

        SettingsGroup(title = stringResource(R.string.label_widget_section_legacy)) {
            Checkbox(
                checked = viewState.showLabel,
                label = stringResource(R.string.label_show_widget_label),
                subtitle = stringResource(R.string.label_show_widget_label_subtitle),
                onCheckedChange = onShowLabelChanged,
            )

            if (viewState.showLabel) {
                SettingsButton(
                    title = stringResource(R.string.label_widget_label_color),
                    subtitle = viewState.labelColorFormatted,
                    onClick = onLabelColorButtonClicked,
                )
            }
        }

        styleDialogTarget?.let { target ->
            when (target) {
                ShortcutFontStyleTarget.TITLE -> FontStyleDialog(
                    title = stringResource(R.string.label_widget_section_title),
                    color = viewState.titleColor,
                    size = viewState.titleSize,
                    weight = viewState.titleFontWeight,
                    onColorChanged = onTitleColorChanged,
                    onSizeChanged = onTitleSizeChanged,
                    onWeightChanged = onTitleFontWeightChanged,
                    onDismiss = { styleDialogTarget = null },
                )
                ShortcutFontStyleTarget.SUBTITLE -> FontStyleDialog(
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

private enum class ShortcutFontStyleTarget {
    TITLE,
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
private fun TextSourceSelector(
    title: String,
    source: WidgetTextSource,
    onSourceChanged: (WidgetTextSource) -> Unit,
    shortcutName: String? = null,
    shortcutDescription: String? = null,
) {
    val items = buildList {
        add(WidgetTextSource.SHORTCUT_NAME to stringResource(R.string.label_widget_source_shortcut_name, shortcutName ?: ""))
        if (shortcutDescription != null) {
            add(WidgetTextSource.SHORTCUT_DESCRIPTION to stringResource(R.string.label_widget_source_shortcut_description, shortcutDescription.ifEmpty { "—" }))
        }
        add(WidgetTextSource.CUSTOM to stringResource(R.string.label_widget_source_custom))
    }

    SelectionField(
        title = title,
        selectedKey = source,
        items = items,
        onItemSelected = onSourceChanged,
    )
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
