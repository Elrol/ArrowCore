package dev.elrol.arrow.data;

import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import dev.elrol.arrow.api.data.IPlayerData;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

public record PlayerDataType<T extends IPlayerData> (MapCodec<T> codec) {
    public static final Registry<PlayerDataType<?>> REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(Identifier.of("arrow", "player_data_types")), Lifecycle.stable());
}
