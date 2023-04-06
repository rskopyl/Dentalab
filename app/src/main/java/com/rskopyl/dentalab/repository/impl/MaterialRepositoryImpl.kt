package com.rskopyl.dentalab.repository.impl

import com.rskopyl.dentalab.data.local.dao.MaterialDao
import com.rskopyl.dentalab.data.model.Material
import com.rskopyl.dentalab.di.ApplicationScope
import com.rskopyl.dentalab.repository.MaterialRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MaterialRepositoryImpl @Inject constructor(
    private val materialDao: MaterialDao,
    @ApplicationScope
    private val applicationScope: CoroutineScope
) : MaterialRepository {

    override fun getAll(): Flow<List<Material>> =
        materialDao.getAll()

    override fun getById(id: Long): Flow<Material?> =
        materialDao.getById(id)

    override fun insert(material: Material) {
        applicationScope.launch(Dispatchers.IO) {
            materialDao.insert(material)
        }
    }

    override fun update(material: Material) {
        applicationScope.launch(Dispatchers.IO) {
            materialDao.update(material)
        }
    }

    override fun deleteById(id: Long) {
        applicationScope.launch(Dispatchers.IO) {
            materialDao.deleteById(id)
        }
    }
}