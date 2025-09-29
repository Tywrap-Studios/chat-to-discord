package org.tywrapstudios.krafter.database.tables

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.duration
import org.tywrapstudios.krafter.database.entities.OwnedThread
import org.tywrapstudios.krafter.database.sql.SnowflakeIdTable
import org.tywrapstudios.krafter.database.sql.snowflake
import kotlin.time.toKotlinDuration

object OwnedThreadTable : SnowflakeIdTable() {
    val owner = snowflake("owner")
    val guildId = snowflake("guildId")
    val preventArchiving = bool("preventArchiving").default(false)

    val maxThreadDuration = duration("maxThreadDuration").nullable()
    val maxThreadAfterIdle = duration("maxThreadAfterIdle").nullable()

    fun fromRow(row: ResultRow) = OwnedThread(
        row[id].value,
        row[owner],
        row[guildId],
        row[preventArchiving],
        row[maxThreadDuration]?.toKotlinDuration(),
        row[maxThreadAfterIdle]?.toKotlinDuration()
    )
}