/*
 * The Krafter Log Parsing Extension was adapted from the Cozy Discord Bot.
 * The below is the license notice provided, but the latest version should always be available at the following
 * link: https://github.com/QuiltMC/cozy-discord/blob/root/LICENSE
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package org.tywrapstudios.krafter.extensions.logs

import dev.kord.core.behavior.channel.asChannelOfOrNull
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.Event
import dev.kordex.core.DISCORD_RED
import dev.kordex.core.checks.channelFor
import dev.kordex.core.checks.guildFor
import org.quiltmc.community.cozy.modules.logs.data.Log
import org.quiltmc.community.cozy.modules.logs.data.Order
import org.quiltmc.community.cozy.modules.logs.types.LogParser
import org.tywrapstudios.krafter.config
import org.tywrapstudios.krafter.getOrCreateChannel

class WrongLocationMessageSender : LogParser() {
    override val identifier: String = "wrong-location-message-sender"
    override val order = Order(Int.MAX_VALUE) // be the last parser to run (to destroy the log if necessary)

    @SuppressWarnings("ReturnCount", "MagicNumber")
    override suspend fun predicate(log: Log, event: Event): Boolean {
        val channel = channelFor(event)?.asChannelOfOrNull<TextChannel>() ?: return false
        val guild = guildFor(event)?.asGuildOrNull() ?: return false
        val allowedChannel = getOrCreateChannel(
            config().miscellaneous.crash_analysing.watch_channel,
            "crash-logs",
            "Send your crash logs here to get help.",
            mutableSetOf(),
            guild
        )

        if (channel.id == allowedChannel.id) return false

        channel.asChannelOfOrNull<TextChannel>()?.createEmbed {
            title = "Wrong Location"
            field {
                name = "Problem"
                value = "This log was sent in the wrong location. No parsing will be done."
            }
            field {
                name = "Fix"
                value = "Please use ${allowedChannel.mention} to parse logs."
            }
            color = DISCORD_RED
        }

        return false
    }

    override suspend fun process(log: Log) {
        log.abort("")
    }
}