package dev.elrol.arrow.api.registries;

import dev.elrol.arrow.data.ArrowPlayerData;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IPlayerDataRegistry {

    boolean hasPlayerData(UUID uuid);

    ArrowPlayerData getPlayerData(ServerPlayerEntity player);
    ArrowPlayerData getPlayerData(UUID uuid);

    void updatePlayerData(ServerPlayerEntity player, ArrowPlayerData playerData);
    void updatePlayerData(UUID uuid, ArrowPlayerData playerData);

    void save(UUID uuid, ArrowPlayerData data);
    void save(ServerPlayerEntity player);
    void save(UUID uuid);
    void saveAll();

    ArrowPlayerData load(ServerPlayerEntity player);
    ArrowPlayerData load(UUID uuid);
    List<ArrowPlayerData> loadAll();
    Map<UUID, ArrowPlayerData> getLoadedData();
}
