package org.tywrapstudios.krafter.database

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.statements.expandArgs
import org.sqlite.SQLiteConfig
import org.sqlite.SQLiteDataSource
import org.tywrapstudios.krafter.LOGGING
import org.tywrapstudios.krafter.RUN_PATH
import javax.sql.DataSource
import kotlin.io.path.pathString

const val CONNECTION = "krafter.sqlite"

object DatabaseManager {

    private lateinit var database: Database
    val databaseType: String
        get() = database.dialect.name

    private var databaseContext = Dispatchers.IO + CoroutineName("Krafter Database")
    private val sqlLogger = object : SqlLogger {
        override fun log(context: StatementContext, transaction: Transaction) {
            LOGGING.info("SQL: ${context.expandArgs(transaction)}")
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    fun setup(dataSource: DataSource?) {
        if (dataSource == null) {
            database = Database.connect(getDefaultDatasource())
            databaseContext = newSingleThreadContext("Krafter Database")
        } else {
            database = Database.connect(dataSource)
        }
    }

    private fun getDefaultDatasource(): DataSource {
        val dbFilepath = RUN_PATH.resolve(CONNECTION).pathString
        return SQLiteDataSource(
            SQLiteConfig().apply {
                setJournalMode(SQLiteConfig.JournalMode.WAL)
            }
        ).apply {
            url = "jdbc:sqlite:$dbFilepath"
        }
    }
}