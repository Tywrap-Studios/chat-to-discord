package org.tywrapstudios.ctd;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod("ctd")
public class CTDNeoForge {

    public CTDNeoForge(IEventBus eventBus) {
        CTDCommon.init();
    }
}