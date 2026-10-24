package me.rerere.rikkahub.data.ai.tools

import me.rerere.ai.core.InputSchema
import me.rerere.ai.core.Tool
import me.rerere.rikkahub.data.datastore.SettingsStore
import me.rerere.rikkahub.data.datastore.LockChatSetting // ✅ 修正包名路径
import kotlinx.serialization.json.*
import org.koin.core.component.KoinPlatform // ✅ 修正 Koin 获取方式

fun createLockChatTool(): Tool {
    return Tool(
        name = "lock_chat",
        description = "锁定当前对话，让用户在指定时长内无法发送消息（但仍可查看聊天记录），用于强制休息或睡觉。",
        needsApproval = false, // 不需要用户手动同意，直接执行
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
        // 解析参数
        val params = args.jsonObject
        val minutes = params["duration_minutes"]?.jsonPrimitive?.intOrNull ?: 10
        val msg = params["message"]?.jsonPrimitive?.contentOrNull ?: "该休息啦"
        
        // ✅ 正确的 Koin 获取方式
        val settingsStore = KoinPlatform.getKoin().get<SettingsStore>()
        
        // 写入设置
        settingsStore.update {
            copy(
                lockChatSetting = LockChatSetting(
                    lockedUntilMillis = System.currentTimeMillis() + minutes * 60_000L,
                    lockMessage = msg
                )
            )
        }
        
        // 返回给 AI 的结果
        "对话已锁定 $minutes 分钟，到点自动解锁"
    }
}
