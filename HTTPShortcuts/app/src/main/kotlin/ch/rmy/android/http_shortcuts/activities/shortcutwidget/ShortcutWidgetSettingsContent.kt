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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.matchParentSize
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ch.rmy.android.framework.utils.FilePickerUtil
import ch.rmy.android.http_shortcuts.R
import ch.rmy.android.http_shortcuts.components.Checkbox
import ch.rmy.android.http_shortcuts.components.ColorPickerDialog
import ch.rmy.android.http_shortcuts.components.OrderedOptionsSlider
import ch.rmy.android.http_shortcuts.components.SelectionField
import ch.rmy.android.http_shortcuts.components.SettingsButton
import ch.rmy.android.http_shortcuts.components.SettingsGroup
import ch.rmy.android.http_shortcuts.components.ShortcutIcon
import ch.rmy.android.http_shortcuts.components.Spacing
import ch.rmy.android.http_shortcuts.components.VerticalSpacer
import ch.rmy.android.http_shortcuts.data.enums.PaddingType
import ch.rmy.android.http_shortcuts.data.enums.WidgetBackgroundType
import ch.rmy.android.http_shortcuts.data.enums.WidgetTextSource
import ch.rmy.android.http_shortcuts.icons.EditImageContract
import ch.rmy.android.http_shortcuts.widget.WidgetConstants
import coil3.compose.AsyncImage
import java.io.File

private val ICON_SCALE_OPTIONS = arrayOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f)
private val TITLE_SIZE_OPTIONS = WidgetConstants.TITLE_SIZE_OPTIONS.map { it.toInt() }.toTypedArray()

@Composable
fun ShortcutWidgetSettingsContent(
    viewState: ShortcutWidgetSettingsViewState,
    onShowLabelChanged: (Boolean) -> Unit,
    onShowIconChanged: (Boolean) -> Unit,
    onLabelColorButtonClicked: () -> Unit,
    onIconScaleChanged: (Float) -> Unit,
    onBackgroundSelected: (WidgetBackgroundType?) -> Unit,
    onBackgroundImagePicked: (Uri) -> Unit,
    onTitleSizeChanged: (Int) -> Unit,
    onTitleColorButtonClicked: () -> Unit,
    onTitleSourceChanged: (WidgetTextSource) -> Unit,
    onTitleTextChanged: (String) -> Unit,
    onSubtitleSourceChanged: (WidgetTextSource) -> Unit,
    onSubtitleTextChanged: (String) -> Unit,
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        WidgetPreview(viewState)

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

            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.SMALL),
                modifier = Modifier.padding(horizontal = Spacing.MEDIUM),
            ) {
                Text(text = stringResource(R.string.label_widget_title_size))
                OrderedOptionsSlider(
                    modifier = Modifier.fillMaxWidth(),
                    options = TITLE_SIZE_OPTIONS.map { it.toFloat() }.toTypedArray(),
                    value = viewState.titleSize.toFloat(),
                    onValueChange = { onTitleSizeChanged(it.toInt()) },
                )
            }

            SettingsButton(
                title = stringResource(R.string.label_widget_title_color),
                subtitle = viewState.titleColorFormatted,
                onClick = onTitleColorButtonClicked,
            )
        }

        HorizontalDivider()

        SettingsGroup(title = stringResource(R.string.label_widget_section_subtitle)) {
            TextSourceSelector(
                title = stringResource(R.string.label_widget_subtitle_source),
                source = viewState.subtitleSource,
                onSourceChanged = onSubtitleSourceChanged,
                shortcutName = viewState.shortcutName,
                shortcutDescription = viewState.shortcutDescription,
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

        VerticalSpacer(Spacing.MEDIUM)
    }
}

@Composable
private fun WidgetPreview(viewState: ShortcutWidgetSettingsViewState) {
    val titleText = when (viewState.titleSource) {
        WidgetTextSource.SHORTCUT_NAME -> viewState.shortcutName
        WidgetTextSource.CUSTOM -> viewState.titleText.ifEmpty { "—" }
        else -> "—"
    }
    val subtitleText = when (viewState.subtitleSource) {
        WidgetTextSource.SHORTCUT_NAME -> viewState.shortcutName
        WidgetTextSource.SHORTCUT_DESCRIPTION -> viewState.shortcutDescription
        WidgetTextSource.CUSTOM -> viewState.subtitleText
    }

    val backgroundColor = when (val bg = viewState.background) {
        is WidgetBackgroundType.Color -> Color(bg.color)
        else -> colorResource(R.color.widget_preview_background)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.SMALL)
            .sizeIn(minHeight = 140.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .sizeIn(minWidth = 180.dp, minHeight = 140.dp)
                .clip(RoundedCornerShape(28.dp)),
        ) {
            WidgetPreviewBackground(viewState.background, backgroundColor)
            Column(
                modifier = Modifier
                    .matchParentSize()
                    .padding(viewState.padding.paddingDp.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
            if (viewState.showIcon) {
                ShortcutIcon(
                    viewState.shortcutIcon,
                    size = (44.dp * viewState.iconScale).coerceAtLeast(24.dp),
                )
            } else {
                Spacer(modifier = Modifier.size(1.dp))
            }

            Column {
                if (viewState.showLabel) {
                    Text(
                        text = titleText,
                        color = Color(viewState.titleColor),
                        fontSize = with(androidx.compose.ui.platform.LocalDensity.current) { viewState.titleSize.toSp() },
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (subtitleText.isNotEmpty()) {
                    Text(
                        text = subtitleText,
                        color = Color(WidgetConstants.DEFAULT_SUBTITLE_COLOR),
                        fontSize = with(androidx.compose.ui.platform.LocalDensity.current) {
                            (viewState.titleSize - WidgetConstants.SUBTITLE_SIZE_SMALL_OFFSET).toInt().toSp()
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        }
    }
}

@Composable
private fun WidgetPreviewBackground(background: WidgetBackgroundType?, fallbackColor: Color) {
    val context = LocalContext.current
    when (background) {
        is WidgetBackgroundType.Image -> {
            AsyncImage(
                model = File(context.filesDir, background.fileName),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
            )
        }
        else -> {
            Box(modifier = Modifier.matchParentSize().background(fallbackColor))
        }
    }
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
