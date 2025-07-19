package org.tywrapstudios.ctd;

import gs.mclo.api.MclogsClient;
import org.tywrapstudios.blossombridge.api.config.ConfigManager;
import org.tywrapstudios.blossombridge.api.logging.LoggingHandler;
import org.tywrapstudios.ctd.compat.krafter.RunMode;
import org.tywrapstudios.krafter.config.BotConfig;
import org.tywrapstudios.ctd.config.CTDConfig;
import org.tywrapstudios.ctd.platform.CTDServices;
import org.tywrapstudios.krafter.AppKt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CTDCommon {
    public static final ConfigManager<CTDConfig> CONFIG_MANAGER =
            new ConfigManager<>(CTDConfig.class, new File(CTDServices.PLATFORM.getConfigDirectory(), "ctd.json5"));
    private static final ConfigManager<BotConfig> BOT_CFG =
            new ConfigManager<>(BotConfig.class, new File(CTDServices.PLATFORM.getConfigDirectory(), "ctd-krafter.json5"));
    public static final String MOD_V = CTDServices.PLATFORM.getModVersion("ctd");
    public static MclogsClient MCL;
    public static LoggingHandler<CTDConfig> LOGGING = new LoggingHandler<>("CTD", CONFIG_MANAGER);

    public static List<String> WEBHOOKS = new ArrayList<>();
    public static String TOKEN = "";
    public static RunMode MODE;

    public static void init() {
        WEBHOOKS.clear();

        CONFIG_MANAGER.loadConfig();
        if (CONFIG_MANAGER.getConfig().discord_config.discord_webhooks.isEmpty()) {
            CTDCommon.LOGGING.error("[Discord] No Webhooks Defined! Please Configure your webhooks in the Config file: ctd.json5");
        }

        for (String potentialWebhook : CONFIG_MANAGER.getConfig().discord_config.discord_webhooks) {
            if (potentialWebhook.matches("https://discord\\.com/api/webhooks/[0-9]+/[A-Za-z0-9_\\-]+")) {
                WEBHOOKS.add(potentialWebhook);
                MODE = MODE == RunMode.BOT || MODE == RunMode.DYNAMIC ? RunMode.DYNAMIC : RunMode.WEBHOOK;
            }
            // Note that only the last token in the list will be set as the final token
            if (potentialWebhook.matches("[A-Za-z0-9.]+")) {
                TOKEN = potentialWebhook;
                MODE = MODE == RunMode.WEBHOOK || MODE == RunMode.DYNAMIC ? RunMode.DYNAMIC : RunMode.BOT;
            }
        }

        if (!TOKEN.isEmpty()) AppKt.run(TOKEN, BOT_CFG);

        MCL = new MclogsClient("Chat To Discord", MOD_V);

        LOGGING.info("Loading up.");

        CTDServices.EVENTS.registerAll();

        LOGGING.debug("Debug mode enabled.");
        if (CONFIG_MANAGER.getConfig().discord_config.embed_mode) LOGGING.info("Embed mode enabled.");
        else LOGGING.info("Embed mode disabled.");
    }
}