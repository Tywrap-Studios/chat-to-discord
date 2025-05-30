package org.tywrapstudios.ctd.platform;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import org.tywrapstudios.ctd.platform.services.IPlatformHelper;

import java.io.File;

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
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
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