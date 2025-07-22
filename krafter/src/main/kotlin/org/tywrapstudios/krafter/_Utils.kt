package org.tywrapstudios.krafter

import org.tywrapstudios.krafter.config.BotConfig

const val CFG_CHANNEL_REASON = "Config prompted for an automatic new channel creation."

fun config(): BotConfig = CFG.getConfig()

fun saveConfig() = CFG.saveConfig()