package org.tywrapstudios.krafter.extensions.sab

import dev.kord.common.entity.Overwrite
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.createTextChannel
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.guild.GuildCreateEvent
import dev.kordex.core.checks.guildFor
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.event
import dev.kordex.core.utils.getKoin
import org.koin.core.qualifier.named
import org.tywrapstudios.krafter.CFG_CHANNEL_REASON
import org.tywrapstudios.krafter.config

class SafetyAndAbuseExtension : Extension() {
    override val name: String = "sab"
    var dumpChannel: TextChannel? = null

    override suspend fun setup() {
        val cfg = config().safety_and_abuse

        event<GuildCreateEvent> {
            action {
                if (cfg.dump_channel == "new" || cfg.dump_channel.isNullOrEmpty()) {
                    event.guild.createTextChannel("krafter-sab") {
                        reason = CFG_CHANNEL_REASON
                        topic = "Safety and Abuse logging and dump channel for the Krafter software"
                    }
                }
            }
        }
    }
}

fun getSabChannel(): TextChannel? {
    return getKoin().get<SafetyAndAbuseExtension>(named("sab")).dumpChannel
}