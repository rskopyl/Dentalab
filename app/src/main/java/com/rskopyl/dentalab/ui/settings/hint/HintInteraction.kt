package com.rskopyl.dentalab.ui.settings.hint

import com.rskopyl.dentalab.data.model.Hint

data class HintState(
    val mode: Mode,
    val target: Hint.Target,
    val violations: Set<Any> = emptySet()
) {
    sealed interface Mode {

        object Creating : Mode
        data class Editing(val text: String) : Mode
    }
}

sealed interface HintEvent {

    data class ShowInput(val input: HintTypedInput) : HintEvent
    object NavigateBack : HintEvent
}

sealed interface HintIntent {

    data class Load(val mode: HintState.Mode) : HintIntent
    data class Save(val input: HintTypedInput) : HintIntent
    object Delete : HintIntent
    object Cancel : HintIntent
}