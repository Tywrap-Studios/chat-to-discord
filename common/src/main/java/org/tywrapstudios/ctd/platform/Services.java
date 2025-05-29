package org.tywrapstudios.ctd.platform;

import org.tywrapstudios.ctd.CTDCommon;
import org.tywrapstudios.ctd.platform.services.IEventHelper;
import org.tywrapstudios.ctd.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

public class Services {
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final IEventHelper EVENTS = load(IEventHelper.class);

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        CTDCommon.LOGGING.debug(String.format("Loaded %s for service %s", loadedService, clazz));
        return loadedService;
    }
}