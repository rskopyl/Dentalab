package com.rskopyl.dentalab.repository.impl

import com.rskopyl.dentalab.data.local.dao.OrderDao
import com.rskopyl.dentalab.data.model.Order
import com.rskopyl.dentalab.di.ApplicationScope
import com.rskopyl.dentalab.repository.OrderRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val orderDao: OrderDao,
    @ApplicationScope
    private val applicationScope: CoroutineScope
) : OrderRepository {

    override fun getByDate(date: LocalDate): Flow<List<Order>> =
        orderDao.getByDate(date)

    override fun getById(id: Long): Flow<Order?> =
        orderDao.getById(id)

    override fun insert(order: Order): Deferred<Long> {
        val id = CompletableDeferred<Long>()
        applicationScope.launch(Dispatchers.IO) {
            id.complete(orderDao.insert(order))
        }
        return id
    }

    override fun update(order: Order) {
        applicationScope.launch(Dispatchers.IO) {
            orderDao.update(order)
        }
    }

    override fun deleteById(id: Long) {
        applicationScope.launch(Dispatchers.IO) {
            orderDao.deleteById(id)
        }
    }
}