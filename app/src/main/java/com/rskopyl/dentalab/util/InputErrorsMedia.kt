package com.rskopyl.dentalab.util

import com.google.android.material.textfield.TextInputLayout

interface InputErrorsMedia {

    val inputFieldToErrors: Map<TextInputLayout, Set<Any>>
    val inputErrorToText: Map<Any, String>

    fun showInputErrors(errors: Collection<Any>) {
        for ((field, fieldErrors) in inputFieldToErrors) {
            val priorError = fieldErrors.firstOrNull { it in errors }
            field.error = inputErrorToText[priorError]
        }
    }
}