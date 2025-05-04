package dev.elrol.arrow.api.registries;

import dev.elrol.arrow.api.data.IServerData;

public interface IServerDataRegistry {

    void load();

    void save();

    <T extends IServerData> T get(Class<T> clazz);

    <T extends IServerData> void put(T data);

    <T extends IServerData> void put(T data, boolean save);

}
