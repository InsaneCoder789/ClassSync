package com.rochiee.classsync.domain.repository

import com.rochiee.classsync.domain.model.ClassroomCatalog

interface ClassroomCatalogRepository {
    suspend fun getCatalog(): ClassroomCatalog
}
