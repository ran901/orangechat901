package me.rerere.orangechat.data

data class LockChatSetting(
    val lockedUntilMillis: Long = 0L,
    val lockMessage: String = "",
    val lockBackgroundColor: Long? = null
)
