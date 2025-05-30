package org.tywrapstudios.ctd.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.tywrapstudios.ctd.CTDCommon;
import org.tywrapstudios.ctd.compat.Spark;
import org.tywrapstudios.ctd.config.CTDConfig;
import org.tywrapstudios.ctd.handlers.Handlers;
import org.tywrapstudios.ctd.platform.Services;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class CTDCommand {
    public CTDCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ctd").requires((source) -> source.hasPermission(2))
                .executes(CTDCommand::execute)
                .then(Commands.literal("reload")
                        .executes(CTDCommand::reload))
                .then(Commands.literal("debug")
                        .requires((source) -> source.hasPermission(3))
                        .then(Commands.literal("dump_config")
                                .executes(CTDCommand::dumpConfig))
                        .then(Commands.literal("force_chat")
                                .executes(CTDCommand::forceChat))
                        .then(Commands.literal("force_game")
                                .executes(CTDCommand::forceGameMessage))
                        .then(Commands.literal("force_crash")
                                .executes(CTDCommand::forceCrashMessage))
                        .then(Commands.literal("force_timeout")
                                .executes(CTDCommand::forceTimeoutMessage))
                )
        );
    }

    private static int forceTimeoutMessage(CommandContext<CommandSourceStack> context) {
        Spark.handleSparkWorldTimeOut(new TimeoutException("DEBUG TIMEOUT"));
        return 1;
    }

    private static int forceCrashMessage(CommandContext<CommandSourceStack> context) {
        Handlers.handleCrash("DEBUG CAUSE", null);
        return 1;
    }

    private static int forceGameMessage(CommandContext<CommandSourceStack> context) {
        Handlers.handleGameMessage("Debug message");
        return 1;
    }

    private static int forceChat(CommandContext<CommandSourceStack> context) {
        try {
            Handlers.handleChatMessage("Debug message", context.getSource().getPlayer().getStringUUID(), context.getSource().getPlayer().getName().getString());
        } catch (NullPointerException e) {
            Handlers.handleChatMessage("Debug message", "console", "Console");
        }
        return 1;
    }

    private static int dumpConfig(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String message = String.format("""
                    --------[Config]---------
                    %s
                    -----------------------""",
                CTDCommon.CONFIG_MANAGER.getConfigJsonAsString(false, true));
        source.sendSuccess(() -> Component.literal(message).withStyle(ChatFormatting.GRAY), false);
        return 1;
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        CTDConfig config = CTDCommon.CONFIG_MANAGER.getConfig();
        List<String> webhooks = config.discord_config.discord_webhooks;

        CommandSourceStack source = context.getSource();
        String whenDebug = config.util_config.debug_mode ? "\n> Debug Enabled" : "";
        String message = String.format("""
                    -----[Chat To Discord]-----
                    > Mod Version: %s
                    > Mod Platform: %s
                    > Config Version: %s
                    > Embed Mode: %s
                    > Only Messages Mode: %s
                    > Webhooks Defined: %s%s
                    -----------------------""", CTDCommon.MOD_V, Services.PLATFORM.getPlatformName(), CTDCommon.CONFIG_MANAGER.getConfig().format_version, config.discord_config.embed_mode, config.discord_config.only_send_messages, webhooks.size(), whenDebug);
        source.sendSuccess(() -> Component.literal(message).withStyle(ChatFormatting.BLUE), false);
        return 1;
    }

    private static int reload(CommandContext<CommandSourceStack> context) {
        return Handlers.handleConfigReload(context);
    }
}
