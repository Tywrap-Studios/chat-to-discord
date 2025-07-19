package org.tywrapstudios.krafter

import dev.kord.common.entity.Snowflake
import dev.kordex.core.ExtensibleBot
import dev.kordex.core.utils.env
import dev.kordex.data.api.DataCollection
import dev.kordex.modules.func.phishing.extPhishing
import dev.kordex.modules.func.tags.tags
import dev.kordex.modules.pluralkit.extPluralKit
import org.tywrapstudios.blossombridge.api.config.ConfigManager
import org.tywrapstudios.krafter.config.BotConfig
import org.tywrapstudios.krafter.extensions.CustomTagsData
import java.io.File

private val TEST_SERVER_ID = Snowflake(env("TEST_SERVER").toLong())
private val TEST_TOKEN = env("TEST_TOKEN")
lateinit var CFG: ConfigManager<BotConfig>

private suspend fun setup(token: String, manager: ConfigManager<BotConfig>) = ExtensibleBot(token) {
    CFG = manager
    CFG.loadConfig()
    val config = CFG.getConfig()

    chatCommands {
        defaultPrefix = config.prefix
        enabled = true

        prefix { default ->
            if (guildId == TEST_SERVER_ID) {
                "?>"
            } else {
                default
            }
        }
    }

    applicationCommands {
        defaultGuild = TEST_SERVER_ID
    }

    extensions {

        if (config.miscellaneous.plural_kit.enabled) extPluralKit()
        if (config.safety_and_abuse.block_phishing) extPhishing{
            for (domain in config.safety_and_abuse.banned_domains) badDomain(domain)
        }
        if (config.miscellaneous.tags.enabled) tags(CustomTagsData()) {}
//        if (config.miscellaneous.suggestion_forum) add { SuggestionExtension() }
//        if (config.miscellaneous.ama.enabled) ama()
//        if (config.miscellaneous.crash_analysing.enabled) extLogParser{}
//        if (config.safety_and_abuse.user_cleanup) extUserCleanup{}

    }

    dataCollectionMode = DataCollection.fromDB(config.safety_and_abuse.data_collection)

    presence {
        playing("on ${config.status.server_name}")
    }
}

/**
 * Runs the bot, no setup needed beforehand.
 * @param token The Discord bot token from the developer portal.
 * @param manager A [ConfigManager] that handles the [BotConfig] for the bot.
 */
fun run(token: String, manager: ConfigManager<BotConfig>) {
    suspend {
        val bot = setup(token, manager)
        if (CFG.getConfig().enabled) bot.start()
    }
}

/**
 * Runs a test version of the bot. Provide the bot with a token in an .env file ([TEST_TOKEN]) and configure as needed.
 */
fun main() {
    val manager = ConfigManager(BotConfig::class.java, File("./krafter.json5"))
    run(TEST_TOKEN, manager)
}