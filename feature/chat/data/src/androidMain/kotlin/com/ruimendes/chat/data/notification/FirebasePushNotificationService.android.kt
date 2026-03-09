package com.ruimendes.chat.data.notification

import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.ruimendes.chat.domain.notifications.PushNotificationService
import com.ruimendes.core.domain.logging.AppLogger
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import kotlin.coroutines.coroutineContext

actual class FirebasePushNotificationService(
    private val logger: AppLogger
) : PushNotificationService {

    actual override fun observeDeviceToken() = flow {
        try {
            val fcmToken = Firebase.messaging.token.await()
            logger.info("Initial FCM token received: $fcmToken")
            emit(fcmToken)
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            logger.error("Failed to get FCM token", e)
            emit(null)
        }
    }
}