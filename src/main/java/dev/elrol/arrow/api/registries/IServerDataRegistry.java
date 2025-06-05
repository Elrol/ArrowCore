package dev.elrol.arrow.api.registries;

import dev.elrol.arrow.api.data.IServerData;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface IServerDataRegistry {

    void load();

    void save();

    <T extends IServerData> T get(@NonNull T defaultObject);

    <T extends IServerData> void put(T data);

    <T extends IServerData> void put(T data, boolean save);

}
