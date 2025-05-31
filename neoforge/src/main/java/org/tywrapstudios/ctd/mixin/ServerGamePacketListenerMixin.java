package org.tywrapstudios.ctd.mixin;

import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.tywrapstudios.ctd.handlers.Handlers;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerMixin {
    @Shadow public ServerPlayer player;

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void ctd$handleGameMessage$PLAYER_DISCONNECT(DisconnectionDetails details, CallbackInfo ci) {
        if (!details.reason().getString().equals("Disconnected")) Handlers.handleGameMessage(String.format("%s lost connection: %s", this.player.getName().getString(), details.reason().getString()));
    }

    @Inject(method = "removePlayerFromWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    private void ctd$handleGameMessage$PLAYER_LEFT(CallbackInfo ci) {
        Handlers.handleGameMessage(Component.translatable("multiplayer.player.left", this.player.getDisplayName()).getString());
    }
}
