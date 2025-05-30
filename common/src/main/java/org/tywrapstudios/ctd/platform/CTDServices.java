package org.tywrapstudios.ctd.platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tywrapstudios.ctd.platform.services.IEventHelper;
import org.tywrapstudios.ctd.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

public class CTDServices {
    protected static final Logger LOGGER = LoggerFactory.getLogger(CTDServices.class);

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final IEventHelper EVENTS = load(IEventHelper.class);

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}