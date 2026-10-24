package me.rerere.rikkahub.data.ai.tools

import me.rerere.ai.core.InputSchema
import me.rerere.ai.core.Tool
import me.rerere.ai.ui.UIMessagePart
import me.rerere.rikkahub.data.datastore.Settings
import me.rerere.rikkahub.data.datastore.LockChatSetting
import kotlinx.serialization.json.*

fun createLockChatTool(settings: Settings): Tool {
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

        settings.lockChatSetting = LockChatSetting(
            lockedUntilMillis = System.currentTimeMillis() + minutes * 60_000L,
            lockMessage = msg
        )
        listOf(UIMessagePart.Text("对话已锁定 $minutes 分钟，到点自动解锁"))
    }
}
