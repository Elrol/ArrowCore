package dev.elrol.arrow.registries;

import com.mojang.serialization.MapCodec;
import dev.elrol.arrow.api.data.IPlayerData;
import dev.elrol.arrow.data.PlayerDataType;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class PlayerDataTypes {

    static final Map<String, PlayerDataType<?>>  TYPE_MAP = new HashMap<>();

    public static <T extends IPlayerData> void register(String id, MapCodec<T> codec) {
        TYPE_MAP.putIfAbsent(id, Registry.register(PlayerDataType.REGISTRY, Identifier.of("arrow", id), new PlayerDataType<>(codec)));
    }

    @Nonnull
    public static PlayerDataType<?> get(String modid) {
        if(TYPE_MAP.containsKey(modid)) return TYPE_MAP.get(modid);
        throw new RuntimeException("PlayerDataType not registered for mod: " + modid);
    }

}
