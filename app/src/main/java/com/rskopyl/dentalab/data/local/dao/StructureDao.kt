package com.rskopyl.dentalab.data.local.dao

import androidx.room.*
import com.rskopyl.dentalab.data.model.Structure
import kotlinx.coroutines.flow.Flow

@Dao
interface StructureDao {

    @Query("SELECT * FROM structure WHERE id = :id")
    fun getById(id: Long): Flow<Structure?>

    @Query("SELECT * FROM structure")
    fun getAll(): Flow<List<Structure>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(structure: Structure)

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun update(structure: Structure)

    @Query("DELETE FROM structure WHERE id = :id")
    suspend fun deleteById(id: Long)
}