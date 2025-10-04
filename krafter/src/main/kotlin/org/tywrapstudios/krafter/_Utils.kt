@file:Suppress("WildcardImport", "TooManyFunctions")

package org.tywrapstudios.krafter

import dev.kord.common.entity.Overwrite
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.createTextChannel
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.TextChannel
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import org.tywrapstudios.krafter.config.BotConfig
import org.tywrapstudios.krafter.database.DatabaseManager.krafterSqlLogger
import org.tywrapstudios.krafter.database.tables.*

const val CFG_CHANNEL_REASON = "Config prompted for an automatic new channel creation."

fun config(): BotConfig = CFG.getConfig()

fun saveConfig() = CFG.saveConfig()

fun Transaction.setup() {
    SchemaUtils.create(TagsTable, AmaConfigTable, MinecraftLinkTable)
    addLogger(krafterSqlLogger)
}

fun BotConfig.AdministratorList.getRoles(): Set<String> {
    val roles = HashSet<String>()
    roles.addAll(this.roles)
    roles.addAll(config().global_administrators.roles)
    return roles
}

fun BotConfig.AdministratorList.getUsers(): Set<String> {
    val users = HashSet<String>()
    users.addAll(this.users)
    users.addAll(config().global_administrators.users)
    return users
}

suspend fun getOrCreateChannel(
    providedName: String,
    defaultName: String,
    channelTopic: String = "A channel automatically created by Krafter.",
    permissionOverwrites: MutableSet<Overwrite>?,
    guild: Guild,
): TextChannel {
    var channel: TextChannel?

    val channels = guild
        .channels
        .let { channels ->
            val provided = channels.filter { it.name == providedName }
            val default = channels.filter { it.name == defaultName }
            if (provided.count() < 1) {
                return@let default
            } else {
                return@let provided
            }
        }
    LOGGING.debug("$providedName [$defaultName]: ${channels.count()} channels found.")

    channel = channels.lastOrNull() as? TextChannel

    LOGGING.debug("$providedName [$defaultName]: ${channel?.mention} found.")

    LOGGING.debug("$providedName [$defaultName]: ${providedName == "new"}")
    if (providedName == "new") {
        channel = guild.createTextChannel(defaultName) {
            reason = CFG_CHANNEL_REASON
            topic = channelTopic
            if (permissionOverwrites != null) this.permissionOverwrites = permissionOverwrites
        }
        LOGGING.debug("$providedName [$defaultName]: ${channel.mention} created. New channel.")
    }

    LOGGING.debug("$providedName [$defaultName]: ${providedName.isEmpty() && channel == null} || ${channel == null}")
    if ((providedName.isEmpty() && channel == null) || channel == null) {
        channel = guild.createTextChannel(providedName.ifEmpty { defaultName }) {
            reason = CFG_CHANNEL_REASON
            topic = channelTopic
        }
        LOGGING.debug("$providedName [$defaultName]: ${channel.mention} created. Defaulted name.")
    }

    LOGGING.debug("$providedName [$defaultName]: ${channel.mention} final.")
    return channel
}

// Snowflake skedaddles that are probably bad practice as hell, but I don't care

fun ULong.snowflake() = Snowflake(this)
fun String.snowflake() = Snowflake(this)

@JvmName("mapULongsToSnowflake")
fun Collection<ULong>.snowflake() = this.map { it.snowflake() }.toMutableList()

// Haha Platform declaration clash go brrr
@JvmName("mapStringsToSnowflake")
fun Collection<String>.snowflake() = this.map { it.snowflake() }.toMutableList()

fun Collection<Snowflake>.uLongs() = this.map { it.value }.toMutableList()