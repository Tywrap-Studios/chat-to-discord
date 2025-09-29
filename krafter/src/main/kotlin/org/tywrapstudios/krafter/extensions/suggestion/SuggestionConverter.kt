/*
 * The Krafter Suggestion Extension was adapted from the Cozy Discord Bot.
 * The below is the license notice provided, but the latest version should always be available at the following
 * link: https://github.com/QuiltMC/cozy-discord/blob/root/LICENSE
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

@file:OptIn(KordPreview::class)

package org.tywrapstudios.krafter.extensions.suggestion

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.interaction.OptionValue
import dev.kord.core.entity.interaction.StringOptionValue
import dev.kordex.core.DiscordRelayedException
import dev.kordex.core.annotations.converters.Converter
import dev.kordex.core.annotations.converters.ConverterType
import dev.kordex.core.commands.Argument
import dev.kordex.core.commands.CommandContext
import dev.kordex.core.commands.OptionWrapper
import dev.kordex.core.commands.converters.SingleConverter
import dev.kordex.core.commands.converters.Validator
import dev.kordex.core.commands.wrapStringOption
import dev.kordex.core.i18n.types.Key
import dev.kordex.parser.StringParser
import org.koin.core.component.inject
import org.tywrapstudios.krafter.database.entities.Suggestion
import org.tywrapstudios.krafter.database.transactors.SuggestionsTransactor
import org.tywrapstudios.krafter.i18n.Translations

@Converter(
    names = ["suggestion"],
    types = [ConverterType.SINGLE, ConverterType.OPTIONAL],
)
class SuggestionConverter(
    override var validator: Validator<Suggestion> = null
) : SingleConverter<Suggestion>() {
    override val signatureType: Key = Translations.Converter.Suggestion.signatureType

    private val suggestions: SuggestionsTransactor by inject()

    override suspend fun parse(parser: StringParser?, context: CommandContext, named: String?): Boolean {
        val arg: String = named ?: parser?.parseNext()?.data ?: return false

        try {
            val snowflake = Snowflake(arg)

            this.parsed = suggestions.get(snowflake)
                ?: suggestions.getByMessage(snowflake)
                        ?: throw DiscordRelayedException(
                    Translations.Errors.Exceptions.unknownSuggestionId.withOrdinalPlaceholders(
                        arg
                    )
                )
        } catch (_: NumberFormatException) {
            throw DiscordRelayedException(
                Translations.Errors.Exceptions.unknownSuggestionId.withOrdinalPlaceholders(arg)
            )
        }

        return true
    }

    override suspend fun toSlashOption(arg: Argument<*>): OptionWrapper<*> =
        wrapStringOption(arg.displayName, arg.description) { required = true }

    override suspend fun parseOption(context: CommandContext, option: OptionValue<*>): Boolean {
        val arg = (option as? StringOptionValue)?.value ?: return false

        try {
            val snowflake = Snowflake(arg)

            this.parsed = suggestions.get(snowflake)
                ?: suggestions.getByMessage(snowflake)
                        ?: throw DiscordRelayedException(
                    Translations.Errors.Exceptions.unknownSuggestionId.withOrdinalPlaceholders(
                        arg
                    )
                )
        } catch (_: NumberFormatException) {
            throw DiscordRelayedException(
                Translations.Errors.Exceptions.unknownSuggestionId.withOrdinalPlaceholders(arg)
            )
        }

        return true
    }
}