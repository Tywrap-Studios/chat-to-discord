package org.tywrapstudios.ctd.handlers;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import org.tywrapstudios.blossombridge.api.config.InvalidConfigVersionException;
import org.tywrapstudios.ctd.CTDCommon;
import org.tywrapstudios.ctd.compat.DiscordSafety;
import org.tywrapstudios.ctd.config.CTDConfig;
import org.tywrapstudios.ctd.discord.Discord;
import org.tywrapstudios.krafter.AppKt;
import org.tywrapstudios.krafter._UtilsKt;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class Handlers {
    public static void handleChatMessage(String messageStr, String authorUUID, String authorName) {
        CTDConfig config = CTDCommon.CONFIG_MANAGER.getConfig();
        List<String> webhookUrls = CTDCommon.WEBHOOKS;

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
        List<String> webhookUrls = CTDCommon.WEBHOOKS;

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
        List<String> webhookUrls = CTDCommon.WEBHOOKS;
        for (String url : webhookUrls) {
            Discord.sendCrashEmbed(cause, 7864320, url, report);
        }
    }

    public static int handleConfigReload(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        try {
            source.sendSuccess(() -> Component.literal("Reloading!").withStyle(ChatFormatting.GRAY), true);
            String token = CTDCommon.TOKEN;
            CTDCommon.WEBHOOKS.clear();
            CTDCommon.CONFIG_MANAGER.loadConfig();

            for (String potentialWebhook : CTDCommon.WEBHOOKS) {
                if (potentialWebhook.matches("https://discord\\.com/api/webhooks/[0-9]+/[A-Za-z0-9_\\-]+")) {
                    CTDCommon.WEBHOOKS.add(potentialWebhook);
                }
                // Note that only the last token in the list will be set as the final token
                if (potentialWebhook.matches("[A-Za-z0-9.]+")) CTDCommon.TOKEN = potentialWebhook;
            }

            if (!CTDCommon.TOKEN.equals(token)) {
                source.sendSuccess(() -> Component.literal("A new bot token was defined! Please check your server console for more info on what to do next.").withStyle(ChatFormatting.GREEN), true);
                CTDCommon.LOGGING.info("Since it's a little risky to stop a bot, and restart it with a different token while it's in the middle of running, your bot will only start using the new token after a full server restart.");
            }
        } catch (InvalidConfigVersionException e) {
            source.sendSuccess(() -> Component.literal("Could not reload CTD Config: Version out of sync.").withStyle(ChatFormatting.RED), true);
            return 0;
        }
        return 1;
    }

    public static void warnOperator(ServerPlayer player) {
        if (AppKt.CFG == null || !AppKt.getBotConfig().safety_and_abuse.operator_warning) {
            return;
        }
        MutableComponent warning0 = Component.literal("""
                          !!! WARNING !!!
                          Chat To Discord is currently running in BOT or DYNAMIC mode.""").withStyle(ChatFormatting.GOLD);
        MutableComponent warning1 = Component.literal("""
                          This means the following:""").withStyle(ChatFormatting.GRAY);
        MutableComponent warning2 = Component.literal(String.format("""
                          - The bot is collecting data at the %s level;
                          - The bot might be maintaining a connection from your Discord to the MC Chat."""
                , _UtilsKt.config().safety_and_abuse.data_collection)).withStyle(ChatFormatting.DARK_AQUA);
        MutableComponent warning3 = Component.literal("""
                          View your bot config file to review these settings, in there you can:""").withStyle(ChatFormatting.GRAY);
        MutableComponent warning4 = Component.literal("""
                          - Turn off this message;
                          - Change the data collection level;
                          - Change any other settings as you please.""").withStyle(ChatFormatting.DARK_AQUA);
        MutableComponent warning5 = Component.literal("""
                          Data collection is only present on your Discord server and is fully GDPR compliant. For more information, go to https://docs.kordex.dev/data-collection.html.
                          You're seeing this message because you have permission level 1 or higher.""").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC);

        player.sendSystemMessage(
                warning0.append(warning1).append(warning2).append(warning3).append(warning4).append(warning5)
        );
    }
}
