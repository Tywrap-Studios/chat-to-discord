package org.tywrapstudios.krafter.api.discord

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Member
import org.tywrapstudios.krafter.api.json.McPlayer
import java.util.*

class McAuthor(val member: Member, val player: McPlayer?) {
    fun getName(): String {
        return member.nickname ?: member.globalName ?: getUsername()
    }

    fun getUsername(): String {
        return member.username
    }

    fun getMcName(): String? {
        return player?.name
    }

    fun getId(): Snowflake {
        return member.id
    }

    fun getMcId(): UUID? {
        return if(player == null) null else UUID.fromString(player.id)
    }

    fun getMention(): String {
        return member.mention
    }

    fun getAvatarUrl(): String? {
        return member.avatar?.cdnUrl?.toUrl()
    }
}