package org.tywrapstudios.ctd.compat.krafter;

public enum RunMode {
    /**
     * Indicates only a bot is running.
     */
    BOT,
    /**
     * Indicates only webhooks are being sent.
     */
    WEBHOOK,
    /**
     * Indicates both a bot is running and webhooks are being sent.
     */
    DYNAMIC,
    /**
     * Indicates both a bot is running and webhooks are being sent.
     * Webhooks are sent by the bot instead.
     * @implNote Currently not implemented or used
     */
    HARD_DYNAMIC
}
