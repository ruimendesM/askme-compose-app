package com.ruimendes.chat.data.notification

import com.ruimendes.chat.domain.notifications.PushNotificationService
import kotlinx.coroutines.flow.Flow

expect class FirebasePushNotificationService: PushNotificationService {
    override fun observeDeviceToken(): Flow<String?>
}