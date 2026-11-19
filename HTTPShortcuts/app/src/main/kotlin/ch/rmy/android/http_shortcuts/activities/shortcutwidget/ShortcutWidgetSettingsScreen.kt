package ch.rmy.android.http_shortcuts.activities.shortcutwidget

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ch.rmy.android.http_shortcuts.R
import ch.rmy.android.http_shortcuts.components.BackButton
import ch.rmy.android.http_shortcuts.components.ColorPickerDialog
import ch.rmy.android.http_shortcuts.components.SimpleScaffold
import ch.rmy.android.http_shortcuts.components.ToolbarIcon
import ch.rmy.android.http_shortcuts.components.bindViewModel

@Composable
fun ShortcutWidgetSettingsScreen(
    widgetId: Int?,
) {
    val (viewModel, state) = bindViewModel<
        ShortcutWidgetSettingsViewModel.InitData,
        ShortcutWidgetSettingsViewState,
        ShortcutWidgetSettingsViewModel,
        >(
        ShortcutWidgetSettingsViewModel.InitData(widgetId),
    )

    BackHandler(state != null) {
        viewModel.onBackPressed()
    }

    SimpleScaffold(
        viewState = state,
        title = stringResource(R.string.title_configure_widget),
        backButton = BackButton.CROSS,
        actions = {
            ToolbarIcon(
                painterResource(R.drawable.outline_check_24),
                contentDescription = stringResource(R.string.action_create_widget),
                onClick = viewModel::onCreateButtonClicked,
            )
        },
    ) { viewState ->
        ShortcutWidgetSettingsContent(
            viewState = viewState,
            onShortcutSelected = viewModel::onShortcutSelected,
            onShowLabelChanged = viewModel::onShowLabelChanged,
            onShowIconChanged = viewModel::onShowIconChanged,
            onLabelColorButtonClicked = viewModel::onLabelColorButtonClicked,
            onIconScaleChanged = viewModel::onIconScaleChanged,
            onBackgroundSelected = viewModel::onBackgroundSelected,
            onBackgroundImagePicked = viewModel::onBackgroundImagePicked,
            onTitleSizeChanged = viewModel::onTitleSizeChanged,
            onTitleColorChanged = viewModel::onTitleColorChanged,
            onTitleFontWeightChanged = viewModel::onTitleFontWeightChanged,
            onTitleSourceChanged = viewModel::onTitleSourceChanged,
            onTitleTextChanged = viewModel::onTitleTextChanged,
            onSubtitleSourceChanged = viewModel::onSubtitleSourceChanged,
            onSubtitleTextChanged = viewModel::onSubtitleTextChanged,
            onSubtitleSizeChanged = viewModel::onSubtitleSizeChanged,
            onSubtitleColorChanged = viewModel::onSubtitleColorChanged,
            onSubtitleFontWeightChanged = viewModel::onSubtitleFontWeightChanged,
            onPaddingChanged = viewModel::onPaddingChanged,
        )
    }

    if (state?.colorDialogVisible == true && state.colorDialogTarget == ColorTarget.LABEL) {
        ColorPickerDialog(
            initialColor = state.labelColor,
            onColorSelected = viewModel::onLabelColorSelected,
            onDismissRequested = viewModel::onDialogDismissalRequested,
        )
    }
}
