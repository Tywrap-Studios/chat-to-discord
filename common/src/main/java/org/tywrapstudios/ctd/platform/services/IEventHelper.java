package org.tywrapstudios.ctd.platform.services;

public interface IEventHelper {
    void registerServerStarted();

    void registerServerStopped();

    void registerChatMessage();

    void registerGameMessage();

    void registerCommandMessage();

    void registerCommand();

    default void registerAll() {
        registerServerStarted();
        registerServerStopped();
        registerChatMessage();
        registerGameMessage();
        registerCommandMessage();
        registerCommand();
    }
}
