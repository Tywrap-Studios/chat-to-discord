package org.tywrapstudios.krafter.api.discord

import dev.kord.core.entity.Message
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import org.tywrapstudios.krafter.api.json.McPlayer
import org.tywrapstudios.krafter.api.json.getMcPlayer
import java.util.UUID
import java.util.concurrent.CompletableFuture

class McMessage(val message: Message) {

    fun getContent(): String {
        return message.content
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getAuthor(): CompletableFuture<McAuthor> = GlobalScope.future {
        return@future McAuthor(message.getAuthorAsMember(), getMcPlayer(UUID.randomUUID()))
    }
}