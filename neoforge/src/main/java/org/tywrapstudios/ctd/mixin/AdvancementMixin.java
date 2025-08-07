package org.tywrapstudios.ctd.mixin;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.tywrapstudios.ctd.handlers.Handlers;

@Mixin(PlayerAdvancements.class)
public abstract class AdvancementMixin {
    @Shadow private ServerPlayer player;

    @Inject(method = "lambda$award$2", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    private void ctd$handleGameMessage$ADVANCEMENT(AdvancementHolder advancement, DisplayInfo p_352686_, CallbackInfo ci) {
        Handlers.handleGameMessage(p_352686_.getType().createAnnouncement(advancement, this.player));
    }
}
