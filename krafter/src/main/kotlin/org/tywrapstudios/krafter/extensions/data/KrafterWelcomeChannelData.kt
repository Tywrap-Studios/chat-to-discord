package org.tywrapstudios.krafter.extensions.data

import dev.kord.common.entity.Snowflake
import dev.kordex.modules.func.welcome.data.WelcomeChannelData
import org.tywrapstudios.krafter.config
import org.tywrapstudios.krafter.saveConfig
import org.tywrapstudios.krafter.snowflake
import java.util.function.Consumer

class KrafterWelcomeChannelData : WelcomeChannelData {
    private val data: MutableMap<Snowflake, String> = mutableMapOf()

    override suspend fun getChannelURLs(): MutableMap<Snowflake, String> = syncDataFromConfig()

    override suspend fun getUrlForChannel(channelId: Snowflake): String? = syncDataFromConfig()[channelId]

    override suspend fun setUrlForChannel(channelId: Snowflake, url: String) {
        syncDataToConfig { it[channelId] = url }
    }

    override suspend fun removeChannel(channelId: Snowflake): String? {
        var removed: String? = null
        syncDataToConfig { removed = it.remove(channelId) }
        return removed
    }

    private fun syncDataFromConfig(): MutableMap<Snowflake, String> {
        data.clear()
        config().miscellaneous.embed_channels.channels.forEach {
            data[it.key.toULong().snowflake()] = it.value
        }
        return data
    }

    private fun syncDataToConfig(update: Consumer<MutableMap<Snowflake, String>>): MutableMap<String, String> {
        config().miscellaneous.embed_channels.channels.clear()
        update.accept(syncDataFromConfig())
        data.forEach {
            config().miscellaneous.embed_channels.channels.put(it.key.value.toString(), it.value)
        }
        saveConfig()
        return config().miscellaneous.embed_channels.channels
    }
}