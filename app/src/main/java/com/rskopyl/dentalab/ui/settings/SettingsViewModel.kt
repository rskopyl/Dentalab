package com.rskopyl.dentalab.ui.settings

import androidx.lifecycle.viewModelScope
import com.rskopyl.dentalab.data.model.Hint
import com.rskopyl.dentalab.data.model.Material
import com.rskopyl.dentalab.data.model.Structure
import com.rskopyl.dentalab.data.preferences.AppPreferences
import com.rskopyl.dentalab.data.preferences.AppPreferencesManager
import com.rskopyl.dentalab.repository.HintRepository
import com.rskopyl.dentalab.repository.MaterialRepository
import com.rskopyl.dentalab.repository.StructureRepository
import com.rskopyl.dentalab.util.IntentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appPreferencesManager: AppPreferencesManager,
    private val structureRepository: StructureRepository,
    private val materialRepository: MaterialRepository,
    private val hintRepository: HintRepository
) : IntentViewModel<SettingsState, SettingsEvent, SettingsIntent>(
    initialState = SettingsState()
) {
    init {
        intents.trySend(SettingsIntent.Load)
    }

    override suspend fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.ShowHintDetails ->
                showHintDetails(intent.hintTarget, intent.hintText)
            is SettingsIntent.ShowStructureDetails ->
                showStructureDetails(intent.structureName)
            is SettingsIntent.ShowMaterialDetails ->
                showMaterialDetails(intent.materialName)
            is SettingsIntent.ChangeHomePeriod ->
                changeHomePeriod(intent.period)
            is SettingsIntent.ChangeQuickNavigation ->
                changeQuickNavigation(intent.isEnabled)
            is SettingsIntent.CreateHint ->
                createHint(intent.hintTarget)
            SettingsIntent.Load -> load()
            SettingsIntent.CreateMaterial -> createMaterial()
            SettingsIntent.CreateStructure -> createStructure()
        }
    }

    private fun load() {
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                appPreferencesManager.get().collectLatest { preferences ->
                    _state.update { state ->
                        state.copy(preferences = preferences)
                    }
                }
            }
            launch {
                structureRepository.getAll().collectLatest { structures ->
                    _state.update { state ->
                        state.copy(structureNames = structures.map { it.name })
                    }
                }
            }
            launch {
                materialRepository.getAll().collectLatest { materials ->
                    _state.update { state ->
                        state.copy(materialNames = materials.map { it.name })
                    }
                }
            }
            launch {
                hintRepository.getByTarget(Hint.Target.CLINIC)
                    .collectLatest { clinicHints ->
                        _state.update { state ->
                            state.copy(clinicHints = clinicHints.map { it.text })
                        }
                    }
            }
            launch {
                hintRepository.getByTarget(Hint.Target.DOCTOR)
                    .collectLatest { doctorHints ->
                        _state.update { state ->
                            state.copy(doctorHints = doctorHints.map { it.text })
                        }
                    }
            }
        }
    }

    private fun showHintDetails(hintTarget: Hint.Target, hintText: String) {
        _events.trySend(
            SettingsEvent.NavigateToHintScreen(hintTarget, hintText)
        )
    }

    private fun showStructureDetails(structureName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _events.trySend(
                SettingsEvent.NavigateToStructureScreen(
                    structureId = structureRepository
                        .getAll().first()
                        .first { it.name == structureName }
                        .id
                )
            )
        }
    }

    private fun showMaterialDetails(materialName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _events.trySend(
                SettingsEvent.NavigateToMaterialScreen(
                    materialId = materialRepository
                        .getAll().first()
                        .first { it.name == materialName }
                        .id
                )
            )
        }
    }

    private fun changeHomePeriod(period: AppPreferences.HomePeriod) {
        appPreferencesManager.updateHomePeriod(period)
    }

    private fun changeQuickNavigation(isEnabled: Boolean) {
        appPreferencesManager.updateQuickNavigation(isEnabled)
    }

    private fun createHint(target: Hint.Target) {
        _events.trySend(
            SettingsEvent.NavigateToHintScreen(
                target = target, text = null
            )
        )
    }

    private fun createStructure() {
        _events.trySend(
            SettingsEvent.NavigateToStructureScreen(
                structureId = Structure.EMPTY_ID
            )
        )
    }

    private fun createMaterial() {
        _events.trySend(
            SettingsEvent.NavigateToMaterialScreen(
                materialId = Material.EMPTY_ID
            )
        )
    }
}