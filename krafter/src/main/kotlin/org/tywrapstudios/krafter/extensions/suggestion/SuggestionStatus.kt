package org.tywrapstudios.krafter.extensions.suggestion

import dev.kord.common.Color
import dev.kordex.core.*
import dev.kordex.core.commands.application.slash.converters.ChoiceEnum
import dev.kordex.core.i18n.types.Key
import kotlinx.serialization.Serializable
import org.tywrapstudios.krafter.i18n.Translations.Enum.Suggestions.Status

@Serializable
enum class SuggestionStatus(override val readableName: Key, val color: Color) : ChoiceEnum {
    Open(Status.open, DISCORD_BLURPLE),
    RequiresName(Status.requiresName, DISCORD_FUCHSIA),

    Approved(Status.approved, DISCORD_FUCHSIA),

    Denied(Status.denied, DISCORD_RED),
    Invalid(Status.invalid, DISCORD_RED),
    Spam(Status.spam, DISCORD_RED),

    Future(Status.future, DISCORD_YELLOW),
    Stale(Status.stale, DISCORD_YELLOW),

    Duplicate(Status.duplicate, DISCORD_BLACK),
    Implemented(Status.implemented, DISCORD_GREEN),
}