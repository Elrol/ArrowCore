package dev.elrol.arrow.api.registries;

import dev.elrol.arrow.data.Currency;
import dev.elrol.arrow.data.PlayerData;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IPlayerDataRegistry {

    boolean hasPlayerData(UUID uuid);

    PlayerData getPlayerData(ServerPlayerEntity player);
    PlayerData getPlayerData(UUID uuid);

    void save(UUID uuid, PlayerData data);
    void save(ServerPlayerEntity player);
    void save(UUID uuid);
    void saveAll();

    PlayerData load(ServerPlayerEntity player);
    PlayerData load(UUID uuid);
    List<PlayerData> loadAll();
    Map<UUID, PlayerData> getLoadedData();
}
