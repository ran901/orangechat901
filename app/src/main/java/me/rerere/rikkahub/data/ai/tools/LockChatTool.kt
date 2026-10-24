package me.rerere.orangechat.data

import kotlinx.serialization.Serializable

@Serializable
data class LockChatSetting(
    val lockedUntilMillis: Long = 0L,
    val lockMessage: String = "",
    val lockBackgroundColor: Long? = null
)
