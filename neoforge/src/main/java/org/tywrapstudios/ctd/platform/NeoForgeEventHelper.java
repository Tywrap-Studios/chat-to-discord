package org.tywrapstudios.ctd.platform;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.ServerOpList;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.tywrapstudios.ctd.command.CTDCommand;
import org.tywrapstudios.ctd.handlers.Handlers;
import org.tywrapstudios.ctd.platform.services.IEventHelper;

public class NeoForgeEventHelper implements IEventHelper {
    @Override
    public void registerServerStarted() {
        NeoForge.EVENT_BUS.addListener((ServerStartedEvent event) -> {
            Handlers.handleChatMessage("Server started.","console","Console");
        });
    }

    @Override
    public void registerServerStopped() {
        NeoForge.EVENT_BUS.addListener((ServerStoppedEvent event) -> {
            Handlers.handleChatMessage("Server stopped.","console","Console");
        });
    }

    @Override
    public void registerChatMessage() {
        NeoForge.EVENT_BUS.addListener((ServerChatEvent event) -> {
            String message = event.getMessage().getString();
            String authorUUID = event.getPlayer().getUUID().toString();
            String authorName = event.getUsername();

            Handlers.handleChatMessage(message, authorUUID, authorName);
        });
    }

    // NeoForge doesn't supply sufficient events for Game messages and Command messages.
    // The handling for these is instead done by Mixins.
    @Override
    public void registerGameMessage() {

    }

    @Override
    public void registerCommandMessage() {

    }

    @Override
    public void registerCommand() {
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> {
            CTDCommand.register(event.getDispatcher());
        });
    }

    @Override
    public void registerPlayerJoin() {
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
            Player player = event.getEntity();
            if (player instanceof ServerPlayer serverPlayer) {
                if (serverPlayer.server.getPlayerList().isOp(serverPlayer.getGameProfile())) {
                    Handlers.warnOperator(serverPlayer);
                }
            }
        });
    }
}
