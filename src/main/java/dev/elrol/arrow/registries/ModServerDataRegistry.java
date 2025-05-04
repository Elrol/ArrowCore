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
import dev.elrol.arrow.libs.Constants;
import dev.elrol.arrow.libs.JsonUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ModServerDataRegistry implements IServerDataRegistry {

    Map<String, JsonElement> serverDataMap = new HashMap<>();

    public void load() {
        if(Constants.SERVER_DATA_DIR != null && Constants.SERVER_DATA_DIR.exists()){
            ModServerDataRegistry tempData = JsonUtils.loadFromJson(Constants.SERVER_DATA_DIR, "server_data.dat", this);
            serverDataMap = tempData.serverDataMap;
            save();
            ArrowCore.LOGGER.info("Server Data Loaded Successfully");
            ArrowEvents.SERVER_DATA_LOADED_EVENT.invoker().loaded(this);
        }
    }

    public void save() {
        JsonUtils.saveToJson(Constants.SERVER_DATA_DIR, "server_data.dat", this);
    }

    public <T extends IServerData> T get(Class<T> clazz) {
        try {
            T newInstance = clazz.getDeclaredConstructor().newInstance();
            String key = newInstance.getDataID();
            Codec<T> codec = newInstance.getCodec();
            if(codec != null) {
                if(serverDataMap.containsKey(key)) {
                    DataResult<Pair<T, JsonElement>> result = codec.decode(JsonOps.INSTANCE, serverDataMap.get(key));
                    if(result.isSuccess()) {
                        return result.getOrThrow().getFirst();
                    }
                } else {
                    serverDataMap.put(newInstance.getDataID(), codec.encodeStart(JsonOps.INSTANCE, newInstance).getOrThrow());
                    save();
                }
            }
            return newInstance;
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
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
