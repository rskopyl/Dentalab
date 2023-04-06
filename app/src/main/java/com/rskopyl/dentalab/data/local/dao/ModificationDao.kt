package com.rskopyl.dentalab.data.local.dao

import androidx.room.*
import com.rskopyl.dentalab.data.model.Modification
import kotlinx.coroutines.flow.Flow

@Dao
interface ModificationDao {

    @Query("SELECT * FROM modification WHERE order_id = :orderId")
    fun getByOrderId(orderId: Long): Flow<List<Modification>>

    @Query("SELECT * FROM modification " +
           "WHERE order_id = :orderId AND position = :position")
    fun getByOrderIdAndPosition(
        orderId: Long, position: Int
    ): Flow<Modification?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(modifications: List<Modification>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(modification: Modification)

    @Query("DELETE FROM modification WHERE order_id = :orderId")
    suspend fun deleteByOrderId(orderId: Long)

    @Query("DELETE FROM modification " +
           "WHERE order_id = :orderId AND position = :position")
    suspend fun deleteByOrderIdAndPosition(orderId: Long, position: Int)
}