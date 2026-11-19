package ch.rmy.android.http_shortcuts.activities.variablewidget

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ch.rmy.android.http_shortcuts.R
import ch.rmy.android.http_shortcuts.components.BackButton
import ch.rmy.android.http_shortcuts.components.SimpleScaffold
import ch.rmy.android.http_shortcuts.components.ToolbarIcon
import ch.rmy.android.http_shortcuts.components.bindViewModel

@Composable
fun VariableWidgetSettingsScreen(
    widgetId: Int?,
) {
    val (viewModel, state) = bindViewModel<
        VariableWidgetSettingsViewModel.InitData,
        VariableWidgetSettingsViewState,
        VariableWidgetSettingsViewModel,
        >(
        VariableWidgetSettingsViewModel.InitData(widgetId),
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
                enabled = state?.isSaveEnabled == true,
                contentDescription = stringResource(R.string.action_create_widget),
                onClick = viewModel::onSubmitButtonClicked,
            )
        },
    ) { viewState ->
        VariableWidgetSettingsContent(
            viewState = viewState,
            onVariableSelected = viewModel::onVariableSelected,
            onTitleChanged = viewModel::onTitleChanged,
            onBackgroundChanged = viewModel::onBackgroundChanged,
            onBackgroundImagePicked = viewModel::onBackgroundImagePicked,
            onShortcutSelected = viewModel::onShortcutSelected,
            onTitleSizeChanged = viewModel::onTitleSizeChanged,
            onTitleColorChanged = viewModel::onTitleColorChanged,
            onTitleFontWeightChanged = viewModel::onTitleFontWeightChanged,
            onValueSizeChanged = viewModel::onValueSizeChanged,
            onValueColorChanged = viewModel::onValueColorChanged,
            onValueFontWeightChanged = viewModel::onValueFontWeightChanged,
            onUnitChanged = viewModel::onUnitChanged,
            onUnitSizeChanged = viewModel::onUnitSizeChanged,
            onUnitColorChanged = viewModel::onUnitColorChanged,
            onUnitFontWeightChanged = viewModel::onUnitFontWeightChanged,
            onSubtitleTextChanged = viewModel::onSubtitleTextChanged,
            onSubtitlePositionChanged = viewModel::onSubtitlePositionChanged,
            onSubtitleSizeChanged = viewModel::onSubtitleSizeChanged,
            onSubtitleColorChanged = viewModel::onSubtitleColorChanged,
            onSubtitleFontWeightChanged = viewModel::onSubtitleFontWeightChanged,
            onPaddingChanged = viewModel::onPaddingChanged,
        )
    }
}
