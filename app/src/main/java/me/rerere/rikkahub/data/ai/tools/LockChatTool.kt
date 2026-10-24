package me.rerere.rikkahub.data.ai.tools

import me.rerere.ai.core.InputSchema
import me.rerere.ai.core.Tool
import me.rerere.ai.ui.UIMessagePart // ✅ 补充 UI 返回值引用
import me.rerere.rikkahub.data.datastore.SettingsStore
import me.rerere.rikkahub.data.datastore.LockChatSetting
import kotlinx.serialization.json.*
import org.koin.core.component.KoinPlatform

fun createLockChatTool(): Tool {
    return Tool(
        name = "lock_chat",
        description = "锁定当前对话，让用户在指定时长内无法发送消息（但仍可查看聊天记录），用于强制休息或睡觉。",
        needsApproval = false,
        parameters = {
            InputSchema.Obj(
                properties = buildJsonObject {
                    putJsonObject("duration_minutes") {
                        put("type", "integer")
                        put("description", "锁定时长（分钟），必须是正整数")
                    }
                    putJsonObject("message") {
                        put("type", "string")
                        put("description", "锁定期间显示给用户的话，可选")
                    }
                },
                required = listOf("duration_minutes")
            )
        }
    ) { args ->
        val params = args.jsonObject
        val minutes = params["duration_minutes"]?.jsonPrimitive?.intOrNull ?: 10
        val msg = params["message"]?.jsonPrimitive?.contentOrNull ?: "该休息啦"
        
        val settingsStore = KoinPlatform.getKoin().get<SettingsStore>()
        
        // ✅ 修改 1：使用 it.copy
        settingsStore.update {
            it.copy(
                lockChatSetting = LockChatSetting(
                    lockedUntilMillis = System.currentTimeMillis() + minutes * 60_000L,
                    lockMessage = msg
                )
            )
        }
        
        // ✅ 修改 2：返回值必须是 List<UIMessagePart>
        listOf(UIMessagePart.Text("对话已锁定 $minutes 分钟，到点自动解锁"))
    }
}
