package com.rskopyl.dentalab.repository.impl

import com.rskopyl.dentalab.data.local.dao.MaterialDao
import com.rskopyl.dentalab.data.local.dao.ModificationDao
import com.rskopyl.dentalab.data.local.dao.StructureDao
import com.rskopyl.dentalab.data.model.Components
import com.rskopyl.dentalab.data.model.Modification
import com.rskopyl.dentalab.di.ApplicationScope
import com.rskopyl.dentalab.repository.ModificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

class ModificationRepositoryImpl @Inject constructor(
    private val modificationDao: ModificationDao,
    private val structureDao: StructureDao,
    private val materialDao: MaterialDao,
    @ApplicationScope
    private val applicationScope: CoroutineScope
) : ModificationRepository {

    override fun getByOrderId(
        orderId: Long
    ): Flow<Map<Modification, Components>> {
        return combine(
            modificationDao.getByOrderId(orderId),
            structureDao.getAll(), materialDao.getAll()
        ) { modifications, structures, materials ->
            modifications.associateWith { modification ->
                Components(
                    structure = structures.firstOrNull {
                        it.id == modification.structureId
                    },
                    material = materials.firstOrNull {
                        it.id == modification.materialId
                    }
                )
            }
        }
    }

    override fun getByOrderIdAndPosition(
        orderId: Long, position: Int
    ): Flow<Pair<Modification, Components>?> {
        return combine(
            modificationDao.getByOrderIdAndPosition(orderId, position),
            structureDao.getAll(), materialDao.getAll()
        ) { modification, structures, materials ->
            modification?.to(
                Components(
                    structure = structures.firstOrNull {
                        it.id == modification.structureId
                    },
                    material = materials.firstOrNull {
                        it.id == modification.materialId
                    }
                )
            )
        }
    }

    override fun insert(modifications: List<Modification>) {
        applicationScope.launch(Dispatchers.IO) {
            modificationDao.insert(modifications)
        }
    }

    override fun insert(modification: Modification) {
        applicationScope.launch(Dispatchers.IO) {
            modificationDao.insert(modification)
        }
    }

    override fun deleteByOrderId(orderId: Long) {
        applicationScope.launch(Dispatchers.IO) {
            modificationDao.deleteByOrderId(orderId)
        }
    }

    override fun deleteByOrderIdAndPosition(orderId: Long, position: Int) {
        applicationScope.launch(Dispatchers.IO) {
            modificationDao.deleteByOrderIdAndPosition(orderId, position)
        }
    }
}