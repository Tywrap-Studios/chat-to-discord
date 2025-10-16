package org.tywrapstudios.krafter

import dev.kord.common.entity.Snowflake
import dev.kordex.core.ExtensibleBot
import dev.kordex.core.utils.envOrNull
import dev.kordex.data.api.DataCollection
import dev.kordex.modules.func.phishing.extPhishing
import dev.kordex.modules.func.tags.tags
import dev.kordex.modules.func.welcome.welcomeChannel
import dev.kordex.modules.pluralkit.extPluralKit
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import org.quiltmc.community.cozy.modules.ama.extAma
import org.quiltmc.community.cozy.modules.logs.extLogParser
import org.quiltmc.community.cozy.modules.logs.processors.PiracyProcessor
import org.quiltmc.community.cozy.modules.logs.processors.ProblematicLauncherProcessor
import org.slf4j.LoggerFactory
import org.tywrapstudios.blossombridge.api.config.ConfigManager
import org.tywrapstudios.blossombridge.api.logging.LoggingHandler
import org.tywrapstudios.krafter.checks.isBotModuleAdmin
import org.tywrapstudios.krafter.checks.isGlobalBotAdmin
import org.tywrapstudios.krafter.config.BotConfig
import org.tywrapstudios.krafter.database.DatabaseManager
import org.tywrapstudios.krafter.extensions.data.KrafterAmaData
import org.tywrapstudios.krafter.extensions.data.KrafterTagsData
import org.tywrapstudios.krafter.extensions.data.KrafterWelcomeChannelData
import org.tywrapstudios.krafter.extensions.logs.RuleBreakingModProcessor
import org.tywrapstudios.krafter.extensions.logs.WrongLocationMessageSender
import org.tywrapstudios.krafter.extensions.minecraft.MinecraftExtension
import org.tywrapstudios.krafter.extensions.sab.SafetyAndAbuseExtension
import org.tywrapstudios.krafter.extensions.sab.getOverwrites
import org.tywrapstudios.krafter.extensions.suggestion.SuggestionsExtension
import java.io.File
import java.nio.file.Path
import java.util.concurrent.CompletableFuture

private val TEST_SERVER_ID: Snowflake? = envOrNull("TEST_SERVER")?.toULong()?.snowflake()
private val TEST_TOKEN: String? = envOrNull("TEST_TOKEN")
private val INIT_LOGGER = LoggerFactory.getLogger("Krafter Standalone Initializer")
var TOKEN: String? = null
lateinit var CFG: ConfigManager<BotConfig>
lateinit var LOGGING: LoggingHandler<BotConfig>
lateinit var RUN_PATH: Path

private suspend fun setup(token: String, manager: ConfigManager<BotConfig>, runPath: Path): ExtensibleBot {
    TOKEN = token
    CFG = manager
    LOGGING = LoggingHandler("Krafter", CFG)
    RUN_PATH = runPath

    CFG.loadConfig()
    LOGGING.debug("Before db setup")
    DatabaseManager.setup(null)
    LOGGING.debug("After db setup")

    LOGGING.debug("Current configuration:")
    LOGGING.debug(CFG.getConfigJsonAsString(comments = false, newlines = true))

    val config = config()

    return ExtensibleBot(token) {

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
                defaultGuild = TEST_SERVER_ID
            }
        }

        extensions {

            if (config.miscellaneous.plural_kit.enabled) extPluralKit()

            add(::SafetyAndAbuseExtension)

            if(config.minecraft.enabled) {
                add(::MinecraftExtension)
            }
            if (config.safety_and_abuse.moderation.block_phishing) extPhishing {
                for (domain in config.safety_and_abuse.moderation.banned_domains) badDomain(domain)
                logChannelName =
                    if (
                        config().safety_and_abuse.dump_channel == "new" ||
                        config().safety_and_abuse.dump_channel.isEmpty()
                    ) {
                        "krafter-sab"
                    } else {
                        config.safety_and_abuse.dump_channel
                    }
            }
            if (config.miscellaneous.tags.enabled) tags(KrafterTagsData()) {
                staffCommandCheck { isBotModuleAdmin(config.miscellaneous.tags.administrators) }
                loggingChannelName =
                    if (
                        config().safety_and_abuse.dump_channel == "new" ||
                        config().safety_and_abuse.dump_channel.isEmpty()
                    ) {
                        "krafter-sab"
                    } else {
                        config.safety_and_abuse.dump_channel
                    }
            }
            if (config.miscellaneous.ama.enabled) extAma(KrafterAmaData())
            if (config.miscellaneous.crash_analysing.enabled) extLogParser {
                processor(PiracyProcessor())
                processor(ProblematicLauncherProcessor())
                processor(RuleBreakingModProcessor())

                parser(WrongLocationMessageSender())
                staffCommandCheck { isGlobalBotAdmin() }
            }
            if (config.miscellaneous.embed_channels.enabled) welcomeChannel(KrafterWelcomeChannelData()) {
                staffCommandCheck { isBotModuleAdmin(config.miscellaneous.embed_channels.administrators) }
                getLogChannel { channel, guild ->
                    val cfg = config().safety_and_abuse

                    return@getLogChannel getOrCreateChannel(
                        cfg.dump_channel,
                        "krafter-sab",
                        "Safety and Abuse logging and dump channel for the Krafter software",
                        getOverwrites(guild),
                        guild
                    )
                }
            }

            if (config.miscellaneous.suggestion_forum.enabled) {
                add(::SuggestionsExtension)
            }
        }

        dataCollectionMode = DataCollection.fromDB(config.safety_and_abuse.data_collection)

        presence {
            playing("on ${config.status.server_name}")
        }
    }
}

suspend fun setup(): ExtensibleBot {
    return setup(
        token = TOKEN!!,
        manager = CFG,
        runPath = RUN_PATH
    )
}

/**
 * Runs the bot, no setup needed beforehand.
 * @param token The Discord bot token from the developer portal.
 * @param manager A [ConfigManager] that handles the [BotConfig] for the bot.
 * @param runPath The path where the bot is running from, defaults to the current working directory.
 */
suspend fun run(token: String, manager: ConfigManager<BotConfig>, runPath: Path = Path.of("").toAbsolutePath()) {
//    INIT_LOGGER.info("rmthtoken: $token")
    val bot = setup(token, manager, runPath)
    INIT_LOGGER.info("$CFG")
    INIT_LOGGER.info("${config().enabled}")
    if (config().enabled) {
        bot.start()
    } else {
        INIT_LOGGER.warn("The bot is disabled in the configuration! Please enable it to run.")
    }
}

/**
 * Can be used to run the suspended [run] function in a coroutine scope.
 * This is useful for using it in Java code or in a non-suspending context.
 * You're always better off using the suspended version of [run], but this is here for convenience.
 * This function uses [GlobalScope] to run the bot in a non-blocking manner,
 * which is a delicate coroutine API. Use with care.
 */
@DelicateCoroutinesApi
fun runAsync(
    token: String,
    manager: ConfigManager<BotConfig>,
    runPath: Path = Path.of("").toAbsolutePath()
): CompletableFuture<Unit> = GlobalScope.future {
    INIT_LOGGER.info("Running the bot asynchronously using [CompletableFuture]!")
    run(token, manager, runPath)
    INIT_LOGGER.info("[CompletableFuture] Post run clarity")
}

/**
 * Runs a test version of the bot. Provide the bot with a token in an .env file ([TEST_TOKEN]) and configure as needed.
 */
//@OptIn(DelicateCoroutinesApi::class)
suspend fun main() {
    val file = File("krafter.json5")
    val manager = ConfigManager(BotConfig::class.java, file)
    if (TEST_TOKEN != null) {
        INIT_LOGGER.info("Not null! Running test mode.")
        run(TEST_TOKEN, manager)
    }
}