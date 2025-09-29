/*
 * This Krafter database part was adapted from the Cozy Discord Bot.
 * The below is the license notice provided, but the latest version should always be available at the following
 * link: https://github.com/QuiltMC/cozy-discord/blob/root/LICENSE
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package org.tywrapstudios.krafter.database.tables

import org.jetbrains.exposed.sql.ResultRow
import org.tywrapstudios.krafter.database.entities.Suggestion
import org.tywrapstudios.krafter.database.sql.SnowflakeIdTable
import org.tywrapstudios.krafter.database.sql.snowflake
import org.tywrapstudios.krafter.extensions.suggestion.SuggestionStatus
import org.tywrapstudios.krafter.snowflake

object SuggestionTable : SnowflakeIdTable() {
    val guildId = snowflake("guildId")
    val channelId = snowflake("channelId")

    val comment = text("comment").nullable()
    val status = enumeration("status", SuggestionStatus::class).default(SuggestionStatus.RequiresName)
    val message = snowflake("message").nullable()
    val thread = snowflake("thread").nullable()
    val threadButtons = snowflake("threadButtons").nullable()

    val text = text("text")
    val problem = text("problem").nullable()
    val solution = text("solution").nullable()

    val owner = snowflake("owner")
    val ownerAvatar = text("ownerAvatar").nullable()
    val ownerName = text("ownerName")

    val positiveVoters = array<ULong>("positiveVoters").default(mutableListOf())
    val negativeVoters = array<ULong>("negativeVoters").default(mutableListOf())

    val isPluralkit = bool("isPluralkit").default(false)

    fun fromRow(row: ResultRow) = Suggestion(
        row[id].value,
        row[guildId],
        row[channelId],
        row[comment],
        row[status],
        row[message],
        row[thread],
        row[threadButtons],
        row[text],
        row[problem],
        row[solution],
        row[owner],
        row[ownerAvatar],
        row[ownerName],
        row[positiveVoters].snowflake(),
        row[negativeVoters].snowflake(),
        row[isPluralkit]
    )
}