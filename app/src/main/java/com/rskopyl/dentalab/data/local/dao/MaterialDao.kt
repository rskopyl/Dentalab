package com.rskopyl.dentalab.data.local.dao

import androidx.room.*
import com.rskopyl.dentalab.data.model.Material
import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialDao {

    @Query("SELECT * FROM material")
    fun getAll(): Flow<List<Material>>

    @Query("SELECT * FROM material WHERE id = :id")
    fun getById(id: Long): Flow<Material?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(material: Material)

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun update(material: Material)

    @Query("DELETE FROM material WHERE id = :id")
    suspend fun deleteById(id: Long)
}