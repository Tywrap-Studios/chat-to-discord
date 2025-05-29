package org.tywrapstudios.ctd;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class CTDFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        CTDCommon.init();
    }
}
