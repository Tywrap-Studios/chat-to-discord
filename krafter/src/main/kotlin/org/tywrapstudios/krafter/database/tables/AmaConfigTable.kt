package org.tywrapstudios.krafter.database.tables

import org.jetbrains.exposed.sql.ResultRow
import org.quiltmc.community.cozy.modules.ama.data.AmaConfig
import org.quiltmc.community.cozy.modules.ama.data.AmaEmbedConfig
import org.tywrapstudios.krafter.database.sql.SnowflakeIdTable
import org.tywrapstudios.krafter.database.sql.snowflake

/**
 * Based on [org.quiltmc.community.cozy.modules.ama.data.AmaConfig].
 */
object AmaConfigTable : SnowflakeIdTable() {
    val answerQueueChannel = snowflake("answer-queue-channel")
    val liveChatChannel = snowflake("live-chat-channel")
    val buttonChannel = snowflake("button-channel")
    val approvalQueueChannel = snowflake("approval-queue-channel").nullable()
    val flaggedQuestionChannel = snowflake("flagged-question-channel").nullable()

    val title = text("title")
    val description = mediumText("description").nullable()
    val imageUrl = text("image_url").nullable()

    val buttonMessage = snowflake("button-message")
    val buttonId = text("button-id")
    val enabled = bool("enabled")

    fun fromRow(row: ResultRow) = AmaConfig(
        row[id].value,
        row[answerQueueChannel],
        row[liveChatChannel],
        row[buttonChannel],
        row[approvalQueueChannel],
        row[flaggedQuestionChannel],

        embedConfigFromRow(row),

        row[buttonMessage],
        row[buttonId],
        row[enabled],
    )

    internal fun embedConfigFromRow(row: ResultRow) = AmaEmbedConfig(
        row[title],
        row[description],
        row[imageUrl],
    )
}