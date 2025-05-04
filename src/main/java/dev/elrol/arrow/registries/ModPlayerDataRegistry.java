package dev.elrol.arrow.registries;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dev.elrol.arrow.ArrowCore;
import dev.elrol.arrow.api.registries.IPlayerDataRegistry;
import dev.elrol.arrow.data.PlayerData;
import dev.elrol.arrow.libs.Constants;
import dev.elrol.arrow.libs.DatabaseUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ModPlayerDataRegistry implements IPlayerDataRegistry {

    public static final Gson GSON = Constants.makeGSON();

    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    public boolean hasPlayerData(UUID uuid) {
        return new File(Constants.PLAYER_DATA_DIR, uuid.toString() + ".dat").exists() || playerDataMap.containsKey(uuid);
    }

    @NotNull
    public PlayerData getPlayerData(ServerPlayerEntity player){
        return getPlayerData(player.getUuid());
    }

    @NotNull
    public PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.containsKey(uuid) ? playerDataMap.get(uuid) : load(uuid);
    }

    public void save(UUID uuid, PlayerData settings){
        if(ArrowCore.CONFIG.useDatabase && false) {
            DatabaseUtils.savePlayerData(uuid, settings);

            if(ArrowCore.CONFIG.isDebug)
                ArrowCore.LOGGER.info("Data saved in database for: {}", uuid);

        } else {
            File file = new File(Constants.PLAYER_DATA_DIR, uuid.toString() + ".dat");
            if (Constants.PLAYER_DATA_DIR.mkdirs()) {
                ArrowCore.LOGGER.warn("PlayerData folder created. If this happens multiple times, there is an issue");
            }
            try (FileWriter writer = new FileWriter(file)) {
                GSON.toJson(settings, writer);

                if(ArrowCore.CONFIG.isDebug)
                    ArrowCore.LOGGER.info("Data saved in files for: {}", uuid);

            } catch (IOException e) {
                throw new RuntimeException(e);
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
        playerDataMap.forEach(this::save);
    }

    public PlayerData load(ServerPlayerEntity player) {
        return load(player.getUuid());
    }

    public PlayerData load(UUID uuid){
        PlayerData data = null;
        if(ArrowCore.CONFIG.useDatabase && false) {
            data = DatabaseUtils.loadPlayerData(uuid);
            if(ArrowCore.CONFIG.isDebug)
                ArrowCore.LOGGER.info("Data loaded from database for: {}", uuid);
        } else {
            try {
                File datFile = new File(Constants.PLAYER_DATA_DIR, uuid.toString() + ".dat");
                if(!datFile.exists()) {
                    data = new PlayerData(uuid);
                    save(uuid, data);
                } else {
                    FileReader reader = new FileReader(datFile);
                    data = GSON.fromJson(reader, TypeToken.get(PlayerData.class));

                    if(ArrowCore.CONFIG.isDebug)
                        ArrowCore.LOGGER.info("Data loaded from files for: {}", uuid);
                }
            } catch (IOException e) {
                ArrowCore.LOGGER.error(e.getMessage());
            }
        }
        if (data == null) {
            data = new PlayerData(uuid);
            save(uuid, data);
        }

        playerDataMap.put(uuid, data);
        return data;
    }

    @Override
    public List<PlayerData> loadAll() {
        List<PlayerData> list = new ArrayList<>();
        String[] fileNames = Constants.PLAYER_DATA_DIR.list();

        if(fileNames != null) {
            for (String files : fileNames) {
                ArrowCore.LOGGER.info(files);
                list.add(load(UUID.fromString(files.replace(".dat", ""))));
            }
        }

        return list;
    }

    @Override
    public Map<UUID, PlayerData> getLoadedData() {
        return playerDataMap;
    }

}
