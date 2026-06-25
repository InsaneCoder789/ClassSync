package com.rochiee.classsync.bloc.classroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rochiee.classsync.domain.model.ClassroomCatalog
import com.rochiee.classsync.domain.model.ClassroomSection
import com.rochiee.classsync.domain.usecase.classroom.GetClassroomCatalogUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClassroomScreenViewModel(
    private val getClassroomCatalogUseCase: GetClassroomCatalogUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ClassroomScreenState())
    val state: StateFlow<ClassroomScreenState> = _state.asStateFlow()

    init {
        onEvent(ClassroomScreenEvent.LoadData)
    }

    fun onEvent(event: ClassroomScreenEvent) {
        when (event) {
            ClassroomScreenEvent.LoadData -> loadCatalog()
            is ClassroomScreenEvent.SelectSemester -> {
                _state.update { current ->
                    current.copy(
                        selectedSemester = event.semesterNumber,
                        selectedSectionId = null,
                        selectedSection = null
                    )
                }
            }
            is ClassroomScreenEvent.SelectSection -> {
                _state.update { current ->
                    val section = current.catalog.findSection(current.selectedSemester, event.sectionId)
                    current.copy(
                        selectedSectionId = event.sectionId,
                        selectedSection = section
                    )
                }
            }
            ClassroomScreenEvent.BackToSemesters -> {
                _state.update {
                    it.copy(
                        selectedSemester = null,
                        selectedSectionId = null,
                        selectedSection = null
                    )
                }
            }
            ClassroomScreenEvent.BackToSections -> {
                _state.update {
                    it.copy(
                        selectedSectionId = null,
                        selectedSection = null
                    )
                }
            }
            ClassroomScreenEvent.RefreshData -> loadCatalog(refreshing = true)
            ClassroomScreenEvent.ClearError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun loadCatalog(refreshing: Boolean = false) {
        _state.update { it.copy(isLoading = !refreshing, isRefreshing = refreshing, errorMessage = null) }
        viewModelScope.launch {
            runCatching { getClassroomCatalogUseCase() }
                .onSuccess { catalog ->
                    _state.update { current ->
                        val selectedSection = catalog.findSection(current.selectedSemester, current.selectedSectionId)
                        current.copy(
                            isLoading = false,
                            isRefreshing = false,
                            catalog = catalog,
                            selectedSection = selectedSection,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = error.message ?: "Failed to load classroom catalog."
                        )
                    }
                }
        }
    }

    private fun ClassroomCatalog.findSection(semesterNumber: Int?, sectionId: String?): ClassroomSection? {
        if (semesterNumber == null || sectionId == null) return null
        return semesters
            .firstOrNull { it.semesterNumber == semesterNumber }
            ?.sections
            ?.firstOrNull { it.sectionId == sectionId }
    }
}
