package org.tywrapstudios.ctd.discord;

import gs.mclo.api.Log;
import gs.mclo.api.response.UploadLogResponse;
import org.tywrapstudios.ctd.CTDCommon;
import org.tywrapstudios.ctd.discord.messagetypes.Embed;
import org.tywrapstudios.ctd.discord.messagetypes.PlainMessage;
import org.tywrapstudios.ctd.discord.resources.Footer;
import org.tywrapstudios.ctd.discord.webhook.WebhookClient;
import org.tywrapstudios.ctd.discord.webhook.WebhookConnector;
import org.tywrapstudios.ctd.platform.CTDServices;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static org.tywrapstudios.ctd.CTDCommon.MCL;

public class Discord {
    public static void sendLiteralToDiscord(String message, boolean embedMode, String webhookUrl) {
        if (!embedMode) {
            PlainMessage literalMessage = new PlainMessage()
                    .setContent(message);
            new WebhookConnector()
                    .setChannelUrl(webhookUrl)
                    .setMessage(literalMessage)
                    .setListener(new WebhookClient.Callback() {
                        @Override
                        public void onSuccess(String response) {
                            logSuccess("CTD-Literal","CTD-Literal",message);
                        }

                        @Override
                        public void onFailure(int statusCode, String errorMessage) {
                            logFailure(message, statusCode, errorMessage, "CTD-Literal", "CTD-Literal");
                        }
                    })
                    .exec();
        } else {
            Footer footer = new Footer(message,"https://media.discordapp.net/attachments/1249069998148812930/1293350885837242388/minecraft_logo.png?ex=67070e60&is=6705bce0&hm=33b6d9a9ed182dc00bf080fbfa344a9f27781fde92d9cc9f4d4cfcc54ef40d47&=&format=webp&quality=lossless&width=889&height=889");
            Embed embed = new Embed()
                    .setColor(CTDCommon.CONFIG_MANAGER.getConfig().discord_config.embed_color_rgb_int)
                    .setFooter(footer);
            PlainMessage embedMessage = new PlainMessage()
                    .setContent("");
            new WebhookConnector()
                    .setChannelUrl(webhookUrl)
                    .setEmbeds(new Embed[]{embed})
                    .setMessage(embedMessage)
                    .setListener(new WebhookClient.Callback() {
                        @Override
                        public void onSuccess(String response) {
                            logSuccess("CTD-Literal","CTD-Literal",message);
                        }

                        @Override
                        public void onFailure(int statusCode, String errorMessage) {
                            logFailure(message, statusCode, errorMessage, "CTD-Literal", "CTD-Literal");
                        }
                    })
                    .exec();
        }
    }

    public static void sendChatMessageToDiscord(String chatMessage, String playerName, String webhookUrl, String UUID) {
        PlainMessage message = new PlainMessage()
                .setContent("**"+playerName+":** "+chatMessage);
        new WebhookConnector()
                .setChannelUrl(webhookUrl)
                .setMessage(message)
                .setListener(new WebhookClient.Callback() {
                    @Override
                    public void onSuccess(String response) {
                        logSuccess(playerName, UUID, chatMessage);
                    }
                    @Override
                    public void onFailure(int statusCode, String errorMessage) {
                        logFailure(chatMessage, statusCode, errorMessage, playerName, UUID);
                    }
                })
                .exec();
    }

    public static void sendEmbedToDiscord(String chatMessage, String playerName, String webhookUrl, String UUID, int embedColor) {
        Footer footer = new Footer(playerName+": "+chatMessage,"https://mc-heads.net/avatar/"+UUID+"/90");
        Embed embed = new Embed()
                .setColor(embedColor)
                .setFooter(footer);
        PlainMessage message = new PlainMessage()
                .setContent("");
        new WebhookConnector()
                .setChannelUrl(webhookUrl)
                .setEmbeds(new Embed[]{embed})
                .setMessage(message)
                .setListener(new WebhookClient.Callback() {
                    @Override
                    public void onSuccess(String response) {
                        logSuccess(playerName, UUID, chatMessage);
                    }

                    @Override
                    public void onFailure(int statusCode, String errorMessage) {
                        logFailure(chatMessage, statusCode, errorMessage, playerName, UUID);
                    }
                })
                .exec();
    }

    public static void sendCrashEmbed(String cause, int embedColor, String webhookUrl, Path log) {
        MCL.setMinecraftVersion(CTDServices.PLATFORM.getModVersion("minecraft"));
        UploadLogResponse response = null;
        boolean canSend = true;

        try {
            Log stack = log != null ? new Log(log) : new Log(Arrays.toString(new Exception("CTD Debug").getStackTrace()));
            response = MCL.uploadLog(stack).get().setClient(MCL);
        } catch (ExecutionException | InterruptedException | IOException e) {
            sendLiteralToDiscord("Minecraft experienced an exception, but CTD could not add an accompanying crash message. Please check your logs.", CTDCommon.CONFIG_MANAGER.getConfig().discord_config.embed_mode, webhookUrl);
            canSend = false;
            e.printStackTrace();
        }

        if (!canSend || response == null) return;

        String description = String.format("""
                **Minecraft crashed with the following given cause:**
                [`%s`]
                **Stacktrace:**
                [[`%s`](<%s>)]""", cause, response.getUrl().replace("https://mclo.gs/", ""), response.getUrl());
        Embed embed = new Embed()
                .setColor(embedColor)
                .setTitle("MINECRAFT EXPERIENCED AN EXCEPTION!")
                .setDescription(description);
        PlainMessage message = new PlainMessage()
                .setContent("");
        new WebhookConnector()
                .setChannelUrl(webhookUrl)
                .setEmbeds(new Embed[]{embed})
                .setMessage(message)
                .setListener(new WebhookClient.Callback() {
                    @Override
                    public void onSuccess(String response) {
                        logSuccess("CTD", "CTD-Internals", "Sent a Crash notice to the webhook(s).");
                    }

                    @Override
                    public void onFailure(int statusCode, String errorMessage) {
                        logFailure(cause, statusCode, errorMessage, "CTD", "CTD-Internals");
                    }
                })
                .exec();
    }

    public static void logSuccess(String playerName, String UUID, String chatMessage) {
        String log = String.format("[%s[%s]: %s]", playerName, UUID, chatMessage);
        CTDCommon.LOGGING.debug(log);
    }

    public static void logFailure(String chatMessage, int statusCode, String errorMessage, String playerName, String UUID) {
        CTDCommon.LOGGING.warn(String.format("Message \"%s\" by %s[%s] failed to send. ", chatMessage, playerName, UUID));
        CTDCommon.LOGGING.warn(String.format("Code: %s Error: %s", statusCode, errorMessage));
    }
}
