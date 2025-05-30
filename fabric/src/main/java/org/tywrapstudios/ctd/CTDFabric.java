package org.tywrapstudios.ctd;

import net.fabricmc.api.ModInitializer;

public class CTDFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        CTDCommon.init();
    }
}
