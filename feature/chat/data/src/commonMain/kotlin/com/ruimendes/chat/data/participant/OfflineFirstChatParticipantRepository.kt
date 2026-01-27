package com.ruimendes.chat.data.participant

import com.ruimendes.chat.domain.models.ChatParticipant
import com.ruimendes.chat.domain.participant.ChatParticipantRepository
import com.ruimendes.chat.domain.participant.ChatParticipantService
import com.ruimendes.core.domain.auth.SessionStorage
import com.ruimendes.core.domain.util.DataError
import com.ruimendes.core.domain.util.Result
import com.ruimendes.core.domain.util.onSuccess
import kotlinx.coroutines.flow.first

class OfflineFirstChatParticipantRepository(
    private val sessionStorage: SessionStorage,
    private val chatParticipantService: ChatParticipantService
) : ChatParticipantRepository {

    override suspend fun fetchLocalParticipant(): Result<ChatParticipant, DataError> {
        return chatParticipantService
            .getLocalParticipant()
            .onSuccess { participant ->
                val currentAuthInfo = sessionStorage.observeAuthInfo().first()
                sessionStorage.set(
                    currentAuthInfo?.copy(
                        user = currentAuthInfo.user.copy(
                            id = participant.userId,
                            username = participant.username,
                            profilePictureUrl = participant.profilePictureUrl
                        )
                    )
                )
            }
    }
}