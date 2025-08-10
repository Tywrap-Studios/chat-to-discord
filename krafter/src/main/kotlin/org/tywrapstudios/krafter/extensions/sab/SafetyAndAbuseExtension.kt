package org.tywrapstudios.krafter.extensions.sab

import dev.kord.common.entity.Overwrite
import dev.kord.common.entity.OverwriteType
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.Guild
import dev.kord.core.entity.PermissionOverwrite
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.guild.GuildCreateEvent
import dev.kord.rest.builder.message.embed
import dev.kordex.core.DISCORD_BLURPLE
import dev.kordex.core.DISCORD_GREEN
import dev.kordex.core.DISCORD_RED
import dev.kordex.core.ExtensibleBot
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.event
import dev.kordex.core.utils.getKoin
import kotlinx.datetime.Clock
import org.koin.core.qualifier.named
import org.tywrapstudios.krafter.ORANGE
import org.tywrapstudios.krafter.config
import org.tywrapstudios.krafter.getOrCreateChannel
import org.tywrapstudios.krafter.i18n.Translations

class SafetyAndAbuseExtension : Extension() {
    override val name: String = "krafter.sab"
    var dumpChannel: TextChannel? = null

    override suspend fun setup() {
        val cfg = config().safety_and_abuse

        event<GuildCreateEvent> {
            action {
                dumpChannel = getOrCreateChannel(
                    cfg.dump_channel,
                    "krafter-sab",
                    "Safety and Abuse logging and dump channel for the Krafter software",
                    getOverwrites(event.guild),
                    event.guild
                )
            }
        }
    }
}

fun getOverwrites(guild: Guild): MutableSet<Overwrite> {
    val cfg = config().safety_and_abuse
    val overwrites = mutableSetOf<Overwrite>()
    for (role in cfg.administrators.roles) {
        overwrites.add(
            Overwrite(
                Snowflake(role),
                OverwriteType.Role,
                Permissions {
                    +Permission.ViewChannel
                    -Permission.SendMessages
                },
                Permissions {
                    -Permission.ViewChannel
                    +Permission.SendMessages
                }
            )
        )
    }

    for (user in cfg.administrators.users) {
        overwrites.add(
            Overwrite(
                Snowflake(user),
                OverwriteType.Member,
                Permissions {
                    +Permission.ViewChannel
                    -Permission.SendMessages
                },
                Permissions {
                    -Permission.ViewChannel
                    +Permission.SendMessages
                }
            )
        )
    }

    overwrites.add(
        Overwrite(
            guild.id,
            OverwriteType.Role,
            Permissions {
                -Permission.ViewChannel
                -Permission.SendMessages
            },
            Permissions {
                +Permission.ViewChannel
                +Permission.SendMessages
            }
        )
    )

    return overwrites
}
