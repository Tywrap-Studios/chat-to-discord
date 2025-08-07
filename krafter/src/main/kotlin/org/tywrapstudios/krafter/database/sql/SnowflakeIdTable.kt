package org.tywrapstudios.krafter.database.sql

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

open class SnowflakeIdTable : IdTable<ULong>() {
    override val id: Column<EntityID<ULong>> = ulong("snowflake").entityId().uniqueIndex()
    override val primaryKey: PrimaryKey? = PrimaryKey(id)
}