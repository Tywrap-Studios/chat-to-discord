package org.tywrapstudios.krafter.database.transactors

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.channel.threads.ThreadChannelBehavior
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.replace
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.tywrapstudios.krafter.database.entities.OwnedThread
import org.tywrapstudios.krafter.database.tables.OwnedThreadTable
import org.tywrapstudios.krafter.database.tables.OwnedThreadTable.fromRow
import org.tywrapstudios.krafter.database.tables.OwnedThreadTable.guildId
import org.tywrapstudios.krafter.database.tables.OwnedThreadTable.maxThreadAfterIdle
import org.tywrapstudios.krafter.database.tables.OwnedThreadTable.maxThreadDuration
import org.tywrapstudios.krafter.database.tables.OwnedThreadTable.owner
import org.tywrapstudios.krafter.database.tables.SuggestionTable
import org.tywrapstudios.krafter.setup
import kotlin.time.toJavaDuration

@Suppress("TooManyFunctions")
object OwnedThreadTransactor {

    suspend fun get(id: Snowflake): OwnedThread? {
        var thread: OwnedThread? = null

        transaction {
            setup()

            OwnedThreadTable.selectAll()
                .where { OwnedThreadTable.id eq id }
                .forEach { thread = fromRow(it) }
        }

        return thread
    }

    suspend fun get(thread: ThreadChannelBehavior) =
        get(thread.id)

    suspend fun set(thread: OwnedThread) {
        transaction {
            setup()

            OwnedThreadTable.replace {
                it[id] = thread.id

                it[owner] = thread.owner
                it[guildId] = thread.guildId
                it[preventArchiving] = thread.preventArchiving

                it[maxThreadDuration] = thread.maxThreadDuration?.toJavaDuration()
                it[maxThreadAfterIdle] = thread.maxThreadAfterIdle?.toJavaDuration()
            }
        }
    }

    suspend fun getAllWithDuration(): List<OwnedThread> {
        val threads = mutableListOf<OwnedThread>()

        transaction {
            setup()

            OwnedThreadTable.selectAll()
                .where { (maxThreadDuration neq null) or (maxThreadAfterIdle neq null) }
                .forEach { threads.add(fromRow(it)) }
        }

        return threads
    }

    suspend fun getByOwner(id: Snowflake): OwnedThread? {
        var thread: OwnedThread? = null

        transaction {
            setup()

            OwnedThreadTable.selectAll()
                .where { owner eq id }
                .forEach { thread = fromRow(it) }
        }

        return thread
    }

    suspend fun getByOwner(user: UserBehavior) =
        getByOwner(user.id)

    suspend fun getByGuild(id: Snowflake): OwnedThread? {
        var thread: OwnedThread? = null

        transaction {
            setup()

            OwnedThreadTable.selectAll()
                .where { guildId eq id }
                .forEach { thread = fromRow(it) }
        }

        return thread
    }

    suspend fun getByGuild(guild: GuildBehavior) =
        getByGuild(guild.id)

    suspend fun getByOwnerAndGuild(owner: Snowflake, guild: Snowflake): OwnedThread? {
        var thread: OwnedThread? = null

        transaction {
            setup()

            SuggestionTable.selectAll()
                .where { (OwnedThreadTable.owner eq owner) and (guildId eq guild) }
                .forEach { thread = fromRow(it) }
        }

        return thread
    }

    suspend fun getByOwnerAndGuild(owner: UserBehavior, guild: Snowflake) =
        getByOwnerAndGuild(owner.id, guild)

    suspend fun getByOwnerAndGuild(owner: Snowflake, guild: GuildBehavior) =
        getByOwnerAndGuild(owner, guild.id)

    suspend fun getByOwnerAndGuild(owner: UserBehavior, guild: GuildBehavior) =
        getByOwnerAndGuild(owner.id, guild.id)

    suspend fun isOwner(thread: Snowflake, user: Snowflake) =
        get(thread)?.let { it.owner == user }

    suspend fun isOwner(thread: Snowflake, user: UserBehavior) =
        isOwner(thread, user.id)

    suspend fun isOwner(thread: ThreadChannelBehavior, user: Snowflake) =
        isOwner(thread.id, user)

    suspend fun isOwner(thread: ThreadChannelBehavior, user: UserBehavior) =
        isOwner(thread.id, user.id)
}