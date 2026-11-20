package ch.rmy.android.http_shortcuts.activities.variablewidget

import android.app.Application
import android.graphics.Color
import android.net.Uri
import ch.rmy.android.framework.viewmodel.BaseViewModel
import ch.rmy.android.http_shortcuts.activities.variablewidget.models.SelectableShortcut
import ch.rmy.android.http_shortcuts.activities.variablewidget.models.SelectableVariable
import ch.rmy.android.http_shortcuts.data.domains.shortcuts.ShortcutId
import ch.rmy.android.http_shortcuts.data.domains.shortcuts.ShortcutRepository
import ch.rmy.android.http_shortcuts.data.domains.variables.GlobalVariableId
import ch.rmy.android.http_shortcuts.data.domains.variables.GlobalVariableRepository
import ch.rmy.android.http_shortcuts.data.domains.widgets.VariableWidgetsRepository
import ch.rmy.android.http_shortcuts.data.enums.FontWeight
import ch.rmy.android.http_shortcuts.data.enums.PaddingType
import ch.rmy.android.http_shortcuts.data.enums.SubtitlePosition
import ch.rmy.android.http_shortcuts.data.enums.VariableType
import ch.rmy.android.http_shortcuts.data.enums.WidgetBackgroundType
import ch.rmy.android.http_shortcuts.data.models.GlobalVariable
import ch.rmy.android.http_shortcuts.navigation.NavigationDestination
import ch.rmy.android.http_shortcuts.widget.WidgetConstants
import ch.rmy.android.http_shortcuts.widget.WidgetImageUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VariableWidgetSettingsViewModel
@Inject
constructor(
    application: Application,
    private val globalVariableRepository: GlobalVariableRepository,
    private val shortcutRepository: ShortcutRepository,
    private val variableWidgetsRepository: VariableWidgetsRepository,
) : BaseViewModel<VariableWidgetSettingsViewModel.InitData, VariableWidgetSettingsViewState>(application) {

    private lateinit var constants: List<GlobalVariable>

    override suspend fun initialize(data: InitData): VariableWidgetSettingsViewState {
        val widget = data.widgetId
            ?.let {
                variableWidgetsRepository.getVariableWidgetById(it)
            }

        constants = globalVariableRepository.getGlobalVariables()
            .filter { it.type == VariableType.CONSTANT }
        val selectableVariables = constants.map { it.toSelectableVariable() }
        val selectedVariable = widget
            ?.let { constants.find { it.id == widget.variableId } }
            ?: constants.firstOrNull()

        val selectableShortcuts = shortcutRepository.getShortcuts()
            .map { shortcut ->
                SelectableShortcut(shortcutId = shortcut.id, name = shortcut.name)
            }

        return VariableWidgetSettingsViewState(
            selectableVariables = selectableVariables,
            selectedVariable = selectedVariable?.toSelectableVariable(),
            variableValue = selectedVariable?.value,
            fontSize = widget?.fontSize ?: 22,
            title = widget?.title ?: "",
            background = widget?.background ?: WidgetBackgroundType.Color(WidgetConstants.DEFAULT_BACKGROUND_COLOR),
            shortcutId = widget?.shortcutId,
            selectableShortcuts = selectableShortcuts,
            titleSize = widget?.titleSize ?: WidgetConstants.DEFAULT_TITLE_SIZE,
            titleColor = widget?.titleColor?.let { runCatching { Color.parseColor(it) }.getOrNull() } ?: WidgetConstants.DEFAULT_TITLE_COLOR,
            titleFontWeight = widget?.titleFontWeight?.let { FontWeight.fromWeight(it) } ?: WidgetConstants.DEFAULT_TITLE_FONT_WEIGHT,
            valueSize = widget?.valueSize ?: WidgetConstants.DEFAULT_VALUE_SIZE,
            valueColor = widget?.valueColor?.let { runCatching { Color.parseColor(it) }.getOrNull() } ?: WidgetConstants.DEFAULT_VALUE_COLOR,
            valueFontWeight = widget?.valueFontWeight?.let { FontWeight.fromWeight(it) } ?: WidgetConstants.DEFAULT_VALUE_FONT_WEIGHT,
            unit = widget?.unit ?: "",
            unitSize = widget?.unitSize ?: WidgetConstants.DEFAULT_UNIT_SIZE,
            unitColor = widget?.unitColor?.let { runCatching { Color.parseColor(it) }.getOrNull() } ?: WidgetConstants.DEFAULT_UNIT_COLOR,
            unitFontWeight = widget?.unitFontWeight?.let { FontWeight.fromWeight(it) } ?: WidgetConstants.DEFAULT_UNIT_FONT_WEIGHT,
            subtitleText = widget?.subtitleText ?: "",
            subtitlePosition = widget?.subtitlePosition ?: SubtitlePosition.DEFAULT,
            subtitleSize = widget?.subtitleSize ?: WidgetConstants.DEFAULT_SUBTITLE_SIZE,
            subtitleColor = widget?.subtitleColor?.let { runCatching { Color.parseColor(it) }.getOrNull() } ?: WidgetConstants.DEFAULT_SUBTITLE_COLOR,
            subtitleFontWeight = widget?.subtitleFontWeight?.let { FontWeight.fromWeight(it) } ?: WidgetConstants.DEFAULT_SUBTITLE_FONT_WEIGHT,
            padding = widget?.padding ?: PaddingType.DEFAULT,
        )
    }

    private fun GlobalVariable.toSelectableVariable() =
        SelectableVariable(
            variableId = id,
            variableKey = key,
        )

    fun onVariableSelected(variableId: GlobalVariableId?) = runAction {
        updateViewState {
            copy(
                selectedVariable = selectableVariables.find { it.variableId == variableId },
                variableValue = constants.find { it.id == variableId }?.value,
            )
        }
    }

    fun onFontSizeChanged(fontSize: Int) = runAction {
        updateViewState {
            copy(fontSize = fontSize)
        }
    }

    fun onTitleChanged(title: String) = runAction {
        updateViewState {
            copy(title = title)
        }
    }

    fun onBackgroundChanged(background: WidgetBackgroundType?) = runAction {
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

    fun onShortcutSelected(shortcutId: ShortcutId?) = runAction {
        updateViewState {
            copy(shortcutId = shortcutId)
        }
    }

    fun onTitleSizeChanged(size: Float) = runAction {
        updateViewState {
            copy(titleSize = size)
        }
    }

    fun onTitleFontWeightChanged(weight: FontWeight) = runAction {
        updateViewState {
            copy(titleFontWeight = weight)
        }
    }

    fun onTitleColorChanged(color: Int) = runAction {
        updateViewState {
            copy(titleColor = color)
        }
    }

    fun onValueSizeChanged(size: Float) = runAction {
        updateViewState {
            copy(valueSize = size)
        }
    }

    fun onValueFontWeightChanged(weight: FontWeight) = runAction {
        updateViewState {
            copy(valueFontWeight = weight)
        }
    }

    fun onValueColorChanged(color: Int) = runAction {
        updateViewState {
            copy(valueColor = color)
        }
    }

    fun onUnitChanged(unit: String) = runAction {
        updateViewState {
            copy(unit = unit)
        }
    }

    fun onUnitSizeChanged(size: Float) = runAction {
        updateViewState {
            copy(unitSize = size)
        }
    }

    fun onUnitFontWeightChanged(weight: FontWeight) = runAction {
        updateViewState {
            copy(unitFontWeight = weight)
        }
    }

    fun onUnitColorChanged(color: Int) = runAction {
        updateViewState {
            copy(unitColor = color)
        }
    }

    fun onSubtitleTextChanged(text: String) = runAction {
        updateViewState {
            copy(subtitleText = text)
        }
    }

    fun onSubtitlePositionChanged(position: SubtitlePosition) = runAction {
        updateViewState {
            copy(subtitlePosition = position)
        }
    }

    fun onSubtitleSizeChanged(size: Float) = runAction {
        updateViewState {
            copy(subtitleSize = size)
        }
    }

    fun onSubtitleFontWeightChanged(weight: FontWeight) = runAction {
        updateViewState {
            copy(subtitleFontWeight = weight)
        }
    }

    fun onSubtitleColorChanged(color: Int) = runAction {
        updateViewState {
            copy(subtitleColor = color)
        }
    }

    fun onPaddingChanged(padding: PaddingType) = runAction {
        updateViewState {
            copy(padding = padding)
        }
    }

    fun onSubmitButtonClicked() = runAction {
        val variableId = viewState.selectedVariable?.variableId ?: skipAction()
        closeScreen(
            result = NavigationDestination.VariableWidget.Result(
                variableId = variableId,
                fontSize = viewState.fontSize,
                title = viewState.title,
                background = viewState.background,
                shortcutId = viewState.shortcutId,
                titleSize = viewState.titleSize,
                titleColor = viewState.titleColorFormatted,
                titleFontWeight = viewState.titleFontWeight.weight,
                valueSize = viewState.valueSize,
                valueColor = viewState.valueColorFormatted,
                valueFontWeight = viewState.valueFontWeight.weight,
                unit = viewState.unit,
                unitSize = viewState.unitSize,
                unitColor = viewState.unitColorFormatted,
                unitFontWeight = viewState.unitFontWeight.weight,
                subtitleText = viewState.subtitleText,
                subtitlePosition = viewState.subtitlePosition,
                subtitleSize = viewState.subtitleSize,
                subtitleColor = viewState.subtitleColorFormatted,
                subtitleFontWeight = viewState.subtitleFontWeight.weight,
                padding = viewState.padding,
            ),
        )
    }

    fun onBackPressed() = runAction {
        closeScreen(
            result = NavigationDestination.VariableWidget.RESULT_VARIABLE_WIDGET_SETTINGS_CANCELLED,
        )
    }

    data class InitData(
        val widgetId: Int?,
    )
}
