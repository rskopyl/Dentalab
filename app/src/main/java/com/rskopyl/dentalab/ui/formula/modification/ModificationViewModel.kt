package com.rskopyl.dentalab.ui.formula.modification

import androidx.lifecycle.viewModelScope
import com.rskopyl.dentalab.data.model.Components
import com.rskopyl.dentalab.data.model.Material
import com.rskopyl.dentalab.data.model.Modification
import com.rskopyl.dentalab.data.model.Structure
import com.rskopyl.dentalab.repository.MaterialRepository
import com.rskopyl.dentalab.repository.ModificationRepository
import com.rskopyl.dentalab.repository.StructureRepository
import com.rskopyl.dentalab.util.IntentViewModel
import com.rskopyl.dentalab.util.ValidationException
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ModificationViewModel @AssistedInject constructor(
    @Assisted mode: ModificationState.Mode,
    structureRepository: StructureRepository,
    materialRepository: MaterialRepository,
    private val modificationRepository: ModificationRepository
) : IntentViewModel<ModificationState, ModificationEvent, ModificationIntent>(
    initialState = ModificationState(mode = mode)
) {
    private val structures: StateFlow<List<Structure>> = structureRepository
        .getAll()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val materials: StateFlow<List<Material>> = materialRepository
        .getAll()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        intents.trySend(ModificationIntent.Load(mode))
    }

    override suspend fun handleIntent(intent: ModificationIntent) {
        when (intent) {
            is ModificationIntent.Load -> load(intent.mode)
            is ModificationIntent.Apply -> apply(intent.input)
            is ModificationIntent.Save -> save(intent.input)
            ModificationIntent.Delete -> delete()
            ModificationIntent.Cancel ->
                _events.trySend(ModificationEvent.NavigateBack)
        }
    }

    private fun load(mode: ModificationState.Mode) {
        viewModelScope.launch(Dispatchers.IO) {
            val modification = modificationRepository
                .getByOrderIdAndPosition(
                    orderId = mode.orderId, position = mode.position
                )
                .firstOrNull()
            val input = if (modification == null) {
                ModificationInput()
            } else {
                ModificationInput(modification.second)
            }
            _events.trySend(ModificationEvent.ShowInput(input))
            if (modification != null) {
                _state.update { state ->
                    state.copy(
                        mode = ModificationState.Mode.Editing(
                            orderId = mode.orderId, position = mode.position
                        ),
                        modification = modification
                    )
                }
            }
            launch {
                structures.collectLatest { structures ->
                    _state.update { state ->
                        state.copy(
                            structureNames = structures.map { it.name }
                        )
                    }
                }
            }
            launch {
                materials.collectLatest { materials ->
                    _state.update { state ->
                        state.copy(
                            materialNames = materials.map { it.name }
                        )
                    }
                }
            }
        }
    }

    private fun apply(input: ModificationInput) {
        val structure = structures.value.firstOrNull {
            it.name.equals(
                input.structureName.trim(), ignoreCase = true
            )
        }
        val material =  materials.value.firstOrNull {
            it.name.equals(
                input.materialName.trim(), ignoreCase = true
            )
        }
        _state.update { state ->
            state.copy(
                modification = state.modification.copy(
                    first = state.modification.first.copy(
                        structureId = structure?.id ?: Structure.EMPTY_ID,
                        materialId = material?.id ?: Material.EMPTY_ID
                    ),
                    second = Components(structure, material)
                )
            )
        }
    }

    private fun save(input: ModificationInput) {
        val modification = try {
            val typedInput = ModificationTypedInput(
                input, structures.value, materials.value
            )
            Modification(
                orderId = _state.value.mode.orderId,
                position = _state.value.mode.position,
                structureId = typedInput.structureId,
                materialId = typedInput.materialId
            )
        } catch (e: ValidationException) {
            _state.update { state ->
                state.copy(violations = setOf(e.violation))
            }
            return
        }
        modificationRepository.insert(modification)
        _events.trySend(ModificationEvent.NavigateBack)
    }

    private fun delete() {
        modificationRepository.deleteByOrderIdAndPosition(
            orderId = _state.value.mode.orderId,
            position = _state.value.mode.position
        )
        _events.trySend(ModificationEvent.NavigateBack)
    }

    @AssistedFactory
    interface DaggerFactory {

        fun create(mode: ModificationState.Mode): ModificationViewModel
    }
}