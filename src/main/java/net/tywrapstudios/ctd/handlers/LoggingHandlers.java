package net.tywrapstudios.ctd.handlers;

import net.tywrapstudios.ctd.ChatToDiscord;
import net.tywrapstudios.ctd.config.Manager;
import net.tywrapstudios.ctd.config.config.Config;
import org.slf4j.Logger;

public class LoggingHandlers {
    static Logger logger = ChatToDiscord.LOGGER;
    static Logger debug = ChatToDiscord.DEBUG;

    public static void info(String message) {
        logger.info(message);
    }

    public static void warn(String message) {
        Config config = Manager.getConfig();
        if (!config.suppress_warns) {
            logger.warn(message);
        }
    }

    public static void error(String message) {
        Config config = Manager.getConfig();
        if (!config.suppress_warns) {
            logger.error(message);
        }
    }

    public static void debug(String message) {
        Config config = Manager.getConfig();
        if (config.debug_mode) {
            debug.info(message);
        }
    }

    public static void debugWarning(String message) {
        Config config = Manager.getConfig();
        if (config.debug_mode&&!config.suppress_warns) {
            debug.warn(message);
        }
    }
}
