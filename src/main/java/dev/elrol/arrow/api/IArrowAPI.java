package dev.elrol.arrow.api;

import dev.elrol.arrow.api.registries.*;

public interface IArrowAPI {

    ICommandRegistry            getCommandRegistry();
    IEventRegistry              getEventRegistry();
    IMenuRegistry               getMenuRegistry();
    IPermissionRegistry         getPermissionRegistry();
    IPlayerDataRegistry         getPlayerDataRegistry();
    IServerDataRegistry         getServerDataRegistry();
    IEconomyRegistry            getEconomyRegistry();
}
