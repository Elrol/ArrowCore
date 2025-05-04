package dev.elrol.arrow;

import dev.elrol.arrow.api.IArrowAPI;
import dev.elrol.arrow.api.registries.*;
import dev.elrol.arrow.registries.*;

public class ArrowAPI implements IArrowAPI {

    ICommandRegistry            commandRegistry         = new ModCommandRegistry();
    IEventRegistry              eventRegistry           = new ModEventRegistry();
    IMenuRegistry               menuRegistry            = new ModMenuRegistry();
    IPermissionRegistry         permissionRegistry      = new ModPermissionRegistry();
    IPlayerDataRegistry         playerDataRegistry      = new ModPlayerDataRegistry();
    IServerDataRegistry         serverDataRegistry      = new ModServerDataRegistry();
    IEconomyRegistry            economyRegistry         = new ModEconomyRegistry();

    @Override
    public ICommandRegistry getCommandRegistry() {
        return commandRegistry;
    }

    @Override
    public IEventRegistry getEventRegistry() {
        return eventRegistry;
    }

    @Override
    public IMenuRegistry getMenuRegistry() {
        return menuRegistry;
    }

    @Override
    public IPermissionRegistry getPermissionRegistry() {
        return permissionRegistry;
    }

    @Override
    public IPlayerDataRegistry getPlayerDataRegistry() {
        return playerDataRegistry;
    }

    @Override
    public IServerDataRegistry getServerDataRegistry() {
        return serverDataRegistry;
    }

    @Override
    public IEconomyRegistry getEconomyRegistry() {
        return economyRegistry;
    }

}
