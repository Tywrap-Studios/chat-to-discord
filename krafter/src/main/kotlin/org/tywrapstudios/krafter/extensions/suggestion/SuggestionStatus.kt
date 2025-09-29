/*
 * The Krafter Suggestion Extension was adapted from the Cozy Discord Bot.
 * The below is the license notice provided, but the latest version should always be available at the following
 * link: https://github.com/QuiltMC/cozy-discord/blob/root/LICENSE
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package org.tywrapstudios.krafter.extensions.suggestion

import dev.kord.common.Color
import dev.kordex.core.*
import dev.kordex.core.commands.application.slash.converters.ChoiceEnum
import dev.kordex.core.i18n.types.Key
import kotlinx.serialization.Serializable
import org.tywrapstudios.krafter.i18n.Translations.Enum.SuggestionStatus

@Serializable
enum class SuggestionStatus(override val readableName: Key, val color: Color) : ChoiceEnum {
    Open(SuggestionStatus.open, DISCORD_BLURPLE),
    RequiresName(SuggestionStatus.requiresName, DISCORD_FUCHSIA),

    Approved(SuggestionStatus.approved, DISCORD_FUCHSIA),

    Denied(SuggestionStatus.denied, DISCORD_RED),
    Invalid(SuggestionStatus.invalid, DISCORD_RED),
    Spam(SuggestionStatus.spam, DISCORD_RED),

    Future(SuggestionStatus.future, DISCORD_YELLOW),
    Stale(SuggestionStatus.stale, DISCORD_YELLOW),

    Duplicate(SuggestionStatus.duplicate, DISCORD_BLACK),
    Implemented(SuggestionStatus.implemented, DISCORD_GREEN),
}