package com.ruimendes.chat.domain.notifications

import kotlinx.coroutines.flow.Flow

interface PushNotificationService {
    fun observeDeviceToken(): Flow<String?>
}