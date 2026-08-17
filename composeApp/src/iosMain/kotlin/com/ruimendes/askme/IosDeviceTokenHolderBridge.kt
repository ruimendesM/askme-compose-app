package com.ruimendes.askme

import com.ruimendes.chat.data.notification.IosDeviceTokenHolder

object IosDeviceTokenHolderBridge {
    fun updateToken(token: String) {
        IosDeviceTokenHolder.updateToken(token)
    }
}