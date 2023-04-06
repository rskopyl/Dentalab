package com.rskopyl.dentalab.repository

import com.rskopyl.dentalab.data.model.Order
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface OrderRepository {

    fun getByDate(date: LocalDate): Flow<List<Order>>

    fun getById(id: Long): Flow<Order?>

    fun insert(order: Order): Deferred<Long>

    fun update(order: Order)

    fun deleteById(id: Long)
}