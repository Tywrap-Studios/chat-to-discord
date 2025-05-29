package org.tywrapstudios.ctd.handlers;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.tywrapstudios.blossombridge.api.config.InvalidConfigVersionException;
import org.tywrapstudios.ctd.CTDCommon;
import org.tywrapstudios.ctd.compat.DiscordSafety;
import org.tywrapstudios.ctd.config.CTDConfig;
import org.tywrapstudios.ctd.discord.Discord;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class Handlers {
    public static void handleChatMessage(String messageStr, String authorUUID, String authorName) {
        CTDConfig config = CTDCommon.CONFIG_MANAGER.getConfig();
        List<String> webhookUrls = config.discord_config.discord_webhooks;

        messageStr = CompatHandlers.handleCompat(messageStr);
        if (!config.discord_config.embed_mode) {
            authorName = DiscordSafety.modifyToNegateMarkdown(authorName);
        }

        if (!webhookUrls.isEmpty()) {
            if (!Objects.equals(authorName, "Rcon")) {
                for (String url : webhookUrls) {
                    if (!config.discord_config.embed_mode) {
                        Discord.sendChatMessageToDiscord(messageStr, authorName, url, authorUUID);
                    } else {
                        Discord.sendEmbedToDiscord(messageStr, authorName, url, authorUUID, config.discord_config.embed_color_rgb_int);
                    }
                }
            } else {
                CTDCommon.LOGGING.debug("The sender was the Server or a Remote Console (RCON).");
            }
        } else {
            CTDCommon.LOGGING.error("[Discord] No webhooks configured! Please configure your webhooks in the Config file: ctd.json");
        }
    }

    public static void handleGameMessage(String message) {
        CTDConfig config = CTDCommon.CONFIG_MANAGER.getConfig();
        boolean embedMode = config.discord_config.embed_mode;
        List<String> webhookUrls = config.discord_config.discord_webhooks;

        message = CompatHandlers.handleCompat(message);
        if (!config.discord_config.only_send_messages) {
            if (!embedMode) {
                message = "**" + message + "**";
            }
            if (!webhookUrls.isEmpty()) {
                for (String url : webhookUrls) {
                    Discord.sendLiteralToDiscord(message, embedMode, url);
                }
            } else {
                CTDCommon.LOGGING.error("[Discord] No webhooks configured! Please configure your webhooks in the Config file: ctd.json");
            }
        }
    }

    public static void handleCrash(String cause, Path report) {
        CTDConfig config = CTDCommon.CONFIG_MANAGER.getConfig();
        List<String> webhookUrls = config.discord_config.discord_webhooks;
        for (String url : webhookUrls) {
            Discord.sendCrashEmbed(cause, 7864320, url, report);
        }
    }

    public static int handleConfigReload(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        try {
            source.sendSuccess(() -> Component.literal("Reloading!").withStyle(ChatFormatting.GRAY), true);
            CTDCommon.CONFIG_MANAGER.loadConfig();
        } catch (InvalidConfigVersionException e) {
            source.sendSuccess(() -> Component.literal("Could not reload CTD Config: Version out of sync.").withStyle(ChatFormatting.RED), true);
            return 0;
        }
        return 1;
    }
}
