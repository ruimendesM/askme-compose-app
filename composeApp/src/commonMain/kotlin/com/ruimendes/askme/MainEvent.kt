package com.ruimendes.askme

sealed interface MainEvent {
    data object OnSessionExpired : MainEvent
}