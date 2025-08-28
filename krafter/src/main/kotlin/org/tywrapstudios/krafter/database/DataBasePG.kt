package org.tywrapstudios.krafter.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.sqlite.SQLiteConfig
import org.sqlite.SQLiteDataSource
import org.tywrapstudios.krafter.database.tables.AmaConfigTable
import org.tywrapstudios.krafter.database.tables.TagsTable
import org.tywrapstudios.krafter.snowflake
import kotlin.io.path.Path
import kotlin.io.path.pathString

fun main() {

    val dbFilepath = Path("").toAbsolutePath().resolve("krafter-pg.sqlite").pathString
    val dataSource = SQLiteDataSource(
        SQLiteConfig().apply {
            setJournalMode(SQLiteConfig.JournalMode.WAL)
        }
    ).apply {
        url = "jdbc:sqlite:$dbFilepath"
    }
    DatabaseManager.setup(dataSource)

    transaction {
        SchemaUtils.create(TagsTable, AmaConfigTable)
        addLogger(StdOutSqlLogger)

        val taskId = TagsTable.insert {
            it[key] = "learn"
            it[title] = "Learn Exposed"
            it[description] = "Go through the Get started with Exposed tutorial"
            it[category] = "Test"
        } get TagsTable.id

        val secondTaskId = TagsTable.insert {
            it[key] = "hobbit"
            it[title] = "Balling!"
            it[description] = "Read the first two chapters of The Hobbit"
            it[category] = "Yuhhh"
        } get TagsTable.id

        println("Created new tasks with ids $taskId and $secondTaskId.")

        TagsTable.select(TagsTable.id.count(), TagsTable.key).groupBy(TagsTable.key).forEach {
            println("${it[TagsTable.key]}: ${it[TagsTable.id.count()]} ")
        }

        AmaConfigTable.insert {
            it[id] = 13732384758686.toULong().snowflake()
            it[answerQueueChannel] = 4736372635524.toULong().snowflake()
            it[liveChatChannel] = 7543265435678.toULong().snowflake()
            it[buttonChannel] = 742467754324567.toULong().snowflake()
            it[approvalQueueChannel] = null
            it[flaggedQuestionChannel] = 35677654322456.toULong().snowflake()

            it[title] = "Yes"
            it[description] = "Blabla blablablallakhdhdhdfkdfjdkjfkjdhfjdhfdjkfhkdjfjdfdfjhdkfjdkfh" +
                    "dkfhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhsgsyeyeyeyeyeyegdgdgdhdhdhsggagaga"
            it[imageUrl] = null

            it[buttonMessage] = 337263748586868.toULong().snowflake()
            it[buttonId] = "ama-button-1"
            it[enabled] = true
        }
    }
}