package org.tywrapstudios.krafter.checks

import dev.kordex.core.checks.failed
import dev.kordex.core.checks.memberFor
import dev.kordex.core.checks.nullMember
import dev.kordex.core.checks.passed
import dev.kordex.core.checks.types.CheckContext
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.toList
import org.tywrapstudios.krafter.config
import org.tywrapstudios.krafter.config.BotConfig
import org.tywrapstudios.krafter.getRoles
import org.tywrapstudios.krafter.getUsers
import org.tywrapstudios.krafter.i18n.Translations

suspend fun CheckContext<*>.isBotModuleAdmin(list: BotConfig.AdministratorList) {
    if (!passed) {
        return
    }

    val logger = KotlinLogging.logger("org.tywrapstudios.krafter.checks.isBotModuleAdmin")
    val member = memberFor(event)

    if (member == null) {
        logger.nullMember(event)

        fail()
    } else {
        val memberObj = member.asMember()

        val result = when {
            !memberObj.roles.toList().none { list.getRoles().contains(it.id.value.toString()) } -> true
            list.getUsers().contains(member.id.value.toString()) -> true

            else -> false
        }

        if (result) {
            logger.passed()

            pass()
        } else {
            logger.failed("Member $member is not a bot module admin for the specified AdministratorList.")

            fail(
                Translations.Checks.IsBotModuleAdmin.failed
                    .withLocale(locale)
                    .withOrdinalPlaceholders(list.javaClass.name)
            )
        }
    }
}

suspend fun CheckContext<*>.notIsBotModuleAdmin(list: BotConfig.AdministratorList) {
    if (!passed) {
        return
    }

    val logger = KotlinLogging.logger("org.tywrapstudios.krafter.checks.notIsBotModuleAdmin")
    val member = memberFor(event)

    if (member == null) {
        logger.nullMember(event)

        fail()
    } else {
        val memberObj = member.asMember()

        val result = when {
            memberObj.roles.toList()
                .none { list.getRoles().contains(it.id.value.toString()) } && !list.getUsers()
                .contains(member.id.value.toString()) -> true

            else -> false
        }

        if (result) {
            logger.passed()

            pass()
        } else {
            logger.failed("Member $member is a bot module admin for the specified AdministratorList.")

            fail(
                Translations.Checks.NotIsBotModuleAdmin.failed
                    .withLocale(locale)
                    .withOrdinalPlaceholders(list.javaClass.name)
            )
        }
    }
}

suspend fun CheckContext<*>.isGlobalBotAdmin() {
    if (!passed) {
        return
    }

    isBotModuleAdmin(config().global_administrators)
}

suspend fun CheckContext<*>.notIsGlobalBotAdmin() {
    if (!passed) {
        return
    }

    notIsBotModuleAdmin(config().global_administrators)
}