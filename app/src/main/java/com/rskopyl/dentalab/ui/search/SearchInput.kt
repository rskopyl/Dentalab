package com.rskopyl.dentalab.ui.search

data class SearchInput(
    val text: String = "",
    val filter: Filter = Filter.PATIENT
) {
    enum class Filter { PATIENT, CLINIC, DOCTOR }
}
