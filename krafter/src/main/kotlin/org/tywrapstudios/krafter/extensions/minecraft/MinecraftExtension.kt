package org.tywrapstudios.krafter.extensions.minecraft

import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.guild.GuildCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.event
import kotlinx.coroutines.flow.toList
import org.tywrapstudios.krafter.LOGGING
import org.tywrapstudios.krafter.api.discord.McMessage
import org.tywrapstudios.krafter.config
import org.tywrapstudios.krafter.extensions.data.KrafterMinecraftLinkData
import org.tywrapstudios.krafter.getOrCreateChannel
import org.tywrapstudios.krafter.platform.MCSCCH

class MinecraftExtension : Extension() {
    override val name: String = "krafter.dtc"
    var watchChannel: TextChannel? = null
    val data: KrafterMinecraftLinkData = KrafterMinecraftLinkData()

    override suspend fun setup() {
        val cfg = config().discord_to_chat

        event<GuildCreateEvent> {
            action {
                watchChannel = getOrCreateChannel(
                    cfg.watch_channel,
                    "mc-chat",
                    "Discord to Chat watch channel for the Krafter software. " +
                            "Send your messages here to have them sent to Minecraft chat!",
                    event.guild
                )
            }
        }

        event<MessageCreateEvent> {
            action {
                if (event.message.channel.asChannel() == watchChannel) {
                    MCSCCH.broadcast(McMessage(event.message))
                }
            }
        }
    }
}