package com.rskopyl.dentalab.ui.formula.modification

import com.rskopyl.dentalab.data.model.Components
import com.rskopyl.dentalab.data.model.Material
import com.rskopyl.dentalab.data.model.Structure
import com.rskopyl.dentalab.util.validateNotNull

data class ModificationInput(
    val structureName: String = "",
    val materialName: String = ""
) {
    constructor(components: Components) : this(
        structureName = components.structure?.name ?: "",
        materialName = components.material?.name ?: ""
    )
}

data class ModificationTypedInput(
    val structureId: Long,
    val materialId: Long
) {
    constructor(
        input: ModificationInput,
        structures: List<Structure>,
        materials: List<Material>
    ) : this(
        structureId = validateNotNull(
            structures.singleOrNull {
                it.name.equals(input.structureName, ignoreCase = true)
            }?.id
        ) {
            Violation.INVALID_STRUCTURE
        },
        materialId = validateNotNull(
            materials.singleOrNull {
                it.name.equals(input.materialName, ignoreCase = true)
            }?.id
        ) {
            Violation.INVALID_MATERIAL
        }
    )

    enum class Violation {
        INVALID_STRUCTURE, INVALID_MATERIAL
    }
}