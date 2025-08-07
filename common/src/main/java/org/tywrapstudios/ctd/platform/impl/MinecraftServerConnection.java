package org.tywrapstudios.ctd.platform.impl;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.tywrapstudios.ctd.CTDCommon;
import org.tywrapstudios.ctd.command.CTDCommand;
import org.tywrapstudios.krafter.api.discord.McAuthor;
import org.tywrapstudios.krafter.api.discord.McMessage;
import org.tywrapstudios.krafter.platform.services.IMinecraftServerConnection;

import java.util.concurrent.ExecutionException;

public class MinecraftServerConnection implements IMinecraftServerConnection {
    private static MinecraftServer server;

    public static void init(@NotNull MinecraftServer server) {
        if (MinecraftServerConnection.server != null) {
            throw new IllegalStateException("MinecraftServerConnection is already initialized.");
        }
        MinecraftServerConnection.server = server;
    }

    @Override
    public void broadcast(@NotNull McMessage message) {
        try {
            McAuthor author = message.getAuthor().get();
            String userText = author.getMcName() == null ? "@" + author.getName() : author.getMcName();
            var comp$1 = Component
                    .literal("[" + userText + "] ")
                    .setStyle(Style.EMPTY
                            .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, author.getMention()))
                            .withHoverEvent(new HoverEvent(
                                    HoverEvent.Action.SHOW_TEXT,
                                    Component.literal("@" + author.getUsername()).withStyle(ChatFormatting.DARK_PURPLE)
                            ))
                            .withColor(ChatFormatting.BLUE)
                    );
            var comp$2 = Component
                    .literal(message.getContent())
                    .withStyle(Style.EMPTY
                            .withHoverEvent(new HoverEvent(
                                    HoverEvent.Action.SHOW_TEXT,
                                    Component.literal("Sent from Discord").withStyle(ChatFormatting.DARK_PURPLE)
                            ))
                            .withColor(ChatFormatting.WHITE));
            server.getPlayerList().broadcastSystemMessage(comp$1.append(comp$2), false);
        } catch (ExecutionException | InterruptedException e) {
            CTDCommon.LOGGING.error("Failed to broadcast message: {}", e.getMessage());
        }
    }

    @Override
    public void broadcastPlain(@NotNull String message) {
        server.sendSystemMessage(Component.literal(message));
    }
}
