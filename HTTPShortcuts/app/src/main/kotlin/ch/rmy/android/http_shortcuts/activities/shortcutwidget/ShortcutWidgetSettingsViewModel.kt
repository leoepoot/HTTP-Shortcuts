package ch.rmy.android.http_shortcuts.activities.shortcutwidget

import android.app.Application
import android.graphics.Color
import android.net.Uri
import ch.rmy.android.framework.viewmodel.BaseViewModel
import ch.rmy.android.http_shortcuts.activities.variablewidget.models.SelectableShortcut
import ch.rmy.android.http_shortcuts.data.domains.shortcuts.ShortcutId
import ch.rmy.android.http_shortcuts.data.domains.shortcuts.ShortcutRepository
import ch.rmy.android.http_shortcuts.data.domains.widgets.ShortcutWidgetsRepository
import ch.rmy.android.http_shortcuts.data.enums.PaddingType
import ch.rmy.android.http_shortcuts.data.enums.WidgetBackgroundType
import ch.rmy.android.http_shortcuts.data.enums.WidgetTextSource
import ch.rmy.android.http_shortcuts.data.models.Shortcut
import ch.rmy.android.http_shortcuts.extensions.labelColorInt
import ch.rmy.android.http_shortcuts.navigation.NavigationDestination
import ch.rmy.android.http_shortcuts.widget.WidgetConstants
import ch.rmy.android.http_shortcuts.widget.WidgetImageUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ShortcutWidgetSettingsViewModel
@Inject
constructor(
    application: Application,
    private val shortcutWidgetsRepository: ShortcutWidgetsRepository,
    private val shortcutRepository: ShortcutRepository,
) : BaseViewModel<ShortcutWidgetSettingsViewModel.InitData, ShortcutWidgetSettingsViewState>(application) {

    private lateinit var shortcuts: List<Shortcut>

    override suspend fun initialize(data: InitData): ShortcutWidgetSettingsViewState {
        val widget = data.widgetId
            ?.let {
                shortcutWidgetsRepository.getShortcutWidgetById(it)
            }

        shortcuts = shortcutRepository.getShortcuts()
        val selectableShortcuts = shortcuts.map { it.toSelectableShortcut() }
        val selectedShortcutId = widget
            ?.let { widgetShortcut ->
                shortcuts.find { it.id == widgetShortcut.shortcutId }?.id
            }
            ?: shortcuts.firstOrNull()?.id

        return ShortcutWidgetSettingsViewState(
            showLabel = widget?.showLabel != false,
            showIcon = widget?.showIcon != false,
            labelColor = widget?.labelColorInt() ?: Color.WHITE,
            iconScale = widget?.iconScale ?: 1f,
            selectableShortcuts = selectableShortcuts,
            selectedShortcutId = selectedShortcutId,
            background = widget?.background,
            titleSize = widget?.titleSize?.toInt() ?: WidgetConstants.DEFAULT_TITLE_SIZE.toInt(),
            titleColor = widget?.titleColor?.let { runCatching { Color.parseColor(it) }.getOrNull() } ?: WidgetConstants.DEFAULT_WIDGET_TEXT_COLOR,
            titleSource = widget?.titleSource ?: WidgetTextSource.SHORTCUT_NAME,
            titleText = widget?.titleText ?: "",
            subtitleSource = widget?.subtitleSource ?: WidgetTextSource.SHORTCUT_DESCRIPTION,
            subtitleText = widget?.subtitleText ?: "",
            padding = widget?.padding ?: PaddingType.DEFAULT,
        )
    }

    private fun Shortcut.toSelectableShortcut() =
        SelectableShortcut(
            shortcutId = id,
            name = name,
            description = description,
        )

    fun onShortcutSelected(shortcutId: ShortcutId?) = runAction {
        updateViewState {
            copy(selectedShortcutId = shortcutId)
        }
    }

    fun onLabelColorButtonClicked() = runAction {
        updateViewState {
            copy(colorDialogVisible = true, colorDialogTarget = ColorTarget.LABEL)
        }
    }

    fun onShowLabelChanged(enabled: Boolean) = runAction {
        updateViewState {
            copy(showLabel = enabled)
        }
    }

    fun onShowIconChanged(enabled: Boolean) = runAction {
        updateViewState {
            copy(showIcon = enabled)
        }
    }

    fun onLabelColorSelected(color: Int) = runAction {
        updateViewState {
            copy(
                colorDialogVisible = false,
                labelColor = color,
                colorDialogTarget = ColorTarget.NONE,
            )
        }
    }

    fun onIconScaleChanged(scale: Float) = runAction {
        updateViewState {
            copy(iconScale = scale)
        }
    }

    fun onBackgroundSelected(background: WidgetBackgroundType?) = runAction {
        cleanupOldImageIfNeeded(viewState.background, background)
        updateViewState {
            copy(background = background)
        }
    }

    fun onBackgroundImagePicked(uri: Uri) = runAction {
        val fileName = WidgetImageUtil.saveImageFromUri(
            context = getApplication(),
            imageUri = uri,
        ) ?: skipAction()
        val newBackground = WidgetBackgroundType.Image(fileName)
        cleanupOldImageIfNeeded(viewState.background, newBackground)
        updateViewState {
            copy(background = newBackground)
        }
    }

    private suspend fun cleanupOldImageIfNeeded(oldBackground: WidgetBackgroundType?, newBackground: WidgetBackgroundType?) {
        val oldFile = (oldBackground as? WidgetBackgroundType.Image)?.fileName ?: return
        val newFile = (newBackground as? WidgetBackgroundType.Image)?.fileName
        if (oldFile != newFile) {
            WidgetImageUtil.deleteImage(getApplication(), oldFile)
        }
    }

    fun onTitleSizeChanged(size: Int) = runAction {
        updateViewState {
            copy(titleSize = size)
        }
    }

    fun onTitleColorButtonClicked() = runAction {
        updateViewState {
            copy(colorDialogVisible = true, colorDialogTarget = ColorTarget.TITLE)
        }
    }

    fun onTitleColorSelected(color: Int) = runAction {
        updateViewState {
            copy(
                colorDialogVisible = false,
                titleColor = color,
                colorDialogTarget = ColorTarget.NONE,
            )
        }
    }

    fun onTitleSourceChanged(source: WidgetTextSource) = runAction {
        updateViewState {
            copy(titleSource = source)
        }
    }

    fun onTitleTextChanged(text: String) = runAction {
        updateViewState {
            copy(titleText = text)
        }
    }

    fun onSubtitleSourceChanged(source: WidgetTextSource) = runAction {
        updateViewState {
            copy(subtitleSource = source)
        }
    }

    fun onSubtitleTextChanged(text: String) = runAction {
        updateViewState {
            copy(subtitleText = text)
        }
    }

    fun onPaddingChanged(padding: PaddingType) = runAction {
        updateViewState {
            copy(padding = padding)
        }
    }

    fun onCreateButtonClicked() = runAction {
        val shortcutId = viewState.selectedShortcutId ?: skipAction()
        closeScreen(
            result = NavigationDestination.ShortcutWidget.Result(
                shortcutId = shortcutId,
                labelColor = viewState.labelColorFormatted,
                showLabel = viewState.showLabel,
                showIcon = viewState.showIcon,
                iconScale = viewState.iconScale,
                background = viewState.background,
                titleSize = viewState.titleSize,
                titleColor = viewState.titleColorFormatted,
                titleSource = viewState.titleSource,
                titleText = viewState.titleText,
                subtitleSource = viewState.subtitleSource,
                subtitleText = viewState.subtitleText,
                padding = viewState.padding,
            ),
        )
    }

    fun onDialogDismissalRequested() = runAction {
        updateViewState {
            copy(colorDialogVisible = false, colorDialogTarget = ColorTarget.NONE)
        }
    }

    fun onBackPressed() = runAction {
        closeScreen(result = NavigationDestination.ShortcutWidget.RESULT_SHORTCUT_WIDGET_SETTINGS_CANCELLED)
    }

    data class InitData(
        val widgetId: Int?,
    )
}
