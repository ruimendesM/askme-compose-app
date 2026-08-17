package com.ruimendes.chat.data.notification

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object IosDeviceTokenHolder {

    private val _token = MutableStateFlow<String?>(null)
    val token = _token.asStateFlow()

    fun updateToken(newToken: String?) {
        _token.value = newToken
    }
}