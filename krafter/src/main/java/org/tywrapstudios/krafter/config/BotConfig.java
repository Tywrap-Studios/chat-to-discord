package org.tywrapstudios.krafter.config;

import blue.endless.jankson.Comment;
import org.tywrapstudios.blossombridge.api.config.BasicConfigClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class BotConfig extends BasicConfigClass {
    @Comment("Whether the bot should be run altogether.")
    public boolean enabled = true;
    @Comment("""
            Role and user ids that are considered global administrators for the bot.
            They most notably have full permission over most of the SAB and Misc functionality.
            Don't worry though, you can set separate admins for separate functions in their respective configs.""")
    public AdministratorList administrators = new AdministratorList(new ArrayList<>(), new ArrayList<>());
    public record AdministratorList(List<String> users, List<String> roles){}
    @Comment("Discord to MC Chat functionality specifics.")
    public DiscordToChat discord_to_chat = new DiscordToChat();
    public static class DiscordToChat {
        @Comment("Whether Discord messages should be sent to the MC Chat altogether.")
        public boolean enabled = true;
        @Comment("""
                A channel name in which the bot will watch for messages to send. e.g. "mc-chat"
                Set to "new" to have one made automatically.""")
        public String watch_channel = "";
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
                A channel name in which the bot will post SAB related messages. Not required! e.g. "moderation"
                Set to "new" to have one made automatically.""")
        public String dump_channel = "";
        @Comment("Role and user ids that are considered administrators for SAB functionality.")
        public AdministratorList administrators = new AdministratorList(new ArrayList<>(), new ArrayList<>());
        @Comment("""
                YOOHOO!
                The bot software collects data!
                This value must be one of "none", "minimal", "standard" or "extra" to be valid,
                otherwise we'll set it back to "standard" by default. Can only be applied after a full server restart.
                
                For more information on what data the bot collects, how to get at it, and how it's stored,
                please see here: https://docs.kordex.dev/data-collection.html""")
        public String data_collection = "standard";
        @Comment("Whether operators should receive the General Use warning every time they join. Only works if run on a Minecraft server.")
        public boolean operator_warning = true;
        public Moderation moderation = new Moderation();
        public static class Moderation {
            @Comment("Whether the bot should block, report and keep your server clean of phishing links.")
            public boolean block_phishing = true;
            @Comment("Additional domains you want removed. Only works if block_phishing is true.")
            public List<String> banned_domains = new ArrayList<>();
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
        }

        @Comment("""
                Set up a Suggestion Forum Channel, where your players can suggest features or other things while the bot makes
                sure it's all neatly organised for your comfort and ease of use!""")
        public SuggestionForum suggestion_forum = new SuggestionForum();
        public static class SuggestionForum {
            public boolean enabled = false;
            @Comment("Role and user ids that are considered administrators for Suggestion Forums.")
            public AdministratorList administrators = new AdministratorList(new ArrayList<>(), new ArrayList<>());
            @Comment("""
                    A channel name in which the bot will use to host the suggestion forum. e.g. "suggestions"
                    Contrary to what you'd believe, this is still a regular text channel! Do not feed a Forum channel id
                    into this setting!
                    Set to "new" to have one made automatically.""")
            public String forum_channel = "";
            @Comment("""
                    This setting is for those who want to automatically answer suggestions in case there are FAQ set in place
                    or if you don't want your players requesting specific things and explain why.
                    
                    This configuration value consists of a List [] that has a so called AnswerMap inside with the following structure:
                    {
                      "triggers": [         <-- A list of Strings that indicate the "triggers" that result in the auto-answer.
                        "Update to",            If a message contains this word(s) it will count as a trigger message.
                        "Downgrade to"      |
                      ],                    V   A single String which is the auto-answer given to the post.
                      "answer": "We won't update the server because it would break crucial mods we use.",
                    }
                    
                    Your config might look something like this:
                    
                    "auto_answer": [
                      {
                        "triggers": [
                          "Update to",
                          "Downgrade to"
                        ],
                        "answer": "We won't update the server because it would break crucial mods we use.",
                      },
                      {
                        "triggers": [
                          "Use Forge",
                          "Use Quilt",
                        ],
                        "answer": "We won't use a different mod loader because they don't have some crucial mods we use.",
                      },    <-- TIP: You can use so called "trailing" commas because the format is JSON5!
                    ]""")
            public List<AnswerMap> auto_answer = new ArrayList<>();
            public record AnswerMap(List<String> triggers, String answer){}
            @Comment("Whether to close answered suggestions.")
            public boolean close_answered = true;
            @Comment("Whether to delete answered suggestions completely.")
            public boolean delete_answered = false;
        }

        @Comment("""
                The PluralKit software allows you to add accessibility to the bot for Plural people and Systems.
                No idea what being Plural means? No worries! There are enough sources online that can explain it neatly.
                We personally recommend reading the following one: https://quiltmc.org/en/community/pluralkit/, as it also
                nicely explains how the PluralKit software works and how to use it. Note that you also need the
                PluralKit bot in your server for this to work, as it's not a standalone feature of this mod.""")
        public PluralKit plural_kit = new PluralKit();
        public static class PluralKit {
            public boolean enabled = false;
        }

        @Comment("""
                Using this module you can host an AMA (Ask Me Anything) in your server, which enables your community
                to ask you questions about a certain topic related to your server, like upcoming changes or recent updates.""")
        public AMA ama = new AMA();
        public static class AMA {
            public boolean enabled = false;
            @Comment("Role and user ids that are considered administrators for an AMA.")
            public AdministratorList administrators = new AdministratorList(new ArrayList<>(), new ArrayList<>());
        }

        @Comment("""
                Using this module you can save so called tags for users to run to quickly answer questions without the need
                of messy message links or copy-paste messages. The bot can even send them for you!""")
        public Tags tags = new Tags();
        public static class Tags {
            public boolean enabled = false;
            @Comment("Role and user ids that are considered administrators for an AMA.")
            public AdministratorList administrators = new AdministratorList(new ArrayList<>(), new ArrayList<>());
            @Comment("Whether to automatically reply to a message with a tag if it fits certain triggers.")
            public boolean auto_tag = true;
        }
    }

    @Override
    public void validate() {
        Function<String, String> watch = (t) -> {
            if(!Objects.equals(t, "new") && !t.matches("[0-9]+") && !t.isEmpty()) return "";
            return t;
        };
        discord_to_chat.watch_channel = watch.apply(discord_to_chat.watch_channel);
        safety_and_abuse.dump_channel = watch.apply(safety_and_abuse.dump_channel);
        miscellaneous.suggestion_forum.forum_channel = watch.apply(miscellaneous.suggestion_forum.forum_channel);

        if (!List.of("none", "minimal", "standard", "extra").contains(safety_and_abuse.data_collection)) safety_and_abuse.data_collection = "standard";
    }
}
