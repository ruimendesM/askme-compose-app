package com.ruimendes.chat.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class ProfileViewModel : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(ProfileState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                /** Load initial data here **/
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ProfileState()
        )

    fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.OnChangePasswordClick -> {}
            ProfileAction.OnConfirmPictureDeleteClick -> {}
            ProfileAction.OnDeletePictureClick -> {}
            ProfileAction.OnDismiss -> {}
            ProfileAction.OnDismissDeleteConfirmationDialogClick -> {}
            ProfileAction.OnErrorImagePicker -> {}
            is ProfileAction.OnPictureSelected -> {}
            ProfileAction.OnToggleNewPasswordVisibility -> {}
            ProfileAction.OnUploadPictureClick -> {}
            is ProfileAction.OnUriSelected -> {}
        }
    }

}