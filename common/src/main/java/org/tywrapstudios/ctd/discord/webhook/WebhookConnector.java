package org.tywrapstudios.ctd.discord.webhook;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tywrapstudios.ctd.CTDCommon;
import org.tywrapstudios.ctd.discord.messagetypes.Embed;
import org.tywrapstudios.ctd.discord.messagetypes.PlainMessage;
import org.tywrapstudios.ctd.discord.webhook.WebhookClient.Callback;

public class WebhookConnector {

    private String webhookUrl = "";
    private Embed[] embeds = null;

    Callback callback;
    private PlainMessage message;

    public WebhookConnector() {
    }

    /**
     * Sets the webhook URL for a channel.
     *
     * @param webhookUrl The webhook URL to be set for the channel.
     * @return An instance of WebhookManager with the updated webhook URL if the provided URL is valid.
     */
    public WebhookConnector setChannelUrl(String webhookUrl) {
        boolean isValidUrl = webhookUrl.matches("https://discord\\.com/api/webhooks/[0-9]+/[A-Za-z0-9_\\-]+");

        // If the provided URL is valid, update the channel's webhook URL.
        if (isValidUrl) {
            this.webhookUrl = webhookUrl;
        } else {
            CTDCommon.LOGGING.error(String.format("[Discord] Invalid webhook URL: %s", webhookUrl));
        }

        // Return the updated instance of WebhookManager.
        return this;
    }

    /**
     * Sets the message content and other details.
     *
     * @param message The message object containing username, avatar URL, and content.
     * @return An instance of WebhookManager with the updated message details.
     */
    public WebhookConnector setMessage(PlainMessage message) {
        this.message = message;
        return this;
    }

    /**
     * Sets a listener for webhook response callbacks.
     *
     * @param callback The callback to be invoked upon successful or failed webhook execution.
     * @return An instance of WebhookManager with the updated callback listener.
     */
    public WebhookConnector setListener(Callback callback) {
        this.callback = callback;
        return this;
    }

    public Embed[] getEmbeds() {
        return embeds;
    }

    /**
     * Sets the array of embeds for the webhook message.
     *
     * @param embeds The array of embeds to be attached to the message.
     * @return An instance of WebhookManager with the updated array of embeds.
     */
    public WebhookConnector setEmbeds(Embed[] embeds) {
        this.embeds = embeds;
        return this;
    }

    /**
     * Executes the sending of the message to the specified webhook.
     *
     * @return An instance of WebhookManager.
     */
    public WebhookConnector exec() {
        JSONObject obj = createJsonObject();

        WebhookClient wc = new WebhookClient(callback);
        wc.send(webhookUrl, obj);

        CTDCommon.LOGGING.debug("WebhookConnector executed.");

        return this;
    }

    /**
     * Creates a JSON object representing the message content and embeds.
     *
     * @return The JSON object containing the message details.
     */
    private JSONObject createJsonObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("username", message.getUsername());
            obj.put("avatar_url", message.getAvatarUrl());
            obj.put("content", message.getContent());

            JSONArray embedsArr = new JSONArray();

            if (embeds != null) {
                for (Embed item : this.embeds) {
                    embedsArr.put(item.get());
                }

                if (!embedsArr.isEmpty()) {
                    obj.put("embeds", embedsArr);
                }
            }
        } catch (JSONException e) {
            handleJsonException(e);
        }
        return obj;
    }

    /**
     * Handles JSON-related exceptions by logging them and invoking the onFailure callback.
     *
     * @param exception The JSONException that occurred.
     */
    private void handleJsonException(JSONException exception) {
        Logger logger = LoggerFactory.getLogger(WebhookConnector.class);
        logger.debug("[CTD] JSON Exception: ", exception);

        if (callback != null) {
            callback.onFailure(-1, exception.getMessage());
        }
    }
}
