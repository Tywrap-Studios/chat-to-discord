package org.tywrapstudios.ctd.mixin;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.tywrapstudios.ctd.handlers.Handlers;

@Mixin(Commands.class)
public abstract class CommandManagerMixin {

    @Inject(method = "performCommand", at = @At(value = "HEAD"))
    private void ctd$handleCommandMessage(ParseResults<CommandSourceStack> parseResults, String command, CallbackInfo ci) throws CommandSyntaxException {
        if (command.startsWith("say") || command.startsWith("me")) {
            String message = parseResults.getReader().getString()
                    .replace("say", "")
                    .replace("me", "")
                    .trim();
            String authorUUID;
            String authorName;
            try {
                authorUUID = parseResults.getContext().getSource().getEntityOrException().getStringUUID();
                authorName = parseResults.getContext().getSource().getTextName();
            } catch (CommandSyntaxException e) {
                authorUUID = "console";
                authorName = "Console";
            }

            Handlers.handleChatMessage(message, authorUUID, authorName);
        }
    }
}
