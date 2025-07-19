package org.tywrapstudios.krafter.extensions

import dev.kord.common.entity.Snowflake
import dev.kordex.modules.func.tags.data.Tag
import dev.kordex.modules.func.tags.data.TagsData

class CustomTagsData : TagsData {
    override suspend fun getTagByKey(
        key: String,
        guildId: Snowflake?
    ): Tag? {
        TODO("Not yet implemented")
    }

    override suspend fun getTagsByCategory(
        category: String,
        guildId: Snowflake?
    ): List<Tag> {
        TODO("Not yet implemented")
    }

    override suspend fun getTagsByPartialKey(
        partialKey: String,
        guildId: Snowflake?
    ): List<Tag> {
        TODO("Not yet implemented")
    }

    override suspend fun getTagsByPartialTitle(
        partialTitle: String,
        guildId: Snowflake?
    ): List<Tag> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllCategories(guildId: Snowflake?): Set<String> {
        TODO("Not yet implemented")
    }

    override suspend fun findTags(
        category: String?,
        guildId: Snowflake?,
        key: String?
    ): List<Tag> {
        TODO("Not yet implemented")
    }

    override suspend fun setTag(tag: Tag) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTagByKey(
        key: String,
        guildId: Snowflake?
    ): Tag? {
        TODO("Not yet implemented")
    }
}
