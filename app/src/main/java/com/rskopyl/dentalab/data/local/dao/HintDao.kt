package com.rskopyl.dentalab.data.local.dao

import androidx.room.*
import com.rskopyl.dentalab.data.model.Hint
import kotlinx.coroutines.flow.Flow

@Dao
interface HintDao {

    @Query("SELECT * FROM hint")
    fun getAll(): Flow<List<Hint>>

    @Query("SELECT * FROM hint WHERE target = :target")
    fun getByTarget(target: Hint.Target): Flow<List<Hint>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(hint: Hint)

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun update(hint: Hint)

    @Delete
    suspend fun delete(hint: Hint)
}