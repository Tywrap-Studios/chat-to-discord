package org.tywrapstudios.ctd.handlers;

import org.tywrapstudios.ctd.compat.DiscordSafety;
import org.tywrapstudios.ctd.compat.Xaero;

public class CompatHandlers {
    public static String handleCompat(String message) {
        message = Xaero.convertWayPointMessage(message);
        message = DiscordSafety.modifyToNegateDangerousPings(message);
        message = DiscordSafety.modifyToNegateInviteLinks(message);
        message = DiscordSafety.modifyToNegateMarkdown(message);

        return message;
    }
}
