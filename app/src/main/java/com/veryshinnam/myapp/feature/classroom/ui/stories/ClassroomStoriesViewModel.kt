package com.veryshinnam.myapp.feature.classroom.ui.stories

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veryshinnam.myapp.feature.classroom.data.model.ClassroomCharacterData
import com.veryshinnam.myapp.feature.classroom.data.repository.ClassroomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

sealed interface ClassroomStoriesUiState {
    data object Loading : ClassroomStoriesUiState
    data class Error(val message: String) : ClassroomStoriesUiState
    data class Success(val characters: List<ClassroomCharacterData>, val week: Int) : ClassroomStoriesUiState
}

@HiltViewModel
class ClassroomStoriesViewModel @Inject constructor(
    private val repository: ClassroomRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val classroomId: Long = savedStateHandle["classroomId"] ?: 0L
    private val week: Int = savedStateHandle["week"] ?: 1

    private val _uiState = MutableStateFlow<ClassroomStoriesUiState>(ClassroomStoriesUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init { reload() }

    fun reload() {
        viewModelScope.launch {
            _uiState.value = ClassroomStoriesUiState.Loading
            try {
                val characters = repository.getClassroomStories(classroomId, week)
                _uiState.value = ClassroomStoriesUiState.Success(characters, week)
            } catch (e: HttpException) {
                if (e.code() != 401) _uiState.value = ClassroomStoriesUiState.Error("동화 목록을 불러오지 못했어요.")
            } catch (e: Exception) {
                _uiState.value = ClassroomStoriesUiState.Error(e.message ?: "오류가 발생했어요.")
            }
        }
    }
}
