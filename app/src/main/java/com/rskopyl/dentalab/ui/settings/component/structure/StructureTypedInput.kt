package com.rskopyl.dentalab.ui.settings.component.structure

import com.rskopyl.dentalab.data.model.Structure

data class StructureTypedInput(
    val name: String = "",
    val symbol: String = "",
) {
    constructor(structure: Structure) : this(
        name = structure.name,
        symbol = structure.symbol
    )
}