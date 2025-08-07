package org.tywrapstudios.ctd.platform;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.tywrapstudios.ctd.command.CTDCommand;
import org.tywrapstudios.ctd.handlers.Handlers;
import org.tywrapstudios.ctd.platform.impl.MinecraftServerConnection;
import org.tywrapstudios.ctd.platform.services.IEventHelper;

public class FabricEventHelper implements IEventHelper {
    @Override
    public void registerServerStarted() {
        ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
            MinecraftServerConnection.init(minecraftServer);
            Handlers.handleChatMessage("Server started.","console","Console");
        });
    }

    @Override
    public void registerServerStopped() {
        ServerLifecycleEvents.SERVER_STOPPED.register(minecraftServer -> {
            Handlers.handleChatMessage("Server stopped.","console","Console");
        });
    }

    @Override
    public void registerChatMessage() {
        ServerMessageEvents.CHAT_MESSAGE.register((signedMessage, serverPlayerEntity, bound) -> {
            String message = signedMessage.decoratedContent().getString();
            String authorUUID = signedMessage.sender().toString();
            String authorName = serverPlayerEntity.getName().getString();

            Handlers.handleChatMessage(message, authorUUID, authorName);
        });
    }

    @Override
    public void registerGameMessage() {
        ServerMessageEvents.GAME_MESSAGE.register((minecraftServer, text, b) -> {
            Handlers.handleGameMessage(text);
        });
    }

    @Override
    public void registerCommandMessage() {
        ServerMessageEvents.COMMAND_MESSAGE.register((signedMessage, serverCommandSource, bound) -> {
            String message = signedMessage.decoratedContent().getString();
            String authorUUID = signedMessage.sender().toString();
            String authorName = serverCommandSource.getTextName();

            Handlers.handleChatMessage(message, authorUUID, authorName);
        });
    }

    @Override
    public void registerCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, registrationEnvironment) -> {
            CTDCommand.register(dispatcher);
        });
    }

    @Override
    public void registerPlayerJoin() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (handler.player.server.getPlayerList().isOp(handler.player.getGameProfile())) {
                Handlers.warnOperator(handler.player);
            }
        });
    }
}
