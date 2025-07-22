package org.tywrapstudios.krafter.database.tables

import dev.kord.common.entity.Snowflake
import org.jetbrains.exposed.sql.ResultRow
import org.quiltmc.community.cozy.modules.ama.data.AmaConfig
import org.quiltmc.community.cozy.modules.ama.data.AmaEmbedConfig
import org.tywrapstudios.krafter.database.sql.GuildIdTable

object AmaConfigTable : GuildIdTable() {
    val answerQueueChannel = ulong("answer-queue-channel")
    val liveChatChannel = ulong("live-chat-channel")
    val buttonChannel = ulong("button-channel")
    val approvalQueueChannel = ulong("approval-queue-channel").nullable()
    val flaggedQuestionChannel = ulong("flagged-question-channel").nullable()

    val title = text("title")
    val description = mediumText("description").nullable()
    val imageUrl = text("image_url").nullable()

    val buttonMessage = ulong("button-message")
    val buttonId = text("button-id")
    val enabled = bool("enabled")

    fun fromRow(row: ResultRow): AmaConfig {
        return AmaConfig(
            Snowflake(row[id].value),
            Snowflake(row[answerQueueChannel]),
            Snowflake(row[liveChatChannel]),
            Snowflake(row[buttonChannel]),
            getApprovalQueueChannel(row),
            getFlaggedChannel(row),

            embedConfigFromRow(row),

            Snowflake(row[buttonMessage]),
            row[buttonId],
            row[enabled],
        )
    }

    internal fun embedConfigFromRow(row: ResultRow): AmaEmbedConfig {
        return AmaEmbedConfig(
            row[title],
            row[description],
            row[imageUrl],
        )
    }

    internal fun getApprovalQueueChannel(row: ResultRow): Snowflake? {
        return Snowflake(row[approvalQueueChannel] ?: return null)
    }

    internal fun getFlaggedChannel(row: ResultRow): Snowflake? {
        return Snowflake(row[flaggedQuestionChannel] ?: return null)
    }
}