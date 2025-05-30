package org.tywrapstudios.ctd.mixin;

import net.minecraft.CrashReport;
import net.minecraft.ReportType;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.tywrapstudios.ctd.CTDCommon;
import org.tywrapstudios.ctd.handlers.Handlers;

import java.nio.file.Path;
import java.util.List;

@Mixin(CrashReport.class)
@Debug(export = true)
public abstract class CrashReportMixin {
    @Shadow public abstract String getExceptionMessage();

    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "saveToFile(Ljava/nio/file/Path;Lnet/minecraft/ReportType;Ljava/util/List;)Z",
            at = @At(value = "TAIL"))
    private void ctd$sendWebhookOnCrash(Path path, ReportType type, List<String> links, CallbackInfoReturnable<Boolean> cir) {
        String cause = getExceptionMessage();
        try {
            Handlers.handleCrash(cause, path);
        } catch (Exception e) {
            LOGGER.error("An error occurred while trying to send the crash report to Discord. Please check the logs for more information.");
            //CTDCommon.LOGGING.error("An error occurred while trying to send the crash report to Discord. Please check the logs for more information.");
            e.printStackTrace();
        }
    }
}
