package org.tywrapstudios.ctd.compat;

import org.tywrapstudios.ctd.CTDCommon;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordSafety {
    public static String modifyToNegateDangerousPings(String message) {
        if (!message.contains("@everyone")&&!message.contains("@here")&&!message.contains("<@&")) {
            return message;
        }
        message = message.replace("@everyone", "`@everyone`[ping negated]");
        message = message.replace("@here", "`@here`[ping negated]");
        message = modifyForRoleMentions(message);
        return message;
    }

    public static String modifyForRoleMentions(String message) {
        List<String> allowedRoles = CTDCommon.CONFIG_MANAGER.getConfig().discord_config.role_ids;
        Pattern pattern = Pattern.compile("<@&(\\d+)>");
        Matcher matcher = pattern.matcher(message);

        StringBuilder modifiedMessage = new StringBuilder();

        while (matcher.find()) {
            String roleId = matcher.group(1);

            if (allowedRoles.contains(roleId)) {
                matcher.appendReplacement(modifiedMessage, "<@&" + roleId + ">");
            } else {
                matcher.appendReplacement(modifiedMessage, "`" + roleId + "`[ping negated]");
            }
        }

        matcher.appendTail(modifiedMessage);
        return modifiedMessage.toString();
    }

    public static String modifyToNegateInviteLinks(String message) {
        message = message.replaceAll("(https?://(discord\\.gg|discord\\.com/invite)/[a-zA-Z0-9-]+)", "[Discord Invite]");
        return message;
    }
    
    public static String modifyToNegateMarkdown(String message) {
        message = message.replace("_", "\\_");
        return message;
    }
}
