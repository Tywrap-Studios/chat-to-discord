package org.tywrapstudios.krafter.platform

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.tywrapstudios.krafter.platform.services.IMinecraftServerConnection
import java.util.*

internal val LOGGER: Logger = LoggerFactory.getLogger(IMinecraftServerConnection::class.java)

val MCSCCH = load(IMinecraftServerConnection::class.java)

fun <T> load(clazz: Class<T>): T {
    val loadedService = ServiceLoader.load<T?>(clazz)
        .findFirst().get()
    LOGGER.debug("Loaded {} for service {}", loadedService, clazz)
    return loadedService
}

