package org.tywrapstudios.ctd.platform;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.ServerChatEvent;
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
}
