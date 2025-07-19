package org.tywrapstudios.ctd.config;

import blue.endless.jankson.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tywrapstudios.blossombridge.api.config.BasicConfigClass;
import org.tywrapstudios.blossombridge.api.config.InvalidConfigVersionException;

import java.util.ArrayList;
import java.util.List;

public class CTDConfig extends BasicConfigClass {
    public String format_version = "2.0";

    @Comment("All configurations for the Discord integration.")
    public DiscordConfig discord_config = new DiscordConfig();
    public static class DiscordConfig {
        @Comment("""
                A list of webhooks in Strings that the mod will send messages to: "https://discord.com/api/webhooks/..."
                
                Alternatively, you can input Bot Tokens to run a bot. You can do this alongside webhooks. All the webhooks
                will be loaded, but only the last input token will be run (as only one bot can be run at a time.)
                
                If it's your first time running a bot, a new config file, bot.json5, will be generated with extra settings
                for you to choose from specifically tempered towards hosting a bot.
                
                !!! WARNING !!!
                Remember: this config file can be viewed by anyone that has access to it internally, and operators with
                permission level 3 and above! Make sure you trust these people, and never make your bot token public or share
                it with anyone untrustworthy, as it can be used for malicious purposes. We suggest you re-generate your token
                every so often for added security, and update it accordingly. This is not a requirement.""")
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
        Logger logger = LoggerFactory.getLogger("CTDConfig.validate()");
        if (!format_version.equals("2.0")) {
            logger.warn("Your Config version is invalid: {}", format_version);
        }
    }
}
