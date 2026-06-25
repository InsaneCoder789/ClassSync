package com.rochiee.classsync.domain.usecase.classroom

import com.rochiee.classsync.domain.model.ClassroomCatalog
import com.rochiee.classsync.domain.repository.ClassroomCatalogRepository

class GetClassroomCatalogUseCase(
    private val repository: ClassroomCatalogRepository
) {
    suspend operator fun invoke(): ClassroomCatalog = repository.getCatalog()
}
