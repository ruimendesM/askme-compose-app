package com.ruimendes.chat.data.mappers

import com.ruimendes.chat.data.dto.response.ProfilePictureUploadUrlsResponse
import com.ruimendes.chat.domain.models.ProfilePictureUploadUrls

fun ProfilePictureUploadUrlsResponse.toDomain(): ProfilePictureUploadUrls {
    return ProfilePictureUploadUrls(
        uploadUrl = this.uploadUrl,
        publicUrl = this.publicUrl,
        headers = this.headers
    )
}