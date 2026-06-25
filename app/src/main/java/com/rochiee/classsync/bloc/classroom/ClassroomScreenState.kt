package com.rochiee.classsync.bloc.classroom

import com.rochiee.classsync.domain.model.ClassroomCatalog
import com.rochiee.classsync.domain.model.ClassroomSection

data class ClassroomScreenState(
    val isLoading: Boolean = true,
    val catalog: ClassroomCatalog = ClassroomCatalog(emptyList()),
    val selectedSemester: Int? = null,
    val selectedSectionId: String? = null,
    val selectedSection: ClassroomSection? = null,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)
