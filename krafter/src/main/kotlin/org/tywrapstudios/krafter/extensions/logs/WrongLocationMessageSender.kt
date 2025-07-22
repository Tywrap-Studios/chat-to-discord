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
import dev.kord.core.behavior.createTextChannel
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.Event
import dev.kordex.core.checks.channelFor
import dev.kordex.core.checks.guildFor
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.lastOrNull
import org.quiltmc.community.cozy.modules.logs.data.Log
import org.quiltmc.community.cozy.modules.logs.data.Order
import org.quiltmc.community.cozy.modules.logs.types.LogParser
import org.tywrapstudios.krafter.CFG_CHANNEL_REASON
import org.tywrapstudios.krafter.config

class WrongLocationMessageSender : LogParser() {
    override val identifier: String = "wrong-location-message-sender"
    override val order = Order(Int.MAX_VALUE) // be the last parser to run (to destroy the log if necessary)

    override suspend fun predicate(log: Log, event: Event): Boolean {
        val channel = channelFor(event)?.asChannelOfOrNull<TextChannel>() ?: return false
        val guild = guildFor(event) ?: return false
        var allowedChannel = guild
            .channels
            .filter { it.name == config().miscellaneous.crash_analysing.watch_channel }
            .lastOrNull()
            ?.asChannelOrNull() as? GuildMessageChannel

        if (config().miscellaneous.crash_analysing.watch_channel == "new") {
            allowedChannel = guild.createTextChannel("crash-logs") {
                reason = CFG_CHANNEL_REASON
                topic = "Send your crash logs here to get help."
            }
        }
        if (allowedChannel == null) return false
        if (channel.id == allowedChannel.id) return false

        channel.createMessage(
            "This log was sent in the wrong location. No parsing will be done.\n" +
                    "Please use <#${allowedChannel.name}> to parse logs."
        )
        return false
    }

    override suspend fun process(log: Log) = Unit
}