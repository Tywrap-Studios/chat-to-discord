package org.tywrapstudios.krafter.extensions.data

import dev.kord.common.entity.Snowflake
import dev.kordex.modules.func.tags.data.Tag
import dev.kordex.modules.func.tags.data.TagsData
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.tywrapstudios.krafter.database.tables.TagsTable
import org.tywrapstudios.krafter.database.tables.TagsTable.category
import org.tywrapstudios.krafter.database.tables.TagsTable.fromRow
import org.tywrapstudios.krafter.database.tables.TagsTable.key
import org.tywrapstudios.krafter.database.tables.TagsTable.title
import org.tywrapstudios.krafter.setup

class KrafterTagsData : TagsData {
    override suspend fun getTagByKey(
        key: String,
        guildId: Snowflake?
    ): Tag? {
        var tag: Tag? = null
        transaction {
            setup()

            TagsTable.selectAll().where { (TagsTable.key eq key) and (TagsTable.guildId eq guildId) }
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
            setup()

            TagsTable.selectAll()
                .where { (TagsTable.category eq category) and (TagsTable.guildId eq guildId) }
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
            setup()

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
            setup()

            TagsTable.select(title).forEach {
                if (it[title].contains(partialTitle)) tags.add(fromRow(it))
            }
        }
        return tags
    }

    override suspend fun getAllCategories(guildId: Snowflake?): Set<String> {
        val categories = HashSet<String>()
        transaction {
            setup()

            TagsTable.select(category).where { (TagsTable.guildId eq guildId) or (TagsTable.guildId eq null) }
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
            setup()

            TagsTable.selectAll().forEach {
                if (category == null || category == it[TagsTable.category]) catBool = true
                if (guildId == null || guildId == it[TagsTable.guildId]) guildBool = true
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
            setup()

            TagsTable.replace {
                it[TagsTable.category] = tag.category
                it[TagsTable.description] = tag.description
                it[TagsTable.key] = tag.key
                it[TagsTable.title] = tag.title
                it[TagsTable.color] = if (tag.color != null) tag.color!!.rgb else null
                it[TagsTable.guildId] = tag.guildId
                it[TagsTable.image] = tag.image
            }
        }
    }

    override suspend fun deleteTagByKey(
        key: String,
        guildId: Snowflake?
    ): Tag? {
        var tag: Tag? = null
        transaction {
            setup()

            TagsTable.deleteReturning { (TagsTable.key eq key) and (TagsTable.guildId eq guildId) }.forEach {
                tag = fromRow(it)
            }
        }
        return tag
    }
}
