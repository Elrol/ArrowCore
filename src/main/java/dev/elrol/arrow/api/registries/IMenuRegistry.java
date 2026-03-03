package dev.elrol.arrow.api.registries;

import dev.elrol.arrow.menus._MenuBase;
import net.minecraft.server.network.ServerPlayerEntity;

public interface IMenuRegistry {

    void registerMenu(String name, Class<? extends _MenuBase> clazz);

    <T extends _MenuBase> T createMenu(String name, ServerPlayerEntity player);

    boolean exists(String name);
}
