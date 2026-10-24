package me.rerere.rikkahub.data.ai.tools

import me.rerere.orangechat.data.LockChatSetting
import me.rerere.rikkahub.data.datastore.SettingsStore
import org.koin.core.component.get

fun createLockChatTool(): Tool {
    return Tool(
        name = "lock_chat",
        description = "锁定当前对话，让用户在指定时长内无法发送消息。",
        parameters = listOf(
            ToolParameter("duration_minutes", "int", "锁定时长（分钟）"),
            ToolParameter("message", "string", "锁定期间显示的提示语")
        )
    ) { args ->
        val minutes = (args["duration_minutes"]?.toIntOrNull()) ?: 10
        val msg = args["message"] ?: "该休息啦"
        
        val settingsStore = get<SettingsStore>()
        settingsStore.update {
            copy(
                lockChatSetting = LockChatSetting(
                    lockedUntilMillis = System.currentTimeMillis() + minutes * 60_000L,
                    lockMessage = msg
                )
            )
        }
        "对话已锁定 $minutes 分钟，到点自动解锁"
    }
}
