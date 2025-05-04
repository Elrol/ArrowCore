package dev.elrol.arrow.libs;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dev.elrol.arrow.ArrowCore;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class JsonUtils {

    public static void saveToJson(File dir, String name, Object obj) {
        Gson GSON = Constants.makeGSON();
        File file = new File(dir, name);

        if(dir.mkdirs()) {
            ArrowCore.LOGGER.warn("{} directory for ArrowCore created. If this happens more than once, there is an issue.", dir);
        }

        if(!file.exists()) {
            try {
                if(file.createNewFile()) {
                    ArrowCore.LOGGER.warn("New file {} created.", name);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try(FileWriter writer = new FileWriter(file)) {
            GSON.toJson(obj, writer);
            if(FabricLoader.getInstance().isDevelopmentEnvironment()) {
                ArrowCore.LOGGER.info("Saved File {}", name);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public static <T> T loadFromJson(File dir, String name, T defaultObject) {
        File file = new File(dir, name);


        TypeToken<T> type = new TypeToken<>(){};
        if(file.exists()) {
            try(FileReader reader = new FileReader(file)) {
                Gson GSON = Constants.makeGSON();
                T obj = GSON.fromJson(reader, (Class<T>) defaultObject.getClass());

                if(obj != null) {
                    if(ArrowCore.CONFIG.isDebug) {
                        ArrowCore.LOGGER.info("Loaded File {}", name);
                    }
                    return obj;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        saveToJson(dir, name, defaultObject);
        return defaultObject;

    }

}
