package com.rskopyl.dentalab.repository

import com.rskopyl.dentalab.data.model.Order
import kotlinx.coroutines.flow.Flow

interface OrderSearchRepository {

    fun getByPatient(patient: String): Flow<List<Order>>

    fun getByClinic(clinic: String): Flow<List<Order>>

    fun getByDoctor(doctor: String): Flow<List<Order>>
}