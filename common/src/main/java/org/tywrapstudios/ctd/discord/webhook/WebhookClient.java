package org.tywrapstudios.ctd.discord.webhook;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.tywrapstudios.ctd.CTDCommon;
import org.tywrapstudios.ctd.platform.Services;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class WebhookClient {

    private final Callback callback;

    /**
     * Constructs a new instance of WebhookClient with a specified callback.
     *
     * @param callback The callback to handle webhook execution responses.
     */
    public WebhookClient(Callback callback) {
        this.callback = callback;
    }

    /**
     * Sends a message to a specified webhook URL.
     *
     * @param webhookUrl The URL of the webhook to send the message to.
     * @param message    The JSON message to be sent.
     */
    public void send(String webhookUrl, JSONObject message) {
        try {
            // Create a URI object from the provided webhook URL
            HttpsURLConnection connection = getHttpsURLConnection(webhookUrl);

            // Write the JSON message to the connection's output stream
            try (OutputStream stream = connection.getOutputStream()) {
                stream.write(message.toString().getBytes(StandardCharsets.UTF_8));
            }

            // Get the HTTP response code
            int responseCode = connection.getResponseCode();
            CTDCommon.LOGGING.debug(String.format("[Discord Client] Response Code: %s", responseCode));

            // Handle response based on response code
            if (responseCode == HttpsURLConnection.HTTP_OK || responseCode == HttpsURLConnection.HTTP_NO_CONTENT) {
                handleSuccessfulResponse(connection);
            } else {
                handleErrorResponse(connection, responseCode);
            }
        } catch (IOException | URISyntaxException e) {
            CTDCommon.LOGGING.error(String.format("[Discord Client] Error: %s", e.getMessage()));
            callback.onFailure(-1, e.getMessage());
        }
    }

    private static @NotNull HttpsURLConnection getHttpsURLConnection(String webhookUrl) throws URISyntaxException, IOException {
        URI uri = new URI(webhookUrl);

        // Open a connection to the webhook URL
        HttpsURLConnection connection = (HttpsURLConnection) uri.toURL().openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        Properties p = System.getProperties();
        String userAgent = String.format(
                "Minecraft* (%s; %s %s) - Chat To Discord %s",
                Services.PLATFORM.getPlatformName(),
                p.get("os.arch"),
                p.get("os.version"),
                CTDCommon.MOD_V
        );
        connection.setRequestProperty("User-Agent", userAgent);

        // Enable output for sending POST data
        connection.setDoOutput(true);
        return connection;
    }

    /**
     * Handles successful responses from the webhook server.
     *
     * @param connection The connection from which to read the response.
     * @throws IOException If an I/O error occurs while reading the response.
     */
    private void handleSuccessfulResponse(HttpsURLConnection connection) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            callback.onSuccess(response.toString());
        }
    }

    /**
     * Handles error responses from the webhook server.
     *
     * @param connection   The connection from which to read the error response.
     * @param responseCode The HTTP response code indicating the type of error.
     * @throws IOException If an I/O error occurs while reading the error response.
     */
    private void handleErrorResponse(HttpsURLConnection connection, int responseCode) throws IOException {
        try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(
                responseCode == HttpsURLConnection.HTTP_NOT_FOUND ? connection.getErrorStream() : connection.getInputStream()))) {

            String errorInputLine;
            StringBuilder errorResponse = new StringBuilder();

            while ((errorInputLine = errorReader.readLine()) != null) {
                errorResponse.append(errorInputLine);
            }

            callback.onFailure(-1, errorResponse.toString());
        }
    }

    /**
     * An interface for handling webhook execution responses.
     */
    public interface Callback {
        /**
         * Called when the webhook execution is successful.
         *
         * @param response The response received from the server.
         */
        void onSuccess(String response);

        /**
         * Called when the webhook execution encounters an error.
         *
         * @param statusCode    The HTTP status code indicating the type of error.
         * @param errorMessage The error message received from the server.
         */
        void onFailure(int statusCode, String errorMessage);
    }
}
