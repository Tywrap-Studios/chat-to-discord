package org.tywrapstudios.ctd.platform;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
import org.tywrapstudios.ctd.platform.services.IPlatformHelper;

import java.io.File;
import java.nio.file.Path;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public String getModVersion(String modId) {
        return ModList.get().getModContainerById(modId).orElseThrow().getModInfo().getVersion().toString();
    }

    @Override
    public Path getGamePath() {
        return FMLLoader.getGamePath();
    }

    @Override
    public boolean isModLoaded(String modId) {
        ModList list = ModList.get();
        if (list == null) {
            ModFileInfo modFileInfo = FMLLoader.getLoadingModList().getModFileById(modId);
            return modFileInfo != null;
        }
        return list.isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public File getConfigDirectory() {
        return new File(FMLLoader.getGamePath().toFile(), "config");
    }
}