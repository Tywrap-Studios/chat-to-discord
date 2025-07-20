package org.tywrapstudios.krafter

import dev.kord.common.entity.Snowflake
import dev.kordex.core.ExtensibleBot
import dev.kordex.core.utils.envOrNull
import dev.kordex.data.api.DataCollection
import dev.kordex.modules.func.phishing.extPhishing
import dev.kordex.modules.func.tags.tags
import dev.kordex.modules.pluralkit.extPluralKit
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import org.quiltmc.community.cozy.modules.ama.data.MemoryAmaData
import org.quiltmc.community.cozy.modules.ama.extAma
import org.quiltmc.community.cozy.modules.logs.extLogParser
import org.slf4j.LoggerFactory
import org.tywrapstudios.blossombridge.api.config.ConfigManager
import org.tywrapstudios.blossombridge.api.logging.LoggingHandler
import org.tywrapstudios.krafter.config.BotConfig
import org.tywrapstudios.krafter.database.DatabaseManager
import org.tywrapstudios.krafter.extensions.CustomTagsData
//import org.tywrapstudios.krafter.extensions.suggestion.SuggestionsExtension
import java.io.File
import java.nio.file.Path
import java.util.concurrent.CompletableFuture

private val TEST_SERVER_ID: Long? = envOrNull("TEST_SERVER")?.toLong()
private val TEST_TOKEN: String? = envOrNull("TEST_TOKEN")
private val INIT_LOGGER = LoggerFactory.getLogger("Krafter Standalone Initializer")
lateinit var CFG: ConfigManager<BotConfig>
lateinit var LOGGING: LoggingHandler<BotConfig>
lateinit var RUN_PATH: Path

private suspend fun setup(token: String, manager: ConfigManager<BotConfig>, runPath: Path) = ExtensibleBot(token) {
    CFG = manager
    CFG.loadConfig()
    val config = CFG.getConfig()
    LOGGING = LoggingHandler("Krafter", CFG)
    RUN_PATH = runPath

    DatabaseManager.setup(null)

    chatCommands {
        defaultPrefix = config.prefix
        enabled = true

        prefix { default ->
            // If TEST_SERVER_ID isn't null, we are in test mode and should not use the default prefix.
            if (TEST_SERVER_ID != null) {
                "?>"
            } else {
                default
            }
        }
    }

    applicationCommands {
        if (TEST_SERVER_ID != null) {
            defaultGuild = Snowflake(TEST_SERVER_ID)
        }
    }

    extensions {

        if (config.miscellaneous.plural_kit.enabled) extPluralKit()
        if (config.safety_and_abuse.moderation.block_phishing) extPhishing{
            for (domain in config.safety_and_abuse.moderation.banned_domains) badDomain(domain)
        }
        if (config.miscellaneous.tags.enabled) tags(CustomTagsData()) {}
//        if (config.miscellaneous.suggestion_forum) add { SuggestionsExtension() }
        if (config.miscellaneous.ama.enabled) extAma(MemoryAmaData())
        if (config.miscellaneous.crash_analysing.enabled) extLogParser{

        }

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
 * @param runPath The path where the bot is running from, defaults to the current working directory.
 */
suspend fun run(token: String, manager: ConfigManager<BotConfig>, runPath: Path = Path.of("").toAbsolutePath()) {
    INIT_LOGGER.info("rmthtoken: $token")
    val bot = setup(token, manager, runPath)
    INIT_LOGGER.info("$CFG")
    INIT_LOGGER.info("${CFG.getConfig().enabled}")
    if (CFG.getConfig().enabled) {
        bot.start()
    }
}

/**
 * Can be used to run the suspended [run] function in a coroutine scope.
 * This is useful for using it in Java code or in a non-suspending context.
 * You're always better off using the suspended version of [run], but this is here for convenience.
 * This function uses [GlobalScope] to run the bot in a blocking manner,
 * which is a delicate coroutine API. Use with care
 */
@DelicateCoroutinesApi
fun runAsync(
    token: String,
    manager: ConfigManager<BotConfig>,
    runPath: Path = Path.of("").toAbsolutePath()
): CompletableFuture<Unit> = GlobalScope.future {
    INIT_LOGGER.info("Running the bot asynchronously using [CompletableFuture]!")
    run(token, manager, runPath)
}

/**
 * Runs a test version of the bot. Provide the bot with a token in an .env file ([TEST_TOKEN]) and configure as needed.
 */
fun main() {
    val file = File("krafter.json5")
    val manager = ConfigManager(BotConfig::class.java, file)
    if (TEST_TOKEN != null) {
        INIT_LOGGER.info("Not null! Running test mode.")
        runAsync(TEST_TOKEN, manager)
    }
}