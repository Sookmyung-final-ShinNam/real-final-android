package com.veryshinnam.myapp.feature.admin.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veryshinnam.myapp.feature.admin.data.repository.AdminRepository
import com.veryshinnam.myapp.feature.admin.model.AdminStory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: AdminRepository
) : ViewModel() {

    private val TAG = "AdminStoryVM"

    private val _stories = MutableStateFlow<List<AdminStory>>(emptyList())
    val stories: StateFlow<List<AdminStory>> = _stories

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    fun loadStories() {
        viewModelScope.launch {
            try {
                val result = repository.getIncompleteStories()
                Log.d(TAG, "API Response (loadStories): $result")
                _stories.value = result
            } catch (e: Exception) {
                Log.e(TAG, "Error loading stories", e)
            }
        }
    }

    fun uploadImageLink(id: Long, link: String) {
        viewModelScope.launch {
            try {
                val result = repository.uploadImageYoutubeLink(id, link)
                Log.d(TAG, "API Response (uploadImageLink): $result")

                _toastMessage.value = result
                loadStories()
            } catch (e: Exception) {
                Log.e(TAG, "Error uploading image link", e)
            }
        }
    }

    fun uploadVideoLink(id: Long, link: String) {
        viewModelScope.launch {
            try {
                val result = repository.uploadVideoYoutubeLink(id, link)
                Log.d(TAG, "API Response (uploadVideoLink): $result")

                _toastMessage.value = result
                loadStories()
            } catch (e: Exception) {
                Log.e(TAG, "Error uploading video link", e)
            }
        }
    }

    fun getFailedStories() {
        viewModelScope.launch {
            try {
                val result = repository.getFailedStories()
                Log.d(TAG, "API Response (getFailedStories): $result")
            } catch (e: Exception) {
                Log.e(TAG, "API Response Error (getFailedStories)", e)
            }
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }
}