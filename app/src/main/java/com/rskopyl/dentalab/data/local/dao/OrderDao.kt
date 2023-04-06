package com.rskopyl.dentalab.data.local.dao

import androidx.room.*
import com.rskopyl.dentalab.data.model.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface OrderDao {

    @Query("SELECT * FROM `order` WHERE id = :id")
    fun getById(id: Long): Flow<Order?>

    @Query("SELECT * FROM `order` WHERE date(date_time) == :date")
    fun getByDate(date: LocalDate): Flow<List<Order>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(order: Order): Long

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun update(order: Order)

    @Query("DELETE FROM `order` WHERE id = :id")
    suspend fun deleteById(id: Long)
}