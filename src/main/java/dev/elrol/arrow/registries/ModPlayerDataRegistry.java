package dev.elrol.arrow.registries;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.elrol.arrow.ArrowCore;
import dev.elrol.arrow.api.data.IPlayerData;
import dev.elrol.arrow.api.events.ArrowEvents;
import dev.elrol.arrow.api.registries.IPlayerDataRegistry;
import dev.elrol.arrow.data.ArrowPlayerData;
import dev.elrol.arrow.data.PlayerDataCore;
import dev.elrol.arrow.data.PlayerDataType;
import dev.elrol.arrow.libs.ArrowCoreConstants;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ModPlayerDataRegistry implements IPlayerDataRegistry {

    public static final Gson GSON = ArrowCoreConstants.makeGSON();

    private final Map<UUID, ArrowPlayerData> playerDataMap = new HashMap<>();

    public boolean hasPlayerData(UUID uuid) {
        return new File(ArrowCoreConstants.PLAYER_DATA_DIR, uuid.toString() + ".dat").exists() || playerDataMap.containsKey(uuid);
    }

    @NotNull
    public ArrowPlayerData getPlayerData(ServerPlayerEntity player){
        return getPlayerData(player.getUuid());
    }

    @NotNull
    public ArrowPlayerData getPlayerData(UUID uuid) {
        return playerDataMap.containsKey(uuid) ? playerDataMap.get(uuid) : load(uuid);
    }

    public void save(UUID uuid, ArrowPlayerData data) {
        if(ArrowCore.CONFIG.useDatabase) {

        } else {
            File file = new File(ArrowCoreConstants.PLAYER_DATA_DIR, uuid + ".json");
            try(FileWriter writer = new FileWriter(file)) {
                if(data != null) {
                    DataResult<JsonElement> result = ArrowPlayerData.CODEC.encodeStart(JsonOps.INSTANCE, data);
                    GSON.toJson(result.getOrThrow(), writer);

                    if (ArrowCore.CONFIG.isDebug)
                        ArrowCore.LOGGER.info("New data saved in files for: {}", uuid);
                } else {
                    ArrowCore.LOGGER.error("PlayerData was null");

                }
            } catch (IOException e) {
                ArrowCore.LOGGER.error(e.getLocalizedMessage());
            }
        }
    }

    public void save(ServerPlayerEntity player) {
        save(player.getUuid());
    }

    public void save(UUID uuid) {
        save(uuid, getPlayerData(uuid));
    }

    public void saveAll(){
        for (UUID uuid : playerDataMap.keySet()) {
            ArrowPlayerData data = playerDataMap.get(uuid);
            PlayerDataCore coreData = data.get(new PlayerDataCore());
            save(uuid, data);
        }
    }

    public ArrowPlayerData load(ServerPlayerEntity player) {
        return load(player.getUuid());
    }

    public ArrowPlayerData load(UUID uuid){
        ArrowPlayerData data = null;
        if(ArrowCore.CONFIG.useDatabase) {
            //data = DatabaseUtils.loadPlayerData(uuid);
            if(ArrowCore.CONFIG.isDebug)
                ArrowCore.LOGGER.info("Data loaded from database for: {}", uuid);
        } else {
            try {
                File newJsonFile = new File(ArrowCoreConstants.PLAYER_DATA_DIR, uuid + ".json");
                File datFile = new File(ArrowCoreConstants.PLAYER_DATA_DIR, uuid + ".dat");

                if(newJsonFile.exists()) {
                    FileReader reader = new FileReader(newJsonFile);
                    JsonElement json = GSON.fromJson(reader, TypeToken.get(JsonElement.class));
                    data = ArrowPlayerData.CODEC.decode(JsonOps.INSTANCE, json).getOrThrow().getFirst();
                } else {
                    if(!datFile.exists()) {
                        data = new ArrowPlayerData(uuid);
                        save(uuid, data);
                    } else {
                        FileReader reader = new FileReader(datFile);
                        data = GSON.fromJson(reader, TypeToken.get(ArrowPlayerData.class));

                        if(ArrowCore.CONFIG.isDebug)
                            ArrowCore.LOGGER.info("Data loaded from files for: {}", uuid);
                    }
                }
            } catch (IOException e) {
                ArrowCore.LOGGER.error(e.getMessage());
            }
        }
        if (data == null) {
            data = new ArrowPlayerData(uuid);
            save(uuid, data);
        }

        playerDataMap.put(uuid, data);
        return data;
    }

    @Override
    public List<ArrowPlayerData> loadAll() {
        List<ArrowPlayerData> list = new ArrayList<>();
        String[] fileNames = ArrowCoreConstants.PLAYER_DATA_DIR.list((file, name) -> name.endsWith(".dat"));

        if(fileNames != null) {
            for (String files : fileNames) {
                ArrowCore.LOGGER.info(files);
                list.add(load(UUID.fromString(files.replace(".dat", ""))));
            }
        }
        ArrowEvents.ALL_PLAYER_DATA_LOADED_EVENT.invoker().loaded(list);

        saveAll();
        return list;
    }

    @Override
    public Map<UUID, ArrowPlayerData> getLoadedData() {
        return playerDataMap;
    }

}
