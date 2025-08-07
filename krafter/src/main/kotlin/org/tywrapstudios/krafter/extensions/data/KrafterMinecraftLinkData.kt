package org.tywrapstudios.krafter.extensions.data

import dev.kord.common.entity.Snowflake
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.tywrapstudios.krafter.database.tables.MinecraftLinkTable
import org.tywrapstudios.krafter.setup
import java.util.UUID

class KrafterMinecraftLinkData {
    suspend fun getMinecraftUuid(member: Snowflake): UUID? {
        var uuid: UUID? = null
        transaction {
            setup()

            MinecraftLinkTable.select(MinecraftLinkTable.id).where { MinecraftLinkTable.id eq member.value }
                .forEach { uuid = it[MinecraftLinkTable.uuid] }
        }
        return uuid
    }

    suspend fun setMinecraftUuid(member: Snowflake, uuid: UUID) {
        transaction {
            setup()

            MinecraftLinkTable.update({ MinecraftLinkTable.id eq member.value }) {
                it[MinecraftLinkTable.uuid] = uuid
            }
        }
    }
}