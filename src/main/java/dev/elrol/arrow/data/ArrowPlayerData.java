package dev.elrol.arrow.data;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.arrow.ArrowCore;
import dev.elrol.arrow.api.data.IPlayerData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArrowPlayerData {

    public static final Codec<ArrowPlayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, IPlayerData.PLAYER_DATA_CODEC).fieldOf("data").forGetter(data -> {
                if(data.newData == null) return new HashMap<>();
                return data.newData;
            }),
            Codec.STRING.fieldOf("uuid").forGetter(data -> data.uuid.toString())
    ).apply(instance, (newData, uuid) -> {
        ArrowPlayerData data = new ArrowPlayerData(UUID.fromString(uuid));
        if(newData == null) newData = new HashMap<>();
        data.newData.putAll(newData);
        return data;
    }));

    public Map<String, JsonElement> data = new HashMap<>();
    public Map<String, IPlayerData> newData = new HashMap<>();

    public final UUID uuid;

    public ArrowPlayerData(ServerPlayerEntity player) {
        uuid = player.getUuid();
    }

    public ArrowPlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public boolean hasData(String dataID) {
        return newData.containsKey(dataID.toLowerCase());
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public <T extends IPlayerData> T get(@NotNull T defaultObject) {
        Codec<T> codec = (Codec<T>) defaultObject.getCodec().codec();
        if(codec != null) {
            String id = defaultObject.getDataID();
            if(newData == null) newData = new HashMap<>();
            if(newData.containsKey(id)) {
                return (T) newData.get(id);
            } else {
                if(data.containsKey(id)) {
                    JsonElement json = data.get(id);
                    T dataObject = codec.decode(JsonOps.INSTANCE, json).getOrThrow().getFirst();
                    put(dataObject);
                    return dataObject;
                }
                put(defaultObject);
            }
        }
        if(ArrowCore.CONFIG.isDebug)
            ArrowCore.LOGGER.warn("Data wasn't found. Creating new instance");
        return defaultObject;
    }

    @SuppressWarnings("unchecked")
    public <T extends IPlayerData> boolean put(T dataObject, boolean save){
        Codec<T> codec = (Codec<T>) dataObject.getCodec().codec();
        if(codec == null) {
            if(ArrowCore.CONFIG.isDebug)
                ArrowCore.LOGGER.error("Codec was null");
            return false;
        }
        if(newData == null) newData = new HashMap<>();
        newData.put(dataObject.getDataID(), dataObject);

        if(save) {
            ArrowCore.INSTANCE.getPlayerDataRegistry().save(uuid, this);
        }

        return true;
    }

    public <T extends IPlayerData> boolean put(T dataObject) {
        return put(dataObject, false);
    }

}
