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

private val BAD_MODS = mutableMapOf(
    "fabric_hider" to "Fabric Hider",
    "baritone" to "Baritone",
)

private const val SITE_LINK =
    "https://wild-rubidium-ea3.notion.site/Welcome-to-CordCraft-1e59c7107e2180128f9efe24c853a251"

class RuleBreakingModProcessor : LogProcessor() {
    override val identifier: String = "rule-breaking-mod"
    override val order: Order = Order.Early

    override suspend fun process(log: Log) {
        val mods = log.getMods().filter { BAD_MODS.filter { it2 -> it2.key.contains(it.key) }.isNotEmpty() }

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
                    "For more information, please see [rule 1 on the site]($SITE_LINK). Please note that we will not " +
                            "provide you with support while you're using mods that break our rules."
                )
            }
        )
    }
}