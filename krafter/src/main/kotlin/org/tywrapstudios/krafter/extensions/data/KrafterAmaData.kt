package org.tywrapstudios.krafter.extensions.data

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Snowflake
import dev.kordex.core.checks.hasPermission
import dev.kordex.core.checks.types.CheckContextWithCache
import org.jetbrains.exposed.sql.replace
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.quiltmc.community.cozy.modules.ama.data.AmaConfig
import org.quiltmc.community.cozy.modules.ama.data.AmaData
import org.tywrapstudios.krafter.checks.isBotModuleAdmin
import org.tywrapstudios.krafter.config
import org.tywrapstudios.krafter.database.tables.AmaConfigTable
import org.tywrapstudios.krafter.database.tables.AmaConfigTable.fromRow
import org.tywrapstudios.krafter.setup

class KrafterAmaData : AmaData {
    override suspend fun getConfig(guildId: Snowflake): AmaConfig? {
        var cfg: AmaConfig? = null
        transaction {
            setup()

            AmaConfigTable.selectAll().where { AmaConfigTable.id eq guildId }.forEach {
                cfg = fromRow(it)
            }
        }
        return cfg
    }

    override suspend fun isButtonEnabled(guildId: Snowflake): Boolean? {
        return getConfig(guildId)?.enabled
    }

    override suspend fun modifyButton(guildId: Snowflake, enabled: Boolean) {
        transaction {
            setup()

            AmaConfigTable.update({ AmaConfigTable.id eq guildId }) {
                it[AmaConfigTable.enabled] = enabled
            }
        }
    }

    override suspend fun setConfig(config: AmaConfig) {
        transaction {
            setup()

            AmaConfigTable.replace {
                it[id] = config.guildId
                it[answerQueueChannel] = config.answerQueueChannel
                it[liveChatChannel] = config.liveChatChannel
                it[buttonChannel] = config.buttonChannel
                it[approvalQueueChannel] = config.approvalQueueChannel
                it[flaggedQuestionChannel] = config.flaggedQuestionChannel

                it[title] = config.embedConfig.title
                it[description] = config.embedConfig.description
                it[imageUrl] = config.embedConfig.imageUrl

                it[buttonMessage] = config.buttonMessage
                it[buttonId] = config.buttonId
                it[enabled] = config.enabled
            }
        }
    }

    override suspend fun usePluralKitFronter(user: Snowflake): Boolean {
        return config().miscellaneous.plural_kit.enabled
    }

    override suspend fun CheckContextWithCache<*>.managementChecks() {
        hasPermission(Permission.ManageGuild)
        isBotModuleAdmin(config().miscellaneous.ama.administrators)
    }
}