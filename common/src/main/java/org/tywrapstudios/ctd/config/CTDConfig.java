package org.tywrapstudios.ctd.config;

import blue.endless.jankson.Comment;
import org.tywrapstudios.blossombridge.api.config.BasicConfigClass;
import org.tywrapstudios.blossombridge.api.config.InvalidConfigVersionException;
import org.tywrapstudios.ctd.CTDCommon;

import java.util.ArrayList;
import java.util.List;

public class CTDConfig extends BasicConfigClass {
    public String format_version = "2.0";
    @Comment("All configurations for the Discord integration.")
    public DiscordConfig discord_config = new DiscordConfig();

    public static class DiscordConfig {
        @Comment("A list of webhooks in Strings that the mod will send messages to: \"https://discord.com/api/webhooks/...\"")
        public List<String> discord_webhooks = new ArrayList<>();
        @Comment("Whether to only send player messages to Discord, and not game related messages (e.g. join/leave messages, deaths, etc.).")
        public boolean only_send_messages = false;
        @Comment("Whether to send messages as an embed. If false, messages will be sent as plain text.")
        public boolean embed_mode = false;
        @Comment("""
                The setting below must be an RGB int, so not a `255, 255, 255` type of thing.
                Use this site if you want to use this feature:
                http://www.shodor.org/~efarrow/trunk/html/rgbint.html""")
        public int embed_color_rgb_int = 5489270;
        @Comment("A list of role ID's in Strings that users are allowed to ping from MC. e.g. \"123456789012345678\"")
        public List<String> role_ids = new ArrayList<>();
    }

    @Override
    public void validate() {
        if (!format_version.equals("2.0")) {
            throw new InvalidConfigVersionException("Your Config version is invalid: " + format_version);
        }
    }
}
