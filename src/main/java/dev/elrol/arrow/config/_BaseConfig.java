package dev.elrol.arrow.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.elrol.arrow.libs.JsonUtils;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;

public abstract class _BaseConfig {
    private static final File configDir = new File(FabricLoader.getInstance().getConfigDir().toFile(), "/Arrow");
    private final String fileName;

    public _BaseConfig(String fileName) {
        this.fileName = fileName;
    }

    public void save() {
        DataResult<JsonElement> jsonResult = getCodec().encodeStart(JsonOps.INSTANCE, this);
        JsonElement json = jsonResult.getOrThrow();
        JsonUtils.saveToJson(configDir, fileName + ".conf", json);
    }

    @SuppressWarnings("unchecked")
    public <T extends _BaseConfig> T load() {
        JsonElement json = JsonUtils.loadFromJson(configDir, fileName + ".conf", JsonParser.parseString("{}"));
        DataResult<Pair<_BaseConfig, JsonElement>> configPair = getCodec().decode(JsonOps.INSTANCE, json);
        if(configPair.isError()) {
            save();
            return (T) this;
        }
        return (T) configPair.getOrThrow().getFirst();
    }

    public abstract <T extends _BaseConfig> Codec<T> getCodec();

}
