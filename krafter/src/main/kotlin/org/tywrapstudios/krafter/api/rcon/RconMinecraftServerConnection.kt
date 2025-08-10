package org.tywrapstudios.krafter.api.rcon

import nl.vv32.rcon.Rcon
import org.tywrapstudios.krafter.api.discord.McMessage
import org.tywrapstudios.krafter.config
import org.tywrapstudios.krafter.platform.services.IMinecraftServerConnection

class RconMinecraftServerConnection : IMinecraftServerConnection {
    override fun broadcast(message: McMessage) {
        val cfg = config().minecraft

        Rcon.open(cfg.rcon_host, cfg.rcon_port.toInt()).use { rcon ->
            if (rcon.authenticate(cfg.rcon_password)) {
                val author = message.getAuthor().get()
                val userText = if(author.getMcName() == null) "@" + author.getName() else author.getMcName()
                val tellrawCommand =
                            "/tellraw @a [\"\"," +
                            "{\"text\":\"[$userText] \"," +
                            "\"color\":\"blue\"," +
                            "\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"${author.getMention()}\"}," +
                            "\"hoverEvent\":{\"action\":\"show_text\",\"contents\":" +
                            "[{\"text\":\"@${author.getUsername()}\"," +
                            "\"color\":\"dark_purple\"}]}}," +
                            "{\"text\":\"${message.getContent()}\"," +
                            "\"color\":\"white\"," +
                            "\"hoverEvent\":{\"action\":\"show_text\",\"contents\":" +
                            "[{\"text\":\"Sent from Discord\"," +
                            "\"color\":\"dark_purple\"}]}}]"
                // I'm actually so done with this

                println(rcon.sendCommand(tellrawCommand))
            } else {
                println("Failed to authenticate")
            }
        }
    }

    override fun broadcastPlain(message: String) {
        val cfg = config().minecraft

        Rcon.open(cfg.rcon_host, cfg.rcon_port.toInt()).use { rcon ->
            if (rcon.authenticate(cfg.rcon_password)) {
                val tellrawCommand = "/tellraw @a [\"\",{\"text\":\"$message\"}]"

                println(rcon.sendCommand(tellrawCommand))
            } else {
                println("Failed to authenticate")
            }
        }
    }
}