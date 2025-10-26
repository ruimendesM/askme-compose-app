package com.ruimendes.askme

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform