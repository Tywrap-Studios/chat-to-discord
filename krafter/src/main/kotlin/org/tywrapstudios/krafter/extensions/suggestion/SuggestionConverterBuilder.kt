package org.tywrapstudios.krafter.extensions.suggestion

import dev.kordex.core.annotations.UnexpectedFunctionBehaviour
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.ConverterToOptional
import dev.kordex.core.commands.converters.OptionalConverter
import dev.kordex.core.commands.converters.SingleConverter
import dev.kordex.core.commands.converters.builders.ConverterBuilder
import dev.kordex.core.commands.converters.builders.OptionalConverterBuilder
import org.tywrapstudios.krafter.database.entities.Suggestion

class SuggestionConverterBuilder : ConverterBuilder<Suggestion>() {
    @OptIn(UnexpectedFunctionBehaviour::class)
    override fun build(arguments: Arguments): SingleConverter<Suggestion> {
        val converter = SuggestionConverter(
            validator = validator
        )

        return arguments.arg(
            displayName = name,
            description = description,
            converter = converter.withBuilder(this)
        )
    }
}

public fun Arguments.suggestion(
    body: SuggestionConverterBuilder.() -> Unit
): SingleConverter<Suggestion> {
    val builder = SuggestionConverterBuilder()

    body(builder)

    builder.validateArgument()

    return builder.build(this)
}

class OptionalSuggestionConverterBuilder : OptionalConverterBuilder<Suggestion>() {
    @OptIn(UnexpectedFunctionBehaviour::class, ConverterToOptional::class)
    override fun build(arguments: Arguments): OptionalConverter<Suggestion> {
        val converter = SuggestionConverter(
            validator = validator
        )

        return arguments.arg(
            displayName = name,
            description = description,
            converter = converter.toOptional().withBuilder(this)
        )
    }
}

public fun Arguments.optionalSuggestion(
    body: OptionalSuggestionConverterBuilder.() -> Unit
): OptionalConverter<Suggestion> {
    val builder = OptionalSuggestionConverterBuilder()

    body(builder)

    builder.validateArgument()

    return builder.build(this)
}