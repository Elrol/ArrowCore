package dev.elrol.arrow.registries;

import dev.elrol.arrow.ArrowCore;
import dev.elrol.arrow.api.registries.IMenuRegistry;
import dev.elrol.arrow.menus._MenuBase;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ModMenuRegistry implements IMenuRegistry {

    private static final Map<String, String> menus = new HashMap<>();

    @Override
    public void registerMenu(String name, Class<? extends _MenuBase> clazz) {
        if(menus.containsKey(name)) {
            ArrowCore.LOGGER.error("A menu with the name: {} has already been registered", name);
            return;
        }
        menus.put(name, clazz.getName());
    }

    @Override
    public _MenuBase createMenu(String name, @Nullable ServerPlayerEntity player) {
        if(!menus.containsKey(name)) {
            ArrowCore.LOGGER.error("The menu {} isn't registered", name);
            return null;
        }
        if(player == null) {
            ArrowCore.LOGGER.error("The player was null");
            return null;
        }
        try {
            Class<?> clazz = Class.forName(menus.get(name));
            Constructor<?> constructor = clazz.getDeclaredConstructor(ServerPlayerEntity.class);
            Object obj = constructor.newInstance(player);
            if(obj instanceof _MenuBase menu) return menu;
            throw new RuntimeException("The class " + name + " was not a valid Menu class");
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exists(String name) {
        return menus.containsKey(name);
    }
}
