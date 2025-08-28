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

import org.quiltmc.community.cozy.modules.logs.data.Log
import org.quiltmc.community.cozy.modules.logs.data.Order
import org.quiltmc.community.cozy.modules.logs.types.LogProcessor
import org.tywrapstudios.krafter.config
import java.util.regex.Pattern

private val BAD_MODS = mutableMapOf(
    "fabric_hider" to "Fabric Hider",
    "baritone" to "Baritone",
)

private val SITE_LINK = config().safety_and_abuse.moderation.rules_link

class RuleBreakingModProcessor : LogProcessor() {
    override val identifier: String = "rule-breaking-mod"
    override val order: Order = Order.Early

    override suspend fun process(log: Log) {
        val mods = log.getMods().filter {
            BAD_MODS.filter { it2 ->
                Pattern.compile(it2.key).matcher(it.key).matches()
            }.isNotEmpty()
        }

        if (mods.isEmpty()) {
            return
        }

        log.abort(
            buildString {
                append("You appear to have the following rule-breaking mods installed: ")

                appendLine(
                    mods
                        .map { BAD_MODS[it.key] }
                        .toSet()
                        .sortedBy { it }
                        .joinToString { "**$it**" }
                )

                appendLine()

                append(
                    "For more information, please read [our rules]($SITE_LINK). Please note that we will not " +
                            "provide you with support while you're using mods that break these rules."
                )
            }
        )
    }
}