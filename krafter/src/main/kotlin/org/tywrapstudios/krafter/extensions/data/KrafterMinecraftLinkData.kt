package org.tywrapstudios.krafter.extensions.data

import dev.kord.common.entity.Snowflake
import org.jetbrains.exposed.sql.deleteReturning
import org.jetbrains.exposed.sql.replace
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.tywrapstudios.krafter.database.tables.MinecraftLinkTable
import org.tywrapstudios.krafter.setup
import java.util.*
import kotlin.random.Random
import kotlin.random.nextUInt

class KrafterMinecraftLinkData {
    /**
     * Starts a link status for the given member, if one does not already exist.
     * If a link status already exists, it will be replaced with the new one.
     *
     * @param member The member to set the link status for.
     * @param link The link status to set for the member. It is suggested to use a new [LinkStatus] instance, with
     * just the [LinkStatus.uuid] set, as the other fields will be generated automatically and used for
     * link verification.
     */
    suspend fun setLinkStatus(member: Snowflake, link: LinkStatus): LinkStatus {
        transaction {
            setup()

            MinecraftLinkTable.replace {
                it[id] = member
                it[uuid] = link.uuid
                it[code] = link.code
                it[verified] = link.verified
            }
        }
        return link
    }

    /**
     * Gets the link status for the given member.
     *
     * @param member The member to get the link status for.
     * @return The [LinkStatus] for the member, or null if no link status exists. Note that the [LinkStatus.verified]
     * value might be false, even if a link status exists, meaning the member has not yet verified their link.
     */
    suspend fun getLinkStatus(member: Snowflake): LinkStatus? {
        var status: LinkStatus? = null
        transaction {
            setup()

            MinecraftLinkTable.selectAll().where { MinecraftLinkTable.id eq member }
                .forEach {
                    status = LinkStatus(
                        it[MinecraftLinkTable.uuid],
                        it[MinecraftLinkTable.code],
                        it[MinecraftLinkTable.verified]
                    )
                }
        }
        return status
    }

    /**
     * Verifies the link status for the given member, using the provided verification code.
     *
     * If the verification code matches the one stored in the database, the link status will be updated to
     * set the [LinkStatus.verified] field to true and the [LinkStatus.code] to 0u.
     *
     * - If the verification code does not match, the method will return -1.
     * - If the verification is successful, it will return 1.
     * - If no link status exists for the member, it will return 0.
     *
     * @param member The member to verify the link status for.
     * @param verificationCode The verification code to check against the stored code.
     */
    suspend fun verify(member: Snowflake, verificationCode: UInt): Int {
        var success = 0

        transaction {
            setup()

            MinecraftLinkTable.selectAll().where { MinecraftLinkTable.id eq member }.forEach {
                if (it[MinecraftLinkTable.code] != verificationCode) {
                    success = -1
                    return@transaction
                }
            }

            MinecraftLinkTable.update({ MinecraftLinkTable.id eq member }) {
                it[code] = 0u
                it[verified] = true
                success = 1
            }
        }

        return success
    }

    /**
     * Verifies the link status for the given member, using the provided verification code.
     *
     * If the verification code matches the one stored in the database, the link status will be updated to
     * set the [LinkStatus.verified] field to true and the [LinkStatus.code] to 0u.
     *
     * - If the verification code does not match, the method will return -1.
     * - If the verification is successful, it will return 1.
     * - If no link status exists for the member, it will return 0.
     *
     * @param uuid The UUID to verify the link status for.
     * @param verificationCode The verification code to check against the stored code.
     */
    suspend fun verify(uuid: UUID, verificationCode: UInt): Int {
        var success = 0

        transaction {
            setup()

            MinecraftLinkTable.selectAll().where { MinecraftLinkTable.uuid eq uuid }.forEach {
                if (it[MinecraftLinkTable.code] != verificationCode) {
                    success = -1
                    return@transaction
                }
            }

            MinecraftLinkTable.update({ MinecraftLinkTable.uuid eq uuid }) {
                it[code] = 0u
                it[verified] = true
                success = 1
            }
        }

        return success
    }

    /**
     * Unlinks the given member from their Minecraft account.
     * This will remove the link status for the member and return the UUID that was linked.
     *
     * If no link status exists for the member, it will return null.
     *
     * Will be deleted regardless of whether the link was verified or not.
     *
     * @param member The member to unlink.
     */
    suspend fun unlink(member: Snowflake): UUID? {
        var uuid: UUID? = null

        transaction {
            setup()

            MinecraftLinkTable.deleteReturning { MinecraftLinkTable.id eq member }.forEach {
                uuid = it[MinecraftLinkTable.uuid]
            }
        }

        return uuid
    }

    /**
     * Represents a link status for a Minecraft account.
     *
     * @property uuid The UUID of the linked Minecraft account.
     * @property code The verification code for the link, which is randomly generated and unique.
     * @property verified Whether the link has been verified by the user.
     */
    data class LinkStatus(
        val uuid: UUID,
        val code: UInt = Random.nextUInt(10000u..99999u),
        val verified: Boolean = false
    )
}