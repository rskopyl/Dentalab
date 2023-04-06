package com.rskopyl.dentalab.repository.impl

import com.rskopyl.dentalab.data.local.dao.OrderSearchDao
import com.rskopyl.dentalab.data.model.Order
import com.rskopyl.dentalab.repository.OrderSearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OrderSearchRepositoryImpl @Inject constructor(
    private val orderSearchDao: OrderSearchDao
) : OrderSearchRepository {

    override fun getByPatient(patient: String): Flow<List<Order>> =
        orderSearchDao.getByPatient(patient)

    override fun getByClinic(clinic: String): Flow<List<Order>> =
        orderSearchDao.getByClinic(clinic)

    override fun getByDoctor(doctor: String): Flow<List<Order>> =
        orderSearchDao.getByDoctor(doctor)
}