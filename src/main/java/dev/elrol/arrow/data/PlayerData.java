package dev.elrol.arrow.data;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.elrol.arrow.ArrowCore;
import dev.elrol.arrow.api.data.IPlayerData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PlayerData {


    public final Map<String, JsonElement> data = new HashMap<>();
    final UUID uuid;

    public PlayerData(ServerPlayerEntity player) {
        uuid = player.getUuid();
    }

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public boolean hasData(String dataID) {
        return data.containsKey(dataID.toLowerCase());
    }

    @NotNull
    public <T extends IPlayerData> T get(@NotNull T defaultObject) {
        Codec<T> codec = defaultObject.getCodec();
        if(codec != null) {
            String id = defaultObject.getDataID();
            if(data.containsKey(id)) {
                DataResult<Pair<T, JsonElement>> result = codec.decode(JsonOps.INSTANCE, data.get(id));
                if (result.isSuccess()) {
                    if(ArrowCore.CONFIG.isDebug)
                        ArrowCore.LOGGER.warn("Result is successful: {}", result);
                    return result.getOrThrow().getFirst();
                }
            } else {
                put(defaultObject);
            }
        }
        if(ArrowCore.CONFIG.isDebug)
            ArrowCore.LOGGER.warn("Default Object: {}", defaultObject);
        return defaultObject;
    }

    public <T extends IPlayerData> boolean put(T dataObject, boolean save){
        Codec<T> codec = dataObject.getCodec();
        if(codec == null) {
            if(ArrowCore.CONFIG.isDebug)
                ArrowCore.LOGGER.error("Codec was null");
            return false;
        }

        DataResult<JsonElement> result = codec.encodeStart(JsonOps.INSTANCE, dataObject);

        if(result.isSuccess()) {
            data.put(dataObject.getDataID(), result.getOrThrow());

            if(save) {
                ArrowCore.INSTANCE.getPlayerDataRegistry().save(uuid, this);
                if(ArrowCore.CONFIG.isDebug)
                    ArrowCore.LOGGER.warn("Data Encoding Finished");
            }

            return true;
        }
        if(ArrowCore.CONFIG.isDebug)
            ArrowCore.LOGGER.error("Result Failed");
        return false;
    }

    public <T extends IPlayerData> boolean put(T dataObject) {
        return put(dataObject, false);
    }

}
