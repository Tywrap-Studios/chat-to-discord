package org.tywrapstudios.ctd.mixin;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.ReloadCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.tywrapstudios.ctd.handlers.Handlers;

@Mixin(ReloadCommand.class)
public abstract class ReloadCommandMixin {
    @Inject(method = "lambda$register$3",
            at = @At(value = "HEAD")
    )
    private static void ctd$reloadMyConfig(CommandContext<CommandSourceStack> context, CallbackInfoReturnable<Integer> cir) {
        Handlers.handleConfigReload(context);
    }
}
