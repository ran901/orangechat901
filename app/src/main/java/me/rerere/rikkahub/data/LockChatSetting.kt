package me.rerere.rikkahub.data.datastore // ✅ 修正包名，和底层保持一致

import kotlinx.serialization.Serializable

@Serializable // ✅ 加上序列化注解，修复 Serializer 报错
data class LockChatSetting(
    val lockedUntilMillis: Long = 0L,
    val lockMessage: String = "",
    val lockBackgroundColor: Long? = null
)
