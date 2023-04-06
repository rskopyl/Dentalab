package com.rskopyl.dentalab.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rskopyl.dentalab.util.validate
import kotlinx.datetime.LocalDateTime

@Entity(tableName = "order")
data class Order(
    @ColumnInfo(name = "patient")
    val patient: String,
    @Embedded(prefix = "customer_")
    val customer: Customer,
    @ColumnInfo(name = "date_time")
    val dateTime: LocalDateTime,
    @Embedded(prefix = "payment_")
    val payment: Payment,
    @ColumnInfo(name = "note")
    val note: String,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = EMPTY_ID
) {
    init {
        validate(patient.isNotBlank()) {
            Violation.BLANK_PATIENT
        }
        validate(customer.clinic.isNotBlank()) {
            Violation.BLANK_CLINIC
        }
    }

    companion object {

        const val EMPTY_ID = 0L
    }

    data class Customer(
        @ColumnInfo(name = "clinic")
        val clinic: String,
        @ColumnInfo(name = "doctor")
        val doctor: String?
    )

    data class Payment(
        @ColumnInfo(name = "price")
        val price: Double?,
        @ColumnInfo(name = "is_done")
        val isDone: Boolean
    )

    enum class Violation {
        BLANK_PATIENT, BLANK_CLINIC
    }
}