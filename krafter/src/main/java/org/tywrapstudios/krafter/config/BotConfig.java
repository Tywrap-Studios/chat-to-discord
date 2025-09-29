package org.tywrapstudios.krafter.config;

import blue.endless.jankson.Comment;
import org.tywrapstudios.blossombridge.api.config.BasicConfigClass;

import java.util.*;
import java.util.function.Function;

public class BotConfig extends BasicConfigClass {
    public static class AdministratorList {
        public final Set<String> users = new HashSet<>();
        public final Set<String> roles = new HashSet<>();
    }
    @Comment("Whether the bot should be run altogether.")
    public boolean enabled = true;
    @Comment("""
            Role and user ids that are considered global administrators for the bot.
            They most notably have full permission over most of the SAB and Misc functionality.
            Don't worry though, you can set separate admins for separate functions in their respective configs.""")
    public AdministratorList global_administrators = new AdministratorList();
    @Comment("Discord to MC Chat functionality specifics.")
    public Minecraft minecraft = new Minecraft();
    public static class Minecraft {
        @Comment("Whether Discord messages should be sent to the MC Chat altogether.")
        public boolean enabled = true;
        @Comment("""
                The name of the channel which the bot will watch for messages to send. e.g. "mc-chat"
                Set to "new" to have one made automatically.""")
        public String watch_channel = "";
        @Comment("""
                An address (host plus port) to an RCON connection, which allows the bot to send commands to the server without
                a direct server connection.
                
                If you don't know what this is, or what it does, don't touch this. Krafter will always first try to
                maintain a direct connection before ultimately falling back to RCON.""")
        public String rcon_host = "";
        public String rcon_port = "";
        @Comment("The password for the RCON connection, if you have one set up.")
        public String rcon_password = "";
    }
    @Comment("The prefix for chat commands.")
    public String prefix = ">>";

    @Comment("Configurations that allow users to see the status of your MC server.")
    public Status status = new Status();
    public static class Status {
        @Comment("Whether the bot's status should reflect the server status.")
        public boolean reflect = true;
        @Comment("The name of your server to use in the status message.")
        public String server_name = "The Epic Server";
        @Comment("Override the status text completely. Leave empty to let the mod handle it. Set to \"motd\" to have your MOTD used instead.")
        public String status_override = "";
        @Comment("Have the bot maintain an online players channel, which displays the amount of people online.")
        public boolean online_players_channel = false;
    }

    @Comment("Configurations related to safety and abuse.")
    public SafetyAndAbuse safety_and_abuse = new SafetyAndAbuse();
    public static class SafetyAndAbuse {
        @Comment("""
                The name of the channel in which the bot will post SAB related messages. (And sometimes other ones) e.g. "moderation"
                Set to "new" to have one made automatically.""")
        public String dump_channel = "";
        @Comment("Role and user ids that are considered administrators for SAB functionality.")
        public AdministratorList administrators = new AdministratorList();
        @Comment("""
                YOOHOO!
                The bot software collects data!
                This value must be one of "none", "minimal", "standard" or "extra" to be valid,
                otherwise we'll set it back to "standard" by default. Can only be applied after a full server restart.
                
                For more information on what data the bot collects, how to get at it, and how it's stored,
                please see here: https://docs.kordex.dev/data-collection.html""")
        public String data_collection = "standard";
        @Comment("Whether operators should receive the General Use warning every time they join. Only used if run on a Minecraft server.")
        public boolean operator_warning = true;
        public Moderation moderation = new Moderation();
        public static class Moderation {
            @Comment("Whether the bot should block, report and keep your server clean of phishing links.")
            public boolean block_phishing = true;
            @Comment("Additional domains you want removed. Only works if block_phishing is true.")
            public List<String> banned_domains = new ArrayList<>();
            @Comment("A link to the place where your rules are stated.")
            public String rules_link = "";
        }
    }

    @Comment("Miscellaneous features for the bot to run on your server. All of these are off by default.")
    public Miscellaneous miscellaneous = new Miscellaneous();
    public static class Miscellaneous {
        @Comment("""
                The crash analysing module will analyse crash logs and output a helpful message to help fix the crash.
                Note: this feature is currently limited to analysing Quilt and Fabric loader logs.""")
        public CrashAnalysing crash_analysing = new CrashAnalysing();
        public static class CrashAnalysing {
            public boolean enabled = false;
            @Comment("""
                The name of the channel which the bot will watch for logs. ) e.g. "moderation"
                Set to "new" to have one made automatically.
                Leave empty to allow log parsing everywhere""")
            public String watch_channel = "";
            @Comment("""
                    A list of mod ids to watch out for when parsing, that go against the rules of your server.
                    This has a few mods in here already that are considered cheat mods globally.
                    May be a regular expression or wildcard.""")
            public List<String> bad_mods = new ArrayList<>();
        }

        @Comment("""
                Set up a Suggestion Forum Channel, where your players can suggest features or other things while the bot makes
                sure it's all neatly organised for your comfort and ease of use!""")
        public SuggestionForum suggestion_forum = new SuggestionForum();
        public static class SuggestionForum {
            public boolean enabled = false;
            @Comment("Role and user ids that are considered administrators for Suggestion Forums.")
            public AdministratorList administrators = new AdministratorList();
            @Comment("""
                    The name of the channel in which the bot will use to host the suggestion forum. e.g. "suggestions"
                    Contrary to what you'd believe, this is still a regular text channel! Do not feed a Forum channel id
                    into this setting!
                    Set to "new" to have one made automatically.""")
            public String forum_channel = "";
            @Comment("""
                    This setting is for those who want to automatically answer suggestions in case there are FAQ set in place
                    or if you don't want your players requesting specific things and explain why.
                    
                    This configuration value consists of a List [] that has a so called AnswerMap inside with the following structure:
                    {
                      "id": "update",       <-- A unique identifier for this answer. Used internally.
                      "triggers": [         <-- A list of Strings that indicate the "triggers" that result in the auto-answer.
                        "Update to",            If a message contains this word(s) it will count as a trigger message.
                        "Downgrade to"      |   This list also supports RegEx.
                      ],                    V   A single String which is the auto-answer given to the post.
                      "answer": "We won't update the server because it would break crucial mods we use.",
                      "status": "Denied"    <-- One of: Open, Approved, Denied, Invalid, Spam, Future, Stale, Duplicate, Implemented
                    }
                    
                    Your config might look something like this:
                    
                    "auto_answer": [
                      {
                        "id": "update",
                        "triggers": [
                          "Update to",
                          "Downgrade to"
                        ],
                        "answer": "We won't update the server because it would break crucial mods we use.",
                        "status": "Denied",
                      },
                      {
                        "id": "loader",
                        "triggers": [
                          "Use Forge",
                          "use (forge|quilt)|change( .+)? to (forge|quilt)|make( it)? (forge|quilt)",
                        ],
                        "answer": "We won't use a different mod loader because they don't have some crucial mods we use.",
                        "status": "Denied"
                      }, <-- TIP: You can use so called "trailing" commas because the format is JSON5!
                    ]""")
            public List<AnswerMap> auto_answer = new ArrayList<>();
            public static class AnswerMap {
                public final String id = "";
                public final List<String> triggers = new ArrayList<>();
                public final String answer = "";
                public final String status = "Denied";
            }
            @Comment("Whether to close answered suggestions.")
            public boolean close_answered = true;
            @Comment("Whether to delete answered suggestions completely.")
            public boolean delete_answered = false;
        }

        @Comment("""
                The PluralKit software allows you to add accessibility to the bot for Plural people and Systems.
                No idea what plurality is? No worries! There are enough sources online that can explain it neatly.
                We personally recommend reading the following one: https://quiltmc.org/en/community/pluralkit/, as it also
                nicely explains how the PluralKit software works and how to use it.
                
                Enabling this module is non-invasive, purely helpful, and will only fully work once you add the PluralKit
                bot to your server yourself.""")
        public PluralKit plural_kit = new PluralKit();
        public static class PluralKit {
            public boolean enabled = false;
        }

        @Comment("""
                Using this module you can host an AMA (Ask Me Anything) in your server, which enables your community
                to ask you questions about a certain topic related to your server, like upcoming changes or recent updates.
                
                More settings can be configured in Discord.""")
        public AMA ama = new AMA();
        public static class AMA {
            public boolean enabled = false;
            @Comment("Role and user ids that are considered administrators for an AMA.")
            public AdministratorList administrators = new AdministratorList();
        }

        @Comment("""
                Using this module you can save so called tags for users to run to quickly answer questions without the need
                of messy message links or copy-paste messages. The bot can even send them for you!""")
        public Tags tags = new Tags();
        public static class Tags {
            public boolean enabled = false;
            @Comment("Role and user ids that are considered administrators for managing tags.")
            public AdministratorList administrators = new AdministratorList();
            @Comment("Whether to automatically reply to a message with a tag if it fits certain triggers.")
            public boolean auto_tag = true;
        }
        
        @Comment("""
                Set up channels with flush embeds using this module! Simple, yet effective. And uh swag I guess.
                """)
        public EmbedChannels embed_channels = new EmbedChannels();
        public static class EmbedChannels {
            public boolean enabled = false;
            @Comment("Role and user ids that are considered administrators for managing the embed channels.")
            public AdministratorList administrators = new AdministratorList();
            @Comment("""
                    A list of channel ids and their channel links.
                    Note that for this module, the bot is unable to make new channels automatically.
                    Why is this a map and not a list, or even better, a singular entry? Well, the external library used to handle these
                    channels works on top of a map like this, for their respective reasons, we just follow. Don't fret though!
                    This is one of the least hard setups out here, just simply put the id in the first parentheses, and the full link in the second!
                    e.g.: "3848576687382457654": "https://discord.com/channels/4837388485758686865/3848576687382457654
                    
                    Note: after you run this channel stuff, the id numbers might become a little messed up. No worries, this is normal!
                    For the technical people out there: read our blog about "F*cked up number configs\"""")
            public Map<String, String> channels = new HashMap<>();
        }
    }

    @Override
    public void validate() {
        Function<String, String> watch = (t) -> {
            if(!Objects.equals(t, "new") && !t.matches("[a-z\\-]+") && !t.isEmpty()) return "";
            return t;
        };
        minecraft.watch_channel = watch.apply(minecraft.watch_channel);
        safety_and_abuse.dump_channel = watch.apply(safety_and_abuse.dump_channel);
        miscellaneous.suggestion_forum.forum_channel = watch.apply(miscellaneous.suggestion_forum.forum_channel);
        miscellaneous.crash_analysing.watch_channel = watch.apply(miscellaneous.crash_analysing.watch_channel);

        if (!List.of("none", "minimal", "standard", "extra").contains(safety_and_abuse.data_collection)) safety_and_abuse.data_collection = "standard";
    }
}
