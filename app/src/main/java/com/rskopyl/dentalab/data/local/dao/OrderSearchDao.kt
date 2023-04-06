package com.rskopyl.dentalab.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.rskopyl.dentalab.data.model.Order
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderSearchDao {

    @Query("SELECT * FROM `order`" +
           "WHERE patient LIKE '%' || :name || '%' ")
    fun getByPatient(name: String): Flow<List<Order>>

    @Query("SELECT * FROM `order`" +
           "WHERE customer_clinic LIKE '%' || :name || '%' ")
    fun getByClinic(name: String): Flow<List<Order>>

    @Query("SELECT * FROM `order`" +
           "WHERE customer_doctor LIKE '%' || :name || '%' ")
    fun getByDoctor(name: String): Flow<List<Order>>
}