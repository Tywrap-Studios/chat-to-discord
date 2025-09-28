@file:Suppress("LongMethod", "CyclomaticComplexMethod")

package org.tywrapstudios.krafter.extensions.minecraft

import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.guild.GuildCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.application.slash.ephemeralSubCommand
import dev.kordex.core.commands.application.slash.group
import dev.kordex.core.commands.converters.impl.member
import dev.kordex.core.commands.converters.impl.string
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.ephemeralSlashCommand
import dev.kordex.core.extensions.event
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import org.tywrapstudios.krafter.api.discord.McMessage
import org.tywrapstudios.krafter.checks.isGlobalBotAdmin
import org.tywrapstudios.krafter.config
import org.tywrapstudios.krafter.extensions.data.KrafterMinecraftLinkData
import org.tywrapstudios.krafter.getOrCreateChannel
import org.tywrapstudios.krafter.i18n.Translations
import org.tywrapstudios.krafter.platform.MC_CONNECTION
import org.tywrapstudios.krafter.setup
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.regex.Pattern

var watchChannel: TextChannel? = null

class MinecraftExtension : Extension() {
    override val name: String = "krafter.minecraft"
    val data: KrafterMinecraftLinkData = KrafterMinecraftLinkData()

    override suspend fun setup() {
        val cfg = config().minecraft

        event<GuildCreateEvent> {
            action {
                watchChannel = getOrCreateChannel(
                    cfg.watch_channel,
                    "mc-chat",
                    "Discord to Chat watch channel for the Krafter software. " +
                            "Send your messages here to have them sent to Minecraft chat!",
                    mutableSetOf(),
                    event.guild
                )
            }
        }

        event<MessageCreateEvent> {
            action {
                if (!config().minecraft.enabled) return@action
                if (event.message.channel.asChannel() == watchChannel) {
                    event.message.author?.isBot?.let { if (!it) MC_CONNECTION.broadcast(McMessage(event.message)) }
                }
            }
        }

        ephemeralSlashCommand {
            name = Translations.Commands.minecraft
            description = Translations.Commands.Minecraft.description
//            group(Translations.Commands.minecraft) {
//                description = Translations.Commands.Minecraft.description
//            }

            ephemeralSubCommand(::LinkCommandArguments) {
                name = Translations.Commands.Minecraft.link
                description = Translations.Commands.Minecraft.Link.description
                action {
                    if (!config().minecraft.enabled) {
                        respond {
                            content = Translations.Commands.Minecraft.Link.Error.disabled.translate()
                        }
                        return@action
                    }

                    val uuid = arguments.uuid
                    val member = event.interaction.user.id

                    val link = data.setLinkStatus(member, KrafterMinecraftLinkData.LinkStatus(UUID.fromString(uuid)))

                    respond {
                        content = Translations.Commands.Minecraft.Link.success.withOrdinalPlaceholders(
                            link.code
                        ).translate()
                    }
                }
            }

            ephemeralSubCommand {
                name = Translations.Commands.Minecraft.unlink
                description = Translations.Commands.Minecraft.Unlink.description
                action {
                    if (!config().minecraft.enabled) {
                        respond {
                            content = Translations.Commands.Minecraft.Unlink.Error.disabled.translate()
                        }
                        return@action
                    }

                    val member = event.interaction.user.id
                    val uuid = data.unlink(member)
                    if (uuid == null) {
                        respond {
                            content = Translations.Commands.Minecraft.Unlink.Error.notLinked.translate()
                        }
                    } else {
                        respond {
                            content = Translations.Commands.Minecraft.Unlink.success.withOrdinalPlaceholders(
                                uuid
                            ).translate()
                        }
                    }
                }
            }

            ephemeralSubCommand(::ForceLinkCommandArguments) {
                name = Translations.Commands.Minecraft.forceLink
                description = Translations.Commands.Minecraft.ForceLink.description
                check {
                    isGlobalBotAdmin()
                }
                action {
                    if (!config().minecraft.enabled) {
                        respond {
                            content = Translations.Commands.Minecraft.ForceLink.Error.disabled.translate()
                        }
                        return@action
                    }

                    val member = arguments.member
                    val uuid = arguments.uuid

                    val currentLink = data.getLinkStatus(member.id)

                    if (currentLink == null) {
                        data.setLinkStatus(
                            member.id,
                            KrafterMinecraftLinkData.LinkStatus(UUID.fromString(uuid))
                        )
                    } else if (currentLink.uuid.toString() == uuid && currentLink.verified) {
                        respond {
                            content =
                                Translations.Commands.Minecraft.ForceLink.Error.alreadyLinked.withOrdinalPlaceholders(
                                    currentLink.uuid
                            ).translate()
                        }
                        return@action
                    } else if (currentLink.uuid.toString() != uuid && currentLink.verified) {
                        respond {
                            content = Translations.Commands.Minecraft.ForceLink.Error.alreadyLinkedDifferent
                                .withOrdinalPlaceholders(currentLink.uuid)
                                .translate()
                        }
                        return@action
                    } else if (!currentLink.verified) {
                        data.verify(member.id, currentLink.code)
                        respond {
                            content = Translations.Commands.Minecraft.ForceLink.success.withOrdinalPlaceholders(
                                member.mention,
                                currentLink.uuid
                            ).translate()
                        }
                    }
                }
            }
        }
    }

    class LinkCommandArguments : Arguments() {
        val uuid by string {
            name = Translations.Commands.Minecraft.Link.Arg.uuid
            description = Translations.Commands.Minecraft.Link.description
            validate {
                failIfNot(Translations.Commands.Minecraft.Link.Error.invalidUuid) {
                    Pattern
                        .compile(
                            "([0-9a-f]{8})(?:-|)([0-9a-f]{4})(?:-|)(4[0-9a-f]{" +
                                    "3})(?:-|)([89ab][0-9a-f]{3})(?:-|)([0-9a-f]{12})"
                        )
                        .matcher(value)
                        .matches()
                }
            }
        }
    }

    class ForceLinkCommandArguments : Arguments() {
        val member by member {
            name = Translations.Commands.Minecraft.ForceLink.Arg.member
            description = Translations.Commands.Minecraft.ForceLink.Arg.Member.description
        }

        val uuid by string {
            name = Translations.Commands.Minecraft.ForceLink.Arg.uuid
            description = Translations.Commands.Minecraft.ForceLink.Arg.Uuid.description
            validate {
                failIfNot(Translations.Commands.Minecraft.ForceLink.Error.invalidUuid) {
                    Pattern
                        .compile(
                            "([0-9a-f]{8})(?:-|)([0-9a-f]{4})(?:-|)(4[0-9a-f]{" +
                                    "3})(?:-|)([89ab][0-9a-f]{3})(?:-|)([0-9a-f]{12})"
                        )
                        .matcher(value)
                        .matches()
                }
            }
        }
    }
}

/**
 * Verifies a Minecraft link for the given UUID and code.
 *
 * SHOULD ONLY BE USED IF YOU'RE CERTAIN THAT THE MEMBER IS CURRENTLY BEING LINKED
 * TO A UUID IN THE CURRENT RUNTIME.
 *
 * - If the verification code does not match, the method will return -1.
 * - If the verification is successful, it will return 1.
 * - If no link status exists for the member, or a different error occurs it will return 0.
 */
@OptIn(DelicateCoroutinesApi::class)
fun verify(uuid: UUID, code: Int): CompletableFuture<Int> = GlobalScope.future {
    setup().extensions["krafter.minecraft"]?.let { ext ->
        if (ext is MinecraftExtension) {
            return@future ext.data.verify(uuid, code.toUInt())
        }
    }
    return@future 0
}
