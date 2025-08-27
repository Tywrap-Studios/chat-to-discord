@file:Suppress("WildcardImport")

package org.tywrapstudios.krafter.extensions.sab

import dev.kord.common.entity.*
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.guild.GuildCreateEvent
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.event
import org.tywrapstudios.krafter.config
import org.tywrapstudios.krafter.getOrCreateChannel

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
