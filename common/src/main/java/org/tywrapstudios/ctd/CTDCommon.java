package org.tywrapstudios.ctd;

import java.io.File;
import java.util.List;
import java.util.Objects;
import gs.mclo.api.MclogsClient;

import org.tywrapstudios.blossombridge.api.config.ConfigManager;
import org.tywrapstudios.blossombridge.api.config.InvalidConfigVersionException;
import org.tywrapstudios.blossombridge.api.logging.LoggingHandler;
import org.tywrapstudios.ctd.command.CTDCommand;
import org.tywrapstudios.ctd.config.CTDConfig;
import org.tywrapstudios.ctd.platform.Services;

public class CTDCommon {
    public static final ConfigManager<CTDConfig> CONFIG_MANAGER =
            new ConfigManager<>(CTDConfig.class, new File(Services.PLATFORM.getConfigDirectory(), "ctd.json5"));
    public static final String MOD_V = Services.PLATFORM.getModVersion("ctd");
    public static MclogsClient MCL;
    public static LoggingHandler<CTDConfig> LOGGING = new LoggingHandler<>("CTD", CONFIG_MANAGER);

    public static void init() {
        CONFIG_MANAGER.loadConfig();
        if (CONFIG_MANAGER.getConfig().discord_config.discord_webhooks.isEmpty()) {
            CTDCommon.LOGGING.error("[Discord] No Webhooks Defined! Please Configure your webhooks in the Config file: ctd.json5");
        }

        MCL = new MclogsClient("Chat To Discord", MOD_V);

        LOGGING.info("Loading up.");

        Services.EVENTS.registerAll();

        LOGGING.debug("Debug mode enabled.");
        if (CONFIG_MANAGER.getConfig().discord_config.embed_mode) LOGGING.info("Embed mode enabled.");
        else LOGGING.info("Embed mode disabled.");
    }
}