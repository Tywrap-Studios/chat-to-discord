package org.tywrapstudios.ctd.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.lucko.spark.common.platform.world.AsyncWorldInfoProvider;
import org.tywrapstudios.ctd.compat.Spark;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

@Mixin(value = AsyncWorldInfoProvider.class, remap = false)
@Debug(export = true)
public abstract class AsyncWorldInfoProviderMixin {
    @Inject(
            method = "get",
            at = @At(value = "INVOKE",
                    target = "Lme/lucko/spark/common/SparkPlugin;log(Ljava/util/logging/Level;Ljava/lang/String;)V"),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            remap = false
    )
    private void ctd$printTimedOutStackTrace(CompletableFuture<?> future, CallbackInfoReturnable<?> cir, @Local TimeoutException e) {
        Spark.handleSparkWorldTimeOut(e);
    }
}
