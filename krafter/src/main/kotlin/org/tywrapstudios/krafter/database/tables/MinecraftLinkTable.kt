package org.tywrapstudios.krafter.database.tables

import org.tywrapstudios.krafter.database.sql.SnowflakeIdTable

object MinecraftLinkTable : SnowflakeIdTable() {
    val uuid = uuid("uuid").uniqueIndex()
}