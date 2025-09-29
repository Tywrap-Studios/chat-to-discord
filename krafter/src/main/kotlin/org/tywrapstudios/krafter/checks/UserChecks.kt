package org.tywrapstudios.krafter.checks

import dev.kord.common.entity.Snowflake
import dev.kordex.core.checks.failed
import dev.kordex.core.checks.nullMember
import dev.kordex.core.checks.passed
import dev.kordex.core.checks.types.CheckContext
import dev.kordex.core.checks.userFor
import io.github.oshai.kotlinlogging.KotlinLogging
import org.tywrapstudios.krafter.i18n.Translations
import org.tywrapstudios.krafter.snowflake

suspend fun CheckContext<*>.hasId(id: Snowflake) {
    if (!passed) {
        return
    }

    val logger = KotlinLogging.logger("org.tywrapstudios.krafter.checks.hasId")
    val user = userFor(event)

    if (user == null) {
        logger.nullMember(event)

        fail()
    } else {
        val userObj = user.asUser()

        val result = userObj.id == id

        if (result) {
            logger.passed()

            pass()
        } else {
            logger.failed("User $user does not have id $id")

            fail(
                Translations.Checks.HasId.failed
                    .withLocale(locale)
                    .withOrdinalPlaceholders(id)
            )
        }
    }
}

suspend fun CheckContext<*>.notHasId(id: Snowflake) {
    if (!passed) {
        return
    }

    val logger = KotlinLogging.logger("org.tywrapstudios.krafter.checks.hasId")
    val user = userFor(event)

    if (user == null) {
        logger.nullMember(event)

        fail()
    } else {
        val userObj = user.asUser()

        val result = userObj.id == id

        if (result) {
            logger.failed("User $user does not have id $id")

            fail(
                Translations.Checks.NotHasId.failed
                    .withLocale(locale)
                    .withOrdinalPlaceholders(id)
            )
        } else {
            logger.passed()

            pass()
        }
    }
}

suspend fun CheckContext<*>.hasId(id: String) {
    if (!passed) {
        return
    }

    val id = Snowflake(id)
    hasId(id)
}

suspend fun CheckContext<*>.notHasId(id: String) {
    if (!passed) {
        return
    }

    val id = Snowflake(id)
    notHasId(id)
}

suspend fun CheckContext<*>.hasId(id: ULong) {
    if (!passed) {
        return
    }

    val id = id.snowflake()
    hasId(id)
}

suspend fun CheckContext<*>.notHasId(id: ULong) {
    if (!passed) {
        return
    }

    val id = id.snowflake()
    notHasId(id)
}