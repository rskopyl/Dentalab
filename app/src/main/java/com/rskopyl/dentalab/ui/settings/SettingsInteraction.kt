package com.rskopyl.dentalab.ui.settings

import com.rskopyl.dentalab.data.model.Hint
import com.rskopyl.dentalab.data.preferences.AppPreferences

data class SettingsState(
    val preferences: AppPreferences? = null,
    val structureNames: List<String> = emptyList(),
    val materialNames: List<String> = emptyList(),
    val clinicHints: List<String> = emptyList(),
    val doctorHints: List<String> = emptyList()
)

sealed interface SettingsEvent {

    data class NavigateToStructureScreen(val structureId: Long) : SettingsEvent
    data class NavigateToMaterialScreen(val materialId: Long) : SettingsEvent

    data class NavigateToHintScreen(
        val target: Hint.Target, val text: String?
    ) : SettingsEvent
}

sealed interface SettingsIntent {

    data class ShowHintDetails(
        val hintTarget: Hint.Target, val hintText: String
    ) : SettingsIntent

    data class ShowStructureDetails(val structureName: String) : SettingsIntent
    data class ShowMaterialDetails(val materialName: String) : SettingsIntent

    data class ChangeHomePeriod(
        val period: AppPreferences.HomePeriod
    ) : SettingsIntent

    data class ChangeQuickNavigation(val isEnabled: Boolean) : SettingsIntent
    data class CreateHint(val hintTarget: Hint.Target) : SettingsIntent

    object Load : SettingsIntent
    object CreateStructure : SettingsIntent
    object CreateMaterial : SettingsIntent
}