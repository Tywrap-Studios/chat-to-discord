package org.tywrapstudios.ctd.compat;

import org.tywrapstudios.ctd.CTDCommon;
import org.tywrapstudios.ctd.config.CTDConfig;
import org.tywrapstudios.ctd.discord.Discord;
import org.tywrapstudios.ctd.discord.messagetypes.Embed;
import org.tywrapstudios.ctd.discord.messagetypes.PlainMessage;
import org.tywrapstudios.ctd.discord.webhook.WebhookClient;
import org.tywrapstudios.ctd.discord.webhook.WebhookConnector;

import java.util.List;
import java.util.concurrent.TimeoutException;

public class Spark {
    public static void handleSparkWorldTimeOut(TimeoutException e) {
        CTDConfig config = CTDCommon.CONFIG_MANAGER.getConfig();
        List<String> webhookUrls = config.discord_config.discord_webhooks;
        for (String url : webhookUrls) {
            sendTimeOutEmbed(url);
        }
        e.printStackTrace();
    }

    private static void sendTimeOutEmbed(String webhookUrl) {
        String description = """
                **Timed out waiting for world statistics.**
                **Stacktrace:**
                -> View your console logs for more information.""";
        Embed embed = new Embed()
                .setColor(7864320)
                .setTitle("Spark Profiler:")
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
                        Discord.logSuccess("CTD", "CTD-Compat", "Sent a Timeout notice to the webhook(s).");
                    }

                    @Override
                    public void onFailure(int statusCode, String errorMessage) {
                        Discord.logFailure("Spark Timeout notice", statusCode, errorMessage, "CTD", "CTD-Compat");
                    }
                })
                .exec();
    }
}
