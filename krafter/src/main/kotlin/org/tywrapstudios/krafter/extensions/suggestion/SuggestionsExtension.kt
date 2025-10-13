/*
 * The Krafter Suggestion Extension was adapted from the Cozy Discord Bot.
 * The below is the license notice provided, but the latest version should always be available at the following
 * link: https://github.com/QuiltMC/cozy-discord/blob/root/LICENSE
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

@file:Suppress(
    "MagicNumber",
    "OPT_IN_USAGE_ERROR",
    "LongMethod",
    "WildcardImport",
    "ReturnCount",
    "CyclomaticComplexMethod"
)

package org.tywrapstudios.krafter.extensions.suggestion

import dev.kord.common.entity.*
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.channel.editRolePermission
import dev.kord.core.behavior.channel.threads.edit
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.getChannelOf
import dev.kord.core.behavior.interaction.modal
import dev.kord.core.behavior.interaction.response.createEphemeralFollowup
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.behavior.reply
import dev.kord.core.builder.components.emoji
import dev.kord.core.entity.Guild
import dev.kord.core.entity.PermissionOverwrite
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.entity.channel.TopGuildMessageChannel
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.entity.interaction.ButtonInteraction
import dev.kord.core.event.channel.thread.ThreadChannelCreateEvent
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.GuildModalSubmitInteractionCreateEvent
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.rest.builder.message.EmbedBuilder.Companion.ZERO_WIDTH_SPACE
import dev.kord.rest.builder.message.actionRow
import dev.kord.rest.builder.message.create.MessageCreateBuilder
import dev.kord.rest.builder.message.embed
import dev.kord.rest.builder.message.modify.MessageModifyBuilder
import dev.kord.rest.json.JsonErrorCode
import dev.kord.rest.request.KtorRequestException
import dev.kordex.core.annotations.NotTranslated
import dev.kordex.core.checks.guildFor
import dev.kordex.core.checks.topChannelFor
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.application.slash.converters.impl.optionalEnumChoice
import dev.kordex.core.commands.application.slash.ephemeralSubCommand
import dev.kordex.core.commands.application.slash.group
import dev.kordex.core.commands.converters.impl.optionalString
import dev.kordex.core.commands.converters.impl.string
import dev.kordex.core.events.interfaces.MessageEvent
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.ephemeralSlashCommand
import dev.kordex.core.extensions.event
import dev.kordex.core.utils.*
import dev.kordex.modules.dev.unsafe.commands.slash.InitialSlashCommandResponse
import dev.kordex.modules.dev.unsafe.extensions.unsafeSlashCommand
import dev.kordex.modules.pluralkit.api.PKMember
import dev.kordex.modules.pluralkit.events.ProxiedMessageCreateEvent
import dev.kordex.modules.pluralkit.events.UnProxiedMessageCreateEvent
import io.github.evanrupert.excelkt.Sheet
import io.github.evanrupert.excelkt.workbook
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.json.Json
import org.apache.commons.io.output.ByteArrayOutputStream
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFColor
import org.jetbrains.exposed.sql.exists
import org.tywrapstudios.krafter.*
import org.tywrapstudios.krafter.checks.isBotModuleAdmin
import org.tywrapstudios.krafter.config.BotConfig
import org.tywrapstudios.krafter.database.entities.OwnedThread
import org.tywrapstudios.krafter.database.entities.Suggestion
import org.tywrapstudios.krafter.database.tables.SuggestionTable
import org.tywrapstudios.krafter.database.tables.SuggestionTable.fromRow
import org.tywrapstudios.krafter.database.transactors.OwnedThreadTransactor
import org.tywrapstudios.krafter.database.transactors.SuggestionsTransactor
import org.tywrapstudios.krafter.i18n.Translations
import java.io.ByteArrayInputStream

private const val ACTION_DOWN = "down"
private const val ACTION_REMOVE = "remove"
private const val ACTION_UP = "up"

private const val THREAD_INTRO = "This message is at the top of the thread.\n\n" +
        "If this is your suggestion, you're welcome to use the various %s commands to manage your suggestion " +
        "thread as needed. You can edit your suggestion at any time using the </edit-suggestion:%s> command.\n\n" +
        "The thread is automatically archived to reduce clutter for mods, but anyone can un-archive it by sending a " +
        "message in the thread."

private const val COMMENT_SIZE_LIMIT: Long = 800
private const val SUGGESTION_SIZE_LIMIT: Long = 1000
private const val FIELD_SIZE_LIMIT: Int = 1024
private const val TEXT_SIZE_LIMIT: Int = 4000
private const val THIRTY_SECONDS: Long = 30_000

private const val PK_BASE_URL = "https://api.pluralkit.me/v2"

private val EMOTE_DOWNVOTE = ReactionEmoji.Unicode("⬇️")
private val EMOTE_REMOVE = ReactionEmoji.Unicode("\uD83D\uDDD1️")
private val EMOTE_UPVOTE = ReactionEmoji.Unicode("⬆️")

private val CLEAR_WORDS = arrayOf("clear", "null")

@Suppress("LargeClass", "TooManyFunctions")
class SuggestionsExtension : Extension() {
    override val name: String = "krafter.suggestions"
    private val logger get() = LOGGING

    val suggestions: SuggestionsTransactor = SuggestionsTransactor
    val config: BotConfig.Miscellaneous.SuggestionForum get() = config().miscellaneous.suggestion_forum
    private val threads: OwnedThreadTransactor = OwnedThreadTransactor

    private val httpClient = HttpClient {}

    private lateinit var threadIntroGlobal: String
    private val threadIds = mutableMapOf<Snowflake, Snowflake>()

    private suspend fun threadIntro(guildId: Snowflake): String {
        if (!::threadIntroGlobal.isInitialized) {
            val editSuggestion = kord.getGlobalApplicationCommands()
                .first { it.name == "edit-suggestion" }
                .id

            threadIntroGlobal = THREAD_INTRO.format("</thread:%s>", editSuggestion)
        }
        val threadId = threadIds.getOrPut(guildId) {
            kord.getGuildOrNull(guildId)!!
                .getApplicationCommands()
                .first { it.name == "thread" }
                .id
        }
        return threadIntroGlobal.format(threadId)
    }

    private suspend fun getChannel(guild: Guild?): TextChannel? {
        if (guild == null) return null
        return getOrCreateChannel(
            config.forum_channel,
            "suggestions",
            "A channel to suggest stuff in.",
            null,
            guild
        )
    }

    override suspend fun setup() {
        event<UnProxiedMessageCreateEvent> {
            check {
                failIfNot {
                    event.message.type == MessageType.Default ||
                            event.message.type == MessageType.Reply
                }

                // Outdated now
                fail()
            }

            check { failIf(event.message.content.trim().isEmpty()) }
            check { failIf(event.message.interaction != null) }
            check { failIf(event.message.data.authorId == event.kord.selfId) }
            check { failIf(event.message.author?.isBot == true) }

            check { failIfNot(event.message.channelId == getChannel(event.guild?.asGuild())?.id) }

            action {
                val id = event.message.id
                val suggestion = Suggestion(
                    id = id,
                    guildId = event.guildId!!,
                    channelId = event.message.channelId,
                    text = event.message.content,

                    owner = event.message.author!!.id,
                    ownerAvatar = event.message.author!!.avatar?.cdnUrl?.toUrl(),
                    ownerName = event.message.author!!.asMember(event.message.getGuild().id).effectiveName,

                    positiveVoters = mutableListOf(event.message.author!!.id)
                )

                val autoRemovals = mutableListOf<AutoRemoval>()
                config.auto_answer.forEach {
                    autoRemovals.add(it.toAutoRemoval())
                }

                for (autoRemoval in autoRemovals) {
                    if (autoRemoval.regex.containsMatchIn(suggestion.text)) {
                        suggestion.status = autoRemoval.status
                        suggestion.comment = "(Automatic response) " + autoRemoval.reason
                        break
                    }
                }

                if (checkSuggestionLength(suggestion, event)) {
                    suggestions.set(suggestion)
                    sendSuggestion(suggestion)
                    event.message.delete()
                }
            }
        }

        event<ProxiedMessageCreateEvent> {
            check {
                failIfNot {
                    event.message.type == MessageType.Default ||
                            event.message.type == MessageType.Reply
                }

                // Outdated now
                fail()
            }

            check { failIf(event.message.content.trim().isEmpty()) }
            check { failIf(event.message.interaction != null) }
            check { failIf(event.message.data.authorId == event.kord.selfId) }
            check { failIf(event.message.author?.isBot == true) }

            check { failIfNot(event.message.channelId == getChannel(event.guild?.asGuild())?.id) }

            action {
                val id = event.message.id
                val suggestion = Suggestion(
                    id = id,
                    guildId = event.guildId!!,
                    channelId = event.message.channelId,
                    text = event.message.content,

                    owner = event.pkMessage.sender,
                    ownerAvatar = event.pkMessage.member?.avatarUrl,
                    ownerName = event.pkMessage.member?.name
                        ?: event.pkMessage.system?.name
                        ?: "${event.pkMessage.sender} (Unknown)",

                    positiveVoters = mutableListOf(event.pkMessage.sender),

                    isPluralkit = true
                )

                val autoRemovals = mutableListOf<AutoRemoval>()
                config.auto_answer.forEach {
                    autoRemovals.add(it.toAutoRemoval())
                }

                for (autoRemoval in autoRemovals) {
                    if (autoRemoval.regex.containsMatchIn(suggestion.text)) {
                        suggestion.status = autoRemoval.status
                        suggestion.comment = "(Automatic response) " + autoRemoval.reason
                        break
                    }
                }

                if (checkSuggestionLength(suggestion, event)) {
                    suggestions.set(suggestion)
                    sendSuggestion(suggestion)
                    event.message.delete()
                }
            }
        }

        event<MessageCreateEvent> {
            check { failIfNot(event.message.channelId == getChannel(event.message.getGuild())?.id) }
            check { failIfNot(event.message.type == MessageType.ThreadCreated) }

            action {
                event.message.deleteIgnoringNotFound()
            }
        }

        event<InteractionCreateEvent> {
            check { failIfNot(event.interaction is ButtonInteraction) }
            check { failIfNot(topChannelFor(event)!!.id == getChannel(guildFor(event)?.asGuild())?.id) }

            action {
                val interaction = event.interaction as ButtonInteraction

                if ("/" !in interaction.componentId) {
                    return@action
                }

                val split = interaction.componentId.split('/', limit = 2)

                val id = Snowflake(split[0])
                val action = split[1]

                val suggestion = suggestions.get(id) ?: return@action
                val response = interaction.ackEphemeral(false)

                if (suggestion.status != SuggestionStatus.Open) {
                    response.createEphemeralFollowup {
                        content = "**Error:** This suggestion isn't open, and votes can't be changed."
                    }

                    return@action
                }

                when (action) {
                    ACTION_UP -> if (!suggestion.positiveVoters.contains(interaction.user.id)) {
                        suggestion.positiveVoters.add(interaction.user.id)
                        suggestion.negativeVoters.remove(interaction.user.id)

                        response.createEphemeralFollowup {
                            content = "Vote registered!"
                        }
                    } else {
                        response.createEphemeralFollowup {
                            content = "**Error:** You've already upvoted this suggestion."
                        }

                        return@action
                    }

                    ACTION_DOWN -> if (!suggestion.negativeVoters.contains(interaction.user.id)) {
                        suggestion.negativeVoters.add(interaction.user.id)
                        suggestion.positiveVoters.remove(interaction.user.id)

                        response.createEphemeralFollowup {
                            content = "Vote registered!"
                        }
                    } else {
                        response.createEphemeralFollowup {
                            content = "**Error:** You've already downvoted this suggestion."
                        }

                        return@action
                    }

                    ACTION_REMOVE -> if (suggestion.positiveVoters.contains(interaction.user.id)) {
                        suggestion.positiveVoters.remove(interaction.user.id)

                        response.createEphemeralFollowup {
                            content = "Vote removed!"
                        }
                    } else if (suggestion.negativeVoters.contains(interaction.user.id)) {
                        suggestion.negativeVoters.remove(interaction.user.id)

                        response.createEphemeralFollowup {
                            content = "Vote removed!"
                        }
                    } else {
                        response.createEphemeralFollowup {
                            content = "**Error:** You haven't voted for this suggestion."
                        }

                        return@action
                    }

                    else -> response.createEphemeralFollowup {
                        content = "Unknown action: $action"

                        return@action
                    }
                }

                suggestions.set(suggestion)
                sendSuggestion(suggestion)
            }
        }

        event<ThreadChannelCreateEvent> {
            check { failIfNot(event.channel.parent.id == getChannel(event.channel.guild.asGuild())?.id) }

            check { failIf(event.channel.ownerId == kord.selfId) }

            action {
                event.channel.delete("Suggestion thread not created by Cozy")

                event.channel.owner.asUser().dm {
                    content = "I've removed your thread - please note that suggestion threads are only " +
                            "meant to be created automatically, you shouldn't create your own."
                }
            }
        }

        event<GuildModalSubmitInteractionCreateEvent> {
            check { failIf(event.interaction.modalId != "suggestions:submit") }

            action {
                val resp = event.interaction.deferEphemeralResponse()

                val interactionId = event.interaction.id
                val guildId = event.interaction.data.guildId.value!!
                val channelId = event.interaction.channelId

                var currentText = 0
                fun nextRow() = event.interaction.actionRows[currentText++]

                val title = nextRow().data.value.value!!
                val text = nextRow().data.value.value!!
                val problem = nextRow().data.value.value
                val solution = nextRow().data.value.value

                val pkMemberId = nextRow().data.value.value
                val pkMember = if (!pkMemberId.isNullOrBlank()) {
                    val url = "$PK_BASE_URL/members/$pkMemberId"
                    val response = httpClient.get(url).bodyAsText()
                    Json.decodeFromString(PKMember.serializer(), response)
                } else {
                    null
                }

                val owner = event.interaction.user
                val ownerAvatar = pkMember?.avatarUrl ?: owner.avatar?.cdnUrl?.toUrl()
                val ownerName = if (pkMember != null) {
                    "${pkMember.displayName ?: pkMember.name} (${owner.tag})"
                } else {
                    owner.tag
                }

                val suggestion = Suggestion(
                    id = interactionId,
                    guildId = guildId,
                    channelId = channelId,

                    status = SuggestionStatus.Open,
                    text = text,
                    problem = problem?.ifBlank { null },
                    solution = solution?.ifBlank { null },

                    owner = owner.id,
                    ownerAvatar = ownerAvatar,
                    ownerName = ownerName,

                    isPluralkit = pkMember != null,
                )

                val autoRemovals = mutableListOf<AutoRemoval>()
                config.auto_answer.forEach {
                    autoRemovals.add(it.toAutoRemoval())
                }

                for (autoRemoval in autoRemovals) {
                    if (autoRemoval.regex.containsMatchIn(suggestion.text)) {
                        suggestion.status = autoRemoval.status
                        suggestion.comment = "(Automatic response) " + autoRemoval.reason
                        break
                    }
                }

                if (checkSuggestionLength(suggestion, null)) {
                    suggestions.set(suggestion)
                    sendSuggestion(suggestion, title)

                    val threadId = suggestion.thread
                    if (threadId != null) {
                        val thread = kord.getChannelOf<ThreadChannel>(threadId)
                        thread?.edit {
                            name = title
                        }
                    }

                    resp.respond {
                        content = "Suggestion submitted!"
                    }

                    // shh, I'm totally not just hiding extra data
                    // I don't even know what the hell this means
                    // I don't know if I should keep it or not
                    val lastSuggestionMessageId = event.interaction.actionRows.first()
                        .data.customId.let { if (!it.value.isNullOrEmpty()) Snowflake(it.value!!) else null }

                    resendSuggestionMessage(kord.getChannelOf(channelId)!!, lastSuggestionMessageId)
                } else {
                    resp.respond {
                        content = "Your suggestion is too long. Please shorten it and try again."
                    }
                }
            }
        }

        event<GuildModalSubmitInteractionCreateEvent> {
            check { failIf(event.interaction.modalId != "suggestions:initial-message") }

            action {
                val resp = event.interaction.deferEphemeralResponse()

                var currentObj = 0
                fun nextRow() = event.interaction.actionRows[currentObj++]

                val channel = kord.getChannelOf<TopGuildMessageChannel>(Snowflake(nextRow().data.value.value!!))!!
                val title = nextRow().data.value.value
                val description = nextRow().data.value.value

                // Setup channel permissions
                val currentPerms = channel.permissionOverwrites.firstOrNull { it.target == channel.guildId }
                    ?: PermissionOverwrite.forEveryone(channel.guildId)

                channel.editRolePermission(channel.guildId) {
                    allowed = currentPerms.allowed.copy {
                        -Permission.SendMessages
                        -Permission.AddReactions
                        +Permission.SendMessagesInThreads
                    }
                    denied = currentPerms.denied.copy {
                        +Permission.SendMessages
                        +Permission.AddReactions
                        -Permission.SendMessagesInThreads
                    }
                }

                val modRoles = mutableSetOf<Snowflake>()
                config.administrators.getRoles().forEach {
                    modRoles.add(it.snowflake())
                }

                for (role in modRoles.filter { it in channel.getGuild().roleIds }) {
                    channel.editRolePermission(role) {
                        allowed += Permission.SendMessages
                        denied -= Permission.SendMessages
                    }
                }

                // Create initial message
                channel.createMessage {
                    embed {
                        this.title = title
                        this.description = description
                    }

                    actionRow {
                        interactionButton(ButtonStyle.Primary, "suggestions:create") {
                            label = "Create a suggestion"
                            emoji(ReactionEmoji.Unicode("\uD83D\uDCDD"))
                        }
                    }
                }

                resp.respond {
                    content = "Done"
                }
            }
        }

        event<ButtonInteractionCreateEvent> {
            check { failIf(topChannelFor(event)!!.id != getChannel(guildFor(event)?.asGuild())?.id) }
            check { failIfNot(event.interaction.componentId == "suggestions:create") }

            action {
                try {
                    event.interaction.modal("Submit a suggestion!", "suggestions:submit") {
                        // the custom id for the first input is used to store the last suggestion message id
                        actionRow {
                            val lastSuggestionMessageId = event.interaction.message.id.toString()
                            textInput(TextInputStyle.Short, lastSuggestionMessageId, "Title") {
                                placeholder = "My amazing suggestion"
                                allowedLength = 1..50
                                required = true
                            }
                        }

                        actionRow {
                            textInput(TextInputStyle.Paragraph, "text", "Main text") {
                                placeholder =
                                    "This is absolutely a great idea, and I think it should be implemented because..."
                                required = true
                            }
                        }

                        actionRow {
                            textInput(TextInputStyle.Paragraph, "problem", "Problem") {
                                placeholder = "The issue is..."
                                allowedLength = 0..FIELD_SIZE_LIMIT
                                required = false
                            }
                        }

                        actionRow {
                            textInput(TextInputStyle.Paragraph, "solution", "Solution") {
                                placeholder = "My proposed solution is..."
                                allowedLength = 0..FIELD_SIZE_LIMIT
                                required = false
                            }
                        }

                        actionRow {
                            textInput(TextInputStyle.Short, "pkid", "PluralKit Member ID") {
                                placeholder = "Only used if you're part of a system on PluralKit"
                                allowedLength = 5..5
                                required = false
                            }
                        }
                    }
                } catch (e: KtorRequestException) {
                    if (e.error?.code != JsonErrorCode.InteractionAlreadyAcknowledged) {
                        // "Interaction already acknowledged" is an issue with modals - no real way to fix it
                        // but all other errors should be reported
                        throw e
                    }
                }
            }
        }

        ephemeralSlashCommand {
            group(Translations.Commands.suggestions) {
                description = Translations.Commands.Suggestions.description
            }

            ephemeralSubCommand {
                name = Translations.Commands.Suggestions.spreadsheet
                description = Translations.Commands.Suggestions.Spreadsheet.description

                allowInDms = false

                check { isBotModuleAdmin(config.administrators) }

                action {
                    val suggestions = suggestions.find { exists(SuggestionTable.select(SuggestionTable.id)) }.toList()
                    val outputStream = ByteArrayOutputStream()

                    val book = workbook {
                        sheet("Suggestions") {
                            suggestionHeader()

                            suggestions.forEach { suggestionRow(fromRow(it)) }
                        }
                    }

                    book.xssfWorkbook.write(outputStream)

                    respond {
                        content = "Wrote ${suggestions.size} suggestions to an Excel spreadsheet."

                        addFile(
                            "suggestions.xlsx",
                            ChannelProvider { ByteArrayInputStream(outputStream.toByteArray()).toByteReadChannel() }
                        )
                    }
                }
            }

            ephemeralSubCommand(::SuggestionEditArguments) {
                name = Translations.Commands.Suggestions.edit
                description = Translations.Commands.Suggestions.Edit.description

                allowInDms = false

                action {
                    if (arguments.suggestion.owner != user.id) {
                        respond {
                            content = "**Error:** You don't own that suggestion."
                        }

                        return@action
                    }

                    if (arguments.problem != null) {
                        arguments.suggestion.problem = arguments.problem!!
                    }

                    if (arguments.solution != null) {
                        arguments.suggestion.solution = arguments.solution!!
                    }

                    if (arguments.text != null) {
                        arguments.suggestion.text = arguments.text!!
                    }

                    suggestions.set(arguments.suggestion)
                    sendSuggestion(arguments.suggestion)

                    respond {
                        content = "Suggestion updated."
                    }
                }
            }

            ephemeralSubCommand {
                name = Translations.Commands.Suggestions.manage
                description = Translations.Commands.Suggestions.Manage.description

                check { isBotModuleAdmin(config.administrators) }

                ephemeralSubCommand(::SuggestionStateArguments) {
                    name = Translations.Commands.Suggestions.Manage.state
                    description = Translations.Commands.Suggestions.Manage.State.description

                    allowInDms = false

                    check { isBotModuleAdmin(config.administrators) }

                    action {
                        val status = arguments.status

                        if (status != null) {
                            arguments.suggestion.status = status
                        }

                        if (arguments.comment != null) {
                            arguments.suggestion.comment = if (arguments.comment!!.lowercase() in CLEAR_WORDS) {
                                null
                            } else {
                                arguments.comment
                            }
                        }

                        suggestions.set(arguments.suggestion)
                        sendSuggestion(arguments.suggestion)
                        sendSuggestionUpdateMessage(arguments.suggestion)

                        respond {
                            content = "Suggestion updated."
                        }
                    }
                }

                ephemeralSubCommand(::SuggestionCannedResponseArguments) {
                    name = Translations.Commands.Suggestions.Manage.autoResponse
                    description = Translations.Commands.Suggestions.Manage.AutoResponse.description

                    check { isBotModuleAdmin(config.administrators) }

                    action {
                        val suggestion = arguments.suggestion
                        val responseId = arguments.id

                        val responses = mutableListOf<AutoRemoval>()
                        config.auto_answer.forEach {
                            responses.add(it.toAutoRemoval())
                        }

                        val response = responses.first { it.id == responseId }

                        suggestion.status = response.status
                        suggestion.comment = response.reason

                        suggestions.set(suggestion)
                        sendSuggestion(suggestion)
                        sendSuggestionUpdateMessage(suggestion)
                    }
                }
            }
        }


        unsafeSlashCommand {
            name = Translations.Commands.Suggestions.refresh
            description = Translations.Commands.Suggestions.Refresh.description
            // "Warning: you may not be able to respond in time!"
            initialResponse = InitialSlashCommandResponse.None

            check { isBotModuleAdmin(config.administrators) }

            action {
                val channel = getChannel(guild?.asGuild())

                event.interaction.modal("Refresh suggestion channel", "suggestions:initial-message") {
                    actionRow {
                        textInput(TextInputStyle.Short, "channel", "Channel snowflake") {
                            placeholder = "Snowflake"
                            value = channel?.toString()
                            allowedLength = 18..20
                            required = true
                        }
                    }

                    actionRow {
                        textInput(TextInputStyle.Short, "message", "Title") {
                            placeholder = "A short, descriptive title"
                            value = "Suggestion channel"
                            allowedLength = 1..128
                            required = true
                        }
                    }

                    actionRow {
                        textInput(TextInputStyle.Paragraph, "description", "Description") {
                            placeholder = "A longer description"
                            value = "This channel is used to submit suggestions. " +
                                    "Click the button below to submit a suggestion."

                            allowedLength = 1..TEXT_SIZE_LIMIT
                            required = true
                        }
                    }
                }
            }
        }


//			subCommand(::SuggestionSearchArguments) {
//				name = "search"
//				description = "Search through the submitted suggestions"
//
//				COMMUNITY_MANAGEMENT_ROLES.forEach(::allowRole)
//
//				action {
//
//				}
//			}
    }

    private fun Sheet.suggestionHeader() {
        val headings = listOf("ID", "Status", "Text", "+", "-", "=", "Staff Comment")

        val style = createCellStyle {
            setFont(
                createFont {
                    color = IndexedColors.WHITE.index
                    bold = true
                }
            )

            fillPattern = FillPatternType.SOLID_FOREGROUND
            fillForegroundColor = IndexedColors.BLACK.index
        }

        row(style) {
            headings.forEach(::cell)
        }
    }

    private fun Sheet.suggestionRow(suggestion: Suggestion) {
        val statusStyle = createCellStyle {
            setFont(
                createFont {
                    val color = XSSFColor(
                        byteArrayOf(
                            suggestion.status.color.red.toByte(),
                            suggestion.status.color.green.toByte(),
                            suggestion.status.color.blue.toByte()
                        )
                    )

                    setColor(color)
                }
            )
        }

        row {
            cell(suggestion.id.toString())
            cell(suggestion.status.readableName, statusStyle)
            cell(suggestion.text)
            cell(suggestion.positiveVotes)
            cell(suggestion.negativeVotes)
            cell(suggestion.voteDifference)
            cell(suggestion.comment ?: "")
        }
    }

    suspend fun checkSuggestionLength(suggestion: Suggestion, event: MessageEvent?): Boolean {
        if (suggestion.text.length > SUGGESTION_SIZE_LIMIT) {
            val user = kord.getUser(suggestion.owner)

            val resentText = if (suggestion.text.length > 1800) {
                suggestion.text.substring(0, 1797) + "..."
            } else {
                suggestion.text
            }

            val errorMessage = "The suggestion you posted was too long (${suggestion.text.length} / " +
                    "$SUGGESTION_SIZE_LIMIT characters)\n\n```\n$resentText\n```"

            val dm = user?.dm {
                content = errorMessage
            }

            if (event == null) return false

            if (dm != null) {
                event.message?.delete()
            } else {
                event.message?.reply {
                    content = errorMessage
                }?.delete(THIRTY_SECONDS.toString())

                event.message?.delete(THIRTY_SECONDS.toString())
            }

            return false
        }
        return true
    }

    suspend fun sendSuggestion(suggestion: Suggestion, name: String? = null) {
        val channel = kord.getChannelOf<GuildMessageChannel>(suggestion.channelId)!!

        // interpret the text to see if it has a problem/solution word pair
        if (suggestion.problem == null && "problem: " in suggestion.text.lowercase()) {
            val regex = Regex(
                """^(.*?\n?)?problem: (.*?)(?:\nsolution: (.*?))?$""",
                setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
            )

            val match = regex.matchEntire(suggestion.text) ?: run {
                logger.warn("Failed to parse problem/solution from suggestion text: ${suggestion.text}")
                return
            }

            suggestion.text = ZERO_WIDTH_SPACE

            if (match.groupValues[1].isNotBlank()) {
                suggestion.text = match.groupValues[1].trim()
            }

            suggestion.problem = match.groupValues[2].trim()

            if (match.groups.size > 2) {
                suggestion.solution = match.groupValues[3].trim()
            }

            suggestions.set(suggestion) // update db so i don't bork it
        }

        if (suggestion.message == null) {
            val message = channel.createMessage { suggestion(suggestion) }

            val archiveDuration = kord.rest.channel.getChannel(channel.id).defaultAutoArchiveDuration
            val thread = (channel as? TextChannel)?.startPublicThreadWithMessage(
                message.id,
                name = name ?: suggestion.id.toString()
            ) {
                autoArchiveDuration = archiveDuration.value
                reason = null
            }

            if (thread != null) {
                @Suppress("TooGenericExceptionCaught", "PrintStackTrace")
                try {
                    val threadMessage = thread.createMessage {
                        suggestion(suggestion, sendEmbed = false)

                        content = threadIntro(suggestion.guildId)
                    }

                    threadMessage.pin()

                    thread.addUser(suggestion.owner)

                    threads.set(
                        OwnedThread(
                            thread.id,
                            suggestion.owner,
                            thread.guildId
                        )
                    )

                    suggestion.thread = thread.id
                    suggestion.threadButtons = threadMessage.id

                    thread.edit {
                        archived = true
                        locked = false // allow anyone to un-archive
                    }
                } catch (e: Exception) {
                    logger.error("Failed to create thread for suggestion")
                    e.printStackTrace() // Temporary StackTrace until BBAPI supports it
                }
            }

            suggestion.message = message.id

            suggestions.set(suggestion)
        } else {
            val message = channel.getMessage(suggestion.message!!)

            message.edit { suggestion(suggestion) }

            if (suggestion.thread != null && suggestion.threadButtons != null) {
                val thread = (channel as? TextChannel)?.activeThreads?.toList()?.firstOrNull {
                    it.id == suggestion.thread
                }

                val threadMessage = thread?.getMessage(suggestion.threadButtons!!)

                threadMessage?.edit {
                    suggestion(suggestion, false)

                    content = threadIntro(suggestion.guildId)
                }
            }
        }
    }

    suspend fun sendSuggestionUpdateMessage(suggestion: Suggestion) {
        val user = kord.getUser(suggestion.owner) ?: return

        val suggestionMessage = if (suggestion.message != null) {
            kord.getGuildOrNull(suggestion.guildId)
                ?.getChannelOf<GuildMessageChannel>(suggestion.channelId)
                ?.getMessageOrNull(suggestion.message!!)
        } else {
            null
        }

        user.dm {
            embed {
                color = suggestion.status.color
                title = "Suggestion updated"

                description = if (suggestionMessage != null) {
                    "[Suggestion ${suggestion.id.value}](${suggestionMessage.getJumpUrl()}) "
                } else {
                    "Suggestion ${suggestion.id.value} "
                }

                description += "has been updated.\n\n" +
                        "**__Suggestion__**\n\n" +
                        suggestion.text

                description += "\n\n**Status:** ${suggestion.status.readableName}\n"

                if (suggestion.comment != null) {
                    description += "\n" +
                            "**__Staff response__**\n\n" +
                            suggestion.comment
                }
            }
        }

        if (suggestion.thread != null) {
            kord.getChannelOf<ThreadChannel>(suggestion.thread!!)?.createMessage {
                content = "**__Suggestion updated__**\n" +
                        "**Status:** ${suggestion.status.readableName}\n"

                if (suggestion.comment != null) {
                    content += "\n" +
                            "**__Staff response__**\n\n" +
                            suggestion.comment
                }
            }
        }
    }

    suspend fun resendSuggestionMessage(channel: TopGuildMessageChannel, prevMessageId: Snowflake?) {
        val prevMessage = prevMessageId?.let { channel.getMessageOrNull(it) }

        channel.createMessage {
            embed {
                if (prevMessage != null) {
                    prevMessage.embeds.first().apply(this)
                } else {
                    title = "Suggestion"
                    description = "Suggest something by clicking the button below."
                }
            }

            actionRow {
                interactionButton(ButtonStyle.Primary, "suggestions:create") {
                    label = "Create a suggestion"
                    emoji(ReactionEmoji.Unicode("\uD83D\uDCDD"))
                }
            }
        }

        prevMessage?.delete()
    }

    fun MessageCreateBuilder.suggestion(suggestion: Suggestion, sendEmbed: Boolean = true) {
        val id = suggestion.id.value

        if (sendEmbed) {
            embed {
                author {
                    name = suggestion.ownerName
                    icon = suggestion.ownerAvatar
                }

                description = if (suggestion.isPluralkit) {
                    "@${suggestion.ownerName} (<@${suggestion.owner.value}>)\n\n"
                } else {
                    "<@${suggestion.owner.value}>\n\n"
                }

                description += "${suggestion.text}\n\n"

                if (suggestion.problem != null) {
                    field {
                        name = "Problem"
                        value = suggestion.problem!!
                    }
                    if (suggestion.solution != null) {
                        field {
                            name = "Solution"
                            value = suggestion.solution!!
                        }
                    }
                }

                field {
                    name = "Votes"
                    value = ""

                    if (suggestion.positiveVotes > 0) {
                        value += "**Upvotes:** ${suggestion.positiveVotes}\n"
                    }

                    if (suggestion.negativeVotes > 0) {
                        value += "**Downvotes:** ${suggestion.negativeVotes}\n"
                    }

                    value += "**Total:** ${suggestion.voteDifference}"
                }

                if (suggestion.comment != null) {
                    field {
                        name = "Staff response"
                        value = suggestion.comment!!
                    }
                }

                color = suggestion.status.color

                footer {
                    text = "Status: ${suggestion.status.readableName} • ID: $id"
                }
            }
        }

        if (suggestion.status == SuggestionStatus.Open) {
            actionRow {
                interactionButton(ButtonStyle.Primary, "$id/$ACTION_UP") {
                    emoji(EMOTE_UPVOTE)

                    label = "Upvote"
                }

                interactionButton(ButtonStyle.Primary, "$id/$ACTION_DOWN") {
                    emoji(EMOTE_DOWNVOTE)

                    label = "Downvote"
                }

                interactionButton(ButtonStyle.Danger, "$id/$ACTION_REMOVE") {
                    emoji(EMOTE_REMOVE)

                    label = "Retract vote"
                }
            }
        }
    }

    fun MessageModifyBuilder.suggestion(suggestion: Suggestion, sendEmbed: Boolean = true) {
        val id = suggestion.id.value

        if (sendEmbed) {
            embed {
                author {
                    name = suggestion.ownerName
                    icon = suggestion.ownerAvatar
                }

                description = if (suggestion.isPluralkit) {
                    "@${suggestion.ownerName} (<@${suggestion.owner.value}>)\n\n"
                } else {
                    "<@${suggestion.owner.value}>\n\n"
                }

                description += "${suggestion.text}\n\n"

                if (suggestion.problem != null) {
                    field {
                        name = "Problem"
                        value = suggestion.problem!!
                    }
                    if (suggestion.solution != null) {
                        field {
                            name = "Solution"
                            value = suggestion.solution!!
                        }
                    }
                }

                field {
                    name = "Votes"
                    value = ""

                    if (suggestion.positiveVotes > 0) {
                        value += "**Upvotes:** ${suggestion.positiveVotes}\n"
                    }

                    if (suggestion.negativeVotes > 0) {
                        value += "**Downvotes:** ${suggestion.negativeVotes}\n"
                    }

                    value += "**Total:** ${suggestion.voteDifference}"
                }

                if (suggestion.comment != null) {
                    field {
                        name = "Staff response"
                        value = suggestion.comment!!
                    }
                }

                color = suggestion.status.color

                footer {
                    text = "Status: ${suggestion.status.readableName} • ID: $id"
                }
            }
        }

        if (suggestion.status == SuggestionStatus.Open) {
            actionRow {
                interactionButton(ButtonStyle.Primary, "$id/$ACTION_UP") {
                    emoji(EMOTE_UPVOTE)

                    label = "Upvote"
                }

                interactionButton(ButtonStyle.Primary, "$id/$ACTION_DOWN") {
                    emoji(EMOTE_DOWNVOTE)

                    label = "Downvote"
                }

                interactionButton(ButtonStyle.Danger, "$id/$ACTION_REMOVE") {
                    emoji(EMOTE_REMOVE)

                    label = "Retract vote"
                }
            }
        } else if (suggestion.status != SuggestionStatus.Open) {
            components = mutableListOf()
        }
    }

    @OptIn(NotTranslated::class)
    class SuggestionEditArguments : Arguments() {
        val suggestion: Suggestion by suggestion {
            name = Translations.Commands.Suggestions.GeneralArgs.suggestion
            description = Translations.Commands.Suggestions.GeneralArgs.Suggestion.description

//            autocomplete(onlyUser = true)
        }

        val text by optionalString {
            name = Translations.Commands.Suggestions.Edit.Arg.text
            description = Translations.Commands.Suggestions.Edit.Arg.Text.description

            validate {
                if (value != null && value!!.length > SUGGESTION_SIZE_LIMIT) {
                    fail("Suggestion text must not be longer than $SUGGESTION_SIZE_LIMIT characters.")
                }
            }
        }

        val problem by optionalString {
            name = Translations.Commands.Suggestions.Edit.Arg.problem
            description = Translations.Commands.Suggestions.Edit.Arg.Problem.description

            validate {
                if (value != null && value!!.length > FIELD_SIZE_LIMIT) {
                    fail("Problem text must not be longer than $FIELD_SIZE_LIMIT characters.")
                }
            }
        }

        val solution by optionalString {
            name = Translations.Commands.Suggestions.Edit.Arg.solution
            description = Translations.Commands.Suggestions.Edit.Arg.Solution.description

            validate {
                if (value != null && value!!.length > FIELD_SIZE_LIMIT) {
                    fail("Solution text must not be longer than $FIELD_SIZE_LIMIT characters.")
                }
            }
        }
    }

//	inner class SuggestionSearchArguments : Arguments() {
//		val status by defaultingEnumChoice<SuggestionStatus>(
//			"status",
//			"Status to check for, defaulting to Approved",
//			"Status",
//			SuggestionStatus.Approved
//		)
//
//		val sentiment by optionalEnumChoice<SuggestionSentiment>(
//			"sentiment",
//			"How the community voted",
//			"Sentiment"
//		)
//
//		val user by optionalUser("user", "Suggestion creator")
//		val suggestion by optionalSuggestion("suggestion", "Suggestion ID to search for")
//
//		val text by optionalCoalescingString("text", "Text to search for in the description")
//	}

    @OptIn(NotTranslated::class)
    class SuggestionStateArguments : Arguments() {
        val suggestion by suggestion {
            name = Translations.Commands.Suggestions.GeneralArgs.suggestion
            description = Translations.Commands.Suggestions.GeneralArgs.Suggestion.description

//            autocomplete()
        }

        val status by optionalEnumChoice<SuggestionStatus> {
            name = Translations.Commands.Suggestions.GeneralArgs.status
            description = Translations.Commands.Suggestions.GeneralArgs.Status.description

            typeName = Translations.Commands.Suggestions.GeneralArgs.status
        }

        val comment by optionalString {
            name = Translations.Commands.Suggestions.Manage.State.Arg.comment
            description = Translations.Commands.Suggestions.Manage.State.Arg.Comment.description

            validate {
                if ((value?.length ?: -1) > COMMENT_SIZE_LIMIT) {
                    fail("Comment must not be longer than $COMMENT_SIZE_LIMIT characters.")
                }
            }
        }
    }

    @OptIn(NotTranslated::class)
    inner class SuggestionCannedResponseArguments : Arguments() {
        val suggestion by suggestion {
            name = Translations.Commands.Suggestions.GeneralArgs.suggestion
            description = Translations.Commands.Suggestions.GeneralArgs.Suggestion.description

//            autocomplete()
        }

        val id by string {
            name = Translations.Commands.Suggestions.Manage.AutoResponse.Arg.id
            description = Translations.Commands.Suggestions.Manage.AutoResponse.Arg.Id.description

            validate {
                failIf("ID must link to a valid auto response!") {
                    val list = mutableListOf<AutoRemoval>()
                    config.auto_answer.forEach {
                        list.add(it.toAutoRemoval())
                    }

                    list.none { it.id == value }
                }
            }

            autoComplete {
                val list = mutableListOf<AutoRemoval>()
                config.auto_answer.forEach {
                    list.add(it.toAutoRemoval())
                }

                val map = list.associate {
                    val key = it.id
                    val arg = "${it.status} - ${it.reason}"

                    @Suppress("MagicNumber")
                    (if (arg.length > 100) arg.substring(0..96) + "..." else arg) to key
                }

                suggestStringMap(map)
            }
        }
    }

    // Yeah, so, idc anymore, no autocomplete for suggestions. fuck you
//    private fun SuggestionConverterBuilder.autocomplete(onlyUser: Boolean = false) {
//        autoComplete { event ->
//            var partialText = focusedOption.value
//            val suggestionFilter = if (partialText.matches("^[a-z]+:.*".toRegex())) {
//                val allowedStatusText = partialText.substringBefore(':')
//                partialText = partialText.substringAfter(':').trim()
//                val allowedStatus = SuggestionStatus.entries.find { it.readableName.translate() == allowedStatusText }
//
//                if (allowedStatus != null) {
//                    listOf(allowedStatus)
//                } else {
//                    listOf(SuggestionStatus.RequiresName, SuggestionStatus.Open)
//                }
//            } else {
//                listOf(SuggestionStatus.RequiresName, SuggestionStatus.Open)
//            }
//
//            val map = mutableMapOf<String, String>()
//
//            val requiredConditions = mutableListOf(Suggestion::status in suggestionFilter)
//            if (onlyUser) {
//                requiredConditions += Suggestion::owner eq user.id
//            }
//
//            run {
//                suggestions.find {  }.publisher.collect {
//                    // Using publisher.collect() to be able to easily escape the loop
//                    // since if using an inline function, you can exit by returning from a higher function
//                    // (in this case `run`)
//                    val id = it.id.toString()
//                    var arg = it.text
//
//                    if (arg.length > 100) {
//                        arg = arg.substring(0..96) + "..."
//                    }
//
//                    if (!arg.startsWith(partialText)) return@collect
//
//                    map[arg] = id
//
//                    if (map.size >= 25) return@run // jump out once we've collected enough
//                }
//            }
//
//            suggestStringMap(map)
//        }
//    }
}