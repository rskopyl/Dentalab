package com.rskopyl.dentalab.ui.order

import com.rskopyl.dentalab.data.model.Order
import com.rskopyl.dentalab.util.validateNotNull
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

data class OrderInput(
    val patient: String = "",
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val customerClinic: String = "",
    val customerDoctor: String = "",
    val paymentPrice: String = "",
    val paymentIsDone: Boolean = false,
    val note: String = ""
) {
    constructor(order: Order) : this(
        patient = order.patient,
        date = order.dateTime.date,
        time = order.dateTime.time,
        customerClinic = order.customer.clinic,
        customerDoctor = order.customer.doctor ?: "",
        paymentPrice = order.payment.price?.toString() ?: "",
        paymentIsDone = order.payment.isDone,
        note = order.note
    )
}

data class OrderTypedInput(
    val patient: String,
    val dateTime: LocalDateTime,
    val customerClinic: String,
    val customerDoctor: String,
    val paymentPrice: Double?,
    val paymentIsDone: Boolean,
    val note: String
) {
    constructor(input: OrderInput) : this(
        patient = input.patient,
        dateTime = LocalDateTime(
            date = validateNotNull(input.date) {
                Violation.EMPTY_DATE
            },
            time = validateNotNull(input.time) {
                Violation.EMPTY_TIME
            }
        ),
        customerClinic = input.customerClinic,
        customerDoctor = input.customerDoctor,
        paymentPrice = validateNotNull(
            input.paymentPrice.toDoubleOrNull()
        ) {
            Violation.INVALID_PRICE
        },
        paymentIsDone = input.paymentIsDone,
        note = input.note
    )

    enum class Violation {
        EMPTY_DATE, EMPTY_TIME, INVALID_PRICE
    }
}