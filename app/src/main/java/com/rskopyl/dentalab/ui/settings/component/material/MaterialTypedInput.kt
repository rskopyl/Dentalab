package com.rskopyl.dentalab.ui.settings.component.material

import com.rskopyl.dentalab.data.model.Material

data class MaterialTypedInput(
    val name: String = "",
    val color: String = ""
) {
    constructor(material: Material) : this(
        name = material.name,
        color = material.color
    )
}