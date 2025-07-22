package org.tywrapstudios.krafter.database.tables

import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kordex.modules.func.tags.data.Tag
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.tywrapstudios.krafter.database.tables.TagsTable.guildId

/**
 * Based on [dev.kordex.modules.func.tags.data.Tag].
 *
 * Doesn't extend [org.tywrapstudios.krafter.database.sql.GuildIdTable] because [guildId] may be `null`.
 */
object TagsTable : IntIdTable() {
    val category = text("category")
    val description = mediumText("description")
    val key = text("key")
    val title = text("title")

    val color = integer("color").nullable()
    val guildId = ulong("guildId").nullable()
    val image = text("image").nullable()

    fun fromRow(row: ResultRow): Tag {
        return Tag(
            category = row[category],
            description = row[description],
            key = row[key],
            title = row[title],

            color = getColor(row),
            guildId = getGuildId(row),
            image = row[image],
        )
    }

    internal fun getColor(row: ResultRow): Color? {
        return Color(row[TagsTable.color] ?: return null)
    }

    internal fun getGuildId(row: ResultRow): Snowflake? {
        return Snowflake(row[TagsTable.guildId] ?: return null)
    }
}