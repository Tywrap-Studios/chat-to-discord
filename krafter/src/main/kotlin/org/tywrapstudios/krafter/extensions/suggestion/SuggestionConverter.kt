package org.tywrapstudios.krafter.extensions.suggestion

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.interaction.OptionValue
import dev.kord.core.entity.interaction.StringOptionValue
import dev.kord.rest.builder.interaction.OptionsBuilder
import dev.kord.rest.builder.interaction.StringChoiceBuilder
import dev.kordex.core.DiscordRelayedException
import dev.kordex.core.annotations.converters.Converter
import dev.kordex.core.annotations.converters.ConverterType
import dev.kordex.core.commands.Argument
import dev.kordex.core.commands.CommandContext
import dev.kordex.core.commands.converters.SingleConverter
import dev.kordex.core.commands.converters.Validator
import dev.kordex.parser.StringParser
import org.koin.core.component.inject
import org.quiltmc.community.database.collections.SuggestionsCollection
import org.quiltmc.community.database.entities.Suggestion

@Converter(
    names = ["suggestion"],
    types = [ConverterType.SINGLE, ConverterType.OPTIONAL],
)
class SuggestionConverter(
    override var validator: Validator<Suggestion> = null
) : SingleConverter<Suggestion>() {
    override val signatureTypeString: String = "Suggestion ID"

    private val suggestions: SuggestionsCollection by inject()

    override suspend fun parse(parser: StringParser?, context: CommandContext, named: String?): Boolean {
        val arg: String = named ?: parser?.parseNext()?.data ?: return false

        try {
            val snowflake = Snowflake(arg)

            this.parsed = suggestions.get(snowflake)
                ?: suggestions.getByMessage(snowflake)
                        ?: throw DiscordRelayedException("Unknown suggestion ID: $arg")
        } catch (e: NumberFormatException) {
            throw DiscordRelayedException("Unknown suggestion ID: $arg")
        }

        return true
    }

    override suspend fun toSlashOption(arg: Argument<*>): OptionsBuilder =
        StringChoiceBuilder(arg.displayName, arg.description).apply { required = true }

    override suspend fun parseOption(context: CommandContext, option: OptionValue<*>): Boolean {
        val arg = (option as? StringOptionValue)?.value ?: return false

        try {
            val snowflake = Snowflake(arg)

            this.parsed = suggestions.get(snowflake)
                ?: suggestions.getByMessage(snowflake)
                        ?: throw DiscordRelayedException("Unknown suggestion ID: $arg")
        } catch (e: NumberFormatException) {
            throw DiscordRelayedException("Unknown suggestion ID: $arg")
        }

        return true
    }
}