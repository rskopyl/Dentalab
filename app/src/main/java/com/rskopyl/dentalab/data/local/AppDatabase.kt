package com.rskopyl.dentalab.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rskopyl.dentalab.data.local.dao.*
import com.rskopyl.dentalab.data.model.*

@Database(
    version = 1,
    entities = [
        Order::class,
        Modification::class,
        Structure::class,
        Material::class,
        Hint::class
    ]
)
@TypeConverters(AppDatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract val orderDao: OrderDao
    abstract val orderSearchDao: OrderSearchDao
    abstract val modificationDao: ModificationDao
    abstract val structureDao: StructureDao
    abstract val materialDao: MaterialDao
    abstract val hintDao: HintDao
}