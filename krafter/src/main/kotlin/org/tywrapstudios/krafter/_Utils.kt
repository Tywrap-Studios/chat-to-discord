package org.tywrapstudios.krafter

import dev.kord.common.Color
import dev.kord.core.behavior.createTextChannel
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.TextChannel
import dev.kordex.core.extensions.Extension
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.lastOrNull
import org.jetbrains.exposed.sql.SchemaUtils
import org.tywrapstudios.krafter.config.BotConfig
import org.tywrapstudios.krafter.database.tables.AmaConfigTable
import org.tywrapstudios.krafter.database.tables.TagsTable

const val CFG_CHANNEL_REASON = "Config prompted for an automatic new channel creation."

val ORANGE = Color(java.awt.Color(238, 142, 64).rgb)

fun config(): BotConfig = CFG.getConfig()

fun saveConfig() = CFG.saveConfig()

fun createSchemas() {
    SchemaUtils.create(TagsTable, AmaConfigTable)
}

suspend fun Extension.getOrCreateChannel(
    providedName: String,
    defaultName: String,
    channelTopic: String = "A channel automatically created by Krafter.",
    guild: Guild,
): TextChannel {
    var channel: TextChannel?

    val channels = guild
        .channels
        .filter { (it.name == providedName || it.name == defaultName) }
    LOGGING.debug("$providedName $defaultName: ${channels.count()} channels found.")

    channel = channels.lastOrNull() as? TextChannel

    LOGGING.debug("$providedName $defaultName: ${channel?.mention} found.")

    LOGGING.debug("$providedName $defaultName: ${providedName == "new"}")
    if (providedName == "new") {
        channel = guild.createTextChannel(defaultName) {
            reason = CFG_CHANNEL_REASON
            topic = channelTopic
        }
        LOGGING.debug("$providedName $defaultName: ${channel.mention} created. New channel.")
    }

    LOGGING.debug("$providedName $defaultName: ${providedName.isEmpty() && channel == null} || ${channel == null}")
    if ((providedName.isEmpty() && channel == null) || channel == null) {
        channel = guild.createTextChannel(providedName.ifEmpty { defaultName }) {
            reason = CFG_CHANNEL_REASON
            topic = channelTopic
        }
        LOGGING.debug("$providedName $defaultName: ${channel.mention} created. Defaulted name.")
    }

    LOGGING.debug("$providedName $defaultName: ${channel.mention} final.")
    return channel
}