/*
 * This Krafter database part was adapted from the Cozy Discord Bot.
 * The below is the license notice provided, but the latest version should always be available at the following
 * link: https://github.com/QuiltMC/cozy-discord/blob/root/LICENSE
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package org.tywrapstudios.krafter.database.entities

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable
import org.tywrapstudios.krafter.extensions.suggestion.SuggestionStatus

@Serializable
data class Suggestion(
    val id: Snowflake,
    val guildId: Snowflake,
    val channelId: Snowflake,

    var comment: String? = null,
    var status: SuggestionStatus = SuggestionStatus.RequiresName,
    var message: Snowflake? = null,
    var thread: Snowflake? = null,
    var threadButtons: Snowflake? = null,

    var text: String,
    var problem: String? = null,
    var solution: String? = null,

    val owner: Snowflake,
    val ownerAvatar: String?,
    val ownerName: String,

    val positiveVoters: MutableList<Snowflake> = mutableListOf(),
    val negativeVoters: MutableList<Snowflake> = mutableListOf(),

    val isPluralkit: Boolean = false,
) {
    val positiveVotes get() = positiveVoters.size
    val negativeVotes get() = negativeVoters.size
    val voteDifference get() = positiveVotes - negativeVotes
}
