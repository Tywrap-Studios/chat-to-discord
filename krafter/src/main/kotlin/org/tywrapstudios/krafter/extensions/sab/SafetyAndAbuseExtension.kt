package org.tywrapstudios.krafter.extensions.sab

import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.guild.GuildCreateEvent
import dev.kord.rest.builder.message.embed
import dev.kordex.core.DISCORD_BLURPLE
import dev.kordex.core.DISCORD_GREEN
import dev.kordex.core.DISCORD_RED
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
                    event.guild
                )
            }
        }
    }
}

fun getSabChannel(): TextChannel? {
    return getKoin().getOrNull<SafetyAndAbuseExtension>(named("krafter.sab"))?.dumpChannel
}

suspend fun sabInfo(title: String, description: String) {
    getSabChannel()?.createMessage {
        embed {
            this.title = title
            field {
                value = description
            }
            color = DISCORD_BLURPLE
            timestamp = Clock.System.now()
            footer {
                text = Translations.Extensions.Sab.name.translate()
//            image =
            }
        }
    }
}

suspend fun sabConfirm(title: String, description: String) {
    getSabChannel()?.createMessage {
        embed {
            this.title = title
            field {
                value = description
            }
            color = DISCORD_GREEN
            timestamp = Clock.System.now()
            footer {
                text = Translations.Extensions.Sab.name.translate()
            }
        }
    }
}

suspend fun sabWarn(title: String, description: String) {
    getSabChannel()?.createMessage {
        embed {
            this.title = title
            field {
                value = description
            }
            color = ORANGE
            timestamp = Clock.System.now()
            footer {
                text = Translations.Extensions.Sab.name.translate()
//            image =
            }
        }
    }
}

suspend fun sabError(title: String, description: String) {
    getSabChannel()?.createMessage {
        embed {
            this.title = title
            field {
                value = description
            }
            color = DISCORD_RED
            timestamp = Clock.System.now()
            footer {
                text = Translations.Extensions.Sab.name.translate()
//            image =
            }
        }
    }
}