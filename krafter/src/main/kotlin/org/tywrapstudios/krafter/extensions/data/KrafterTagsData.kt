package org.tywrapstudios.krafter.extensions.data

import dev.kord.common.entity.Snowflake
import dev.kordex.modules.func.tags.data.Tag
import dev.kordex.modules.func.tags.data.TagsData
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.tywrapstudios.krafter.database.DatabaseManager.krafterSqlLogger
import org.tywrapstudios.krafter.database.tables.TagsTable
import org.tywrapstudios.krafter.database.tables.TagsTable.category
import org.tywrapstudios.krafter.database.tables.TagsTable.fromRow
import org.tywrapstudios.krafter.database.tables.TagsTable.key
import org.tywrapstudios.krafter.database.tables.TagsTable.title

class KrafterTagsData : TagsData {
    override suspend fun getTagByKey(
        key: String,
        guildId: Snowflake?
    ): Tag? {
        var tag: Tag? = null
        transaction {
            addLogger(krafterSqlLogger)

            TagsTable.select(TagsTable.key).where { (TagsTable.key eq key) and (TagsTable.guildId eq guildId?.value) }
                .forEach { tag = fromRow(it) }
        }
        return tag
    }

    override suspend fun getTagsByCategory(
        category: String,
        guildId: Snowflake?
    ): List<Tag> {
        val tags = ArrayList<Tag>()
        transaction {
            addLogger(krafterSqlLogger)

            TagsTable.select(TagsTable.category)
                .where { (TagsTable.category eq category) and (TagsTable.guildId eq guildId?.value) }
                .forEach { tags.add(fromRow(it)) }
        }
        return tags
    }

    override suspend fun getTagsByPartialKey(
        partialKey: String,
        guildId: Snowflake?
    ): List<Tag> {
        val tags = ArrayList<Tag>()
        transaction {
            addLogger(krafterSqlLogger)

            TagsTable.select(key).forEach {
                if (it[key].contains(partialKey)) tags.add(fromRow(it))
            }
        }
        return tags
    }

    override suspend fun getTagsByPartialTitle(
        partialTitle: String,
        guildId: Snowflake?
    ): List<Tag> {
        val tags = ArrayList<Tag>()
        transaction {
            addLogger(krafterSqlLogger)

            TagsTable.select(title).forEach {
                if (it[title].contains(partialTitle)) tags.add(fromRow(it))
            }
        }
        return tags
    }

    override suspend fun getAllCategories(guildId: Snowflake?): Set<String> {
        val categories = HashSet<String>()
        transaction {
            addLogger(krafterSqlLogger)

            TagsTable.select(category).where { (TagsTable.guildId eq guildId?.value) or (TagsTable.guildId eq null) }
                .forEach { categories.add(it[category]) }
        }
        return categories
    }

    override suspend fun findTags(
        category: String?,
        guildId: Snowflake?,
        key: String?
    ): List<Tag> {
        val tags = ArrayList<Tag>()
        var catBool = false
        var guildBool = false
        var keyBool = false

        transaction {
            addLogger(krafterSqlLogger)

            TagsTable.select(TagsTable.id).forEach {
                if (category == null || category == it[TagsTable.category]) catBool = true
                if (guildId == null || guildId.value == it[TagsTable.guildId]) guildBool = true
                if (key == null || key == it[TagsTable.key]) keyBool = true
                if (catBool && guildBool && keyBool) {
                    tags.add(fromRow(it))
                }
            }
        }
        return tags
    }

    override suspend fun setTag(tag: Tag) {
        transaction {
            addLogger(krafterSqlLogger)

            TagsTable.update {
                TagsTable.replace {
                    it[TagsTable.category] = tag.category
                    it[TagsTable.description] = tag.description
                    it[TagsTable.key] = tag.key
                    it[TagsTable.title] = tag.title
                    it[TagsTable.color] = if (tag.color != null) tag.color!!.rgb else null
                    it[TagsTable.guildId] = if (tag.guildId != null) tag.guildId!!.value else null
                    it[TagsTable.image] = tag.image
                }
            }
        }
    }

    override suspend fun deleteTagByKey(
        key: String,
        guildId: Snowflake?
    ): Tag? {
        var tag: Tag? = null
        transaction {
            addLogger(krafterSqlLogger)

            TagsTable.deleteReturning { (TagsTable.key eq key) and (TagsTable.guildId eq guildId?.value) }.forEach {
                tag = fromRow(it)
            }
        }
        return tag
    }
}
