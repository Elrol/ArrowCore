package dev.elrol.arrow.registries;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.elrol.arrow.ArrowCore;
import dev.elrol.arrow.api.data.IServerData;
import dev.elrol.arrow.api.events.ArrowEvents;
import dev.elrol.arrow.api.registries.IServerDataRegistry;
import dev.elrol.arrow.libs.ArrowCoreConstants;
import dev.elrol.arrow.libs.JsonUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class ModServerDataRegistry implements IServerDataRegistry {

    Map<String, JsonElement> serverDataMap = new HashMap<>();

    public void load() {
        if(ArrowCoreConstants.SERVER_DATA_DIR != null && ArrowCoreConstants.SERVER_DATA_DIR.exists()){
            ModServerDataRegistry tempData = JsonUtils.loadFromJson(ArrowCoreConstants.SERVER_DATA_DIR, "server_data.dat", this);
            serverDataMap = tempData.serverDataMap;
            save();
            ArrowCore.LOGGER.info("Server Data Loaded Successfully");
            ArrowEvents.SERVER_DATA_LOADED_EVENT.invoker().loaded(this);
        }
    }

    public void save() {
        JsonUtils.saveToJson(ArrowCoreConstants.SERVER_DATA_DIR, "server_data.dat", this);
    }

    @NonNull
    public <T extends IServerData> T get(@NonNull T defaultObject) {
        String key = defaultObject.getDataID();
        Codec<T> codec = defaultObject.getCodec();
        if (codec != null) {
            if (serverDataMap.containsKey(key)) {
                DataResult<Pair<T, JsonElement>> result = codec.decode(JsonOps.INSTANCE, serverDataMap.get(key));
                if (result.isSuccess()) {
                    return result.getOrThrow().getFirst();
                }
            } else {
                serverDataMap.put(defaultObject.getDataID(), codec.encodeStart(JsonOps.INSTANCE, defaultObject).getOrThrow());
                save();
            }
        }
        return defaultObject;
    }

    @Override
    public <T extends IServerData> void put(T data) {
        put(data, false);
    }

    public <T extends IServerData> void put(T data, boolean save) {
        serverDataMap.put(data.getDataID(), data.getCodec().encodeStart(JsonOps.INSTANCE, data).getOrThrow());
        if(save) save();
    }
}
