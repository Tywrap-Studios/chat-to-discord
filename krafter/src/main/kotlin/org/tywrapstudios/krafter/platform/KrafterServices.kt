package org.tywrapstudios.krafter.platform

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.tywrapstudios.krafter.api.rcon.RconMinecraftServerConnection
import org.tywrapstudios.krafter.platform.services.IMinecraftServerConnection
import java.util.*

internal val LOGGER: Logger = LoggerFactory.getLogger("Krafter Service Loader")

val MC_CONNECTION = loadOrElse(IMinecraftServerConnection::class.java, RconMinecraftServerConnection())

fun <T> load(clazz: Class<T>): T {
    val loadedService = ServiceLoader.load(clazz)
        .findFirst().get()
    debug(loadedService, clazz)
    return loadedService
}

fun <T> loadOrElse(clazz: Class<T>, other: T): T {
    val loadedService = ServiceLoader.load(clazz)
        .findFirst().orElse(other)
    debug(loadedService, clazz)
    return loadedService
}

private fun <T> debug(loadedService: T?, clazz: Class<T>) {
    LOGGER.debug("Loaded {} for service {}", loadedService, clazz)
}

