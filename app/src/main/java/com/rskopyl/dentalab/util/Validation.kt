package com.rskopyl.dentalab.util

class ValidationException(val violation: Any) : RuntimeException()

fun validate(value: Boolean, lazyViolation: () -> Any) {
    if (!value) {
        val violation = lazyViolation()
        throw ValidationException(violation)
    }
}

fun <T> validateNotNull(value: T?, lazyViolation: () -> Any): T {
    if (value == null) {
        val violation = lazyViolation()
        throw ValidationException(violation)
    } else {
        return value
    }
}