package org.tywrapstudios.krafter.database.sql

import dev.kord.common.entity.Snowflake
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnTransformer
import org.jetbrains.exposed.sql.Table

open class SnowflakeIdTable : IdTable<Snowflake>() {
    override val id: Column<EntityID<Snowflake>> = snowflake("id").entityId()
    override val primaryKey: PrimaryKey? = PrimaryKey(id)
}

class SnowflakeTransformer : ColumnTransformer<ULong, Snowflake> {
    override fun unwrap(value: Snowflake): ULong {
        return value.value
    }

    override fun wrap(value: ULong): Snowflake {
        return Snowflake(value)
    }
}

fun Table.snowflake(name: String) = ulong(name).transform(SnowflakeTransformer())