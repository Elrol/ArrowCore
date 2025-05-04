package dev.elrol.arrow.api.registries;

import java.util.function.Supplier;

public interface IEventRegistry {

    void register();
    void registerEvent(Runnable registerEvent);

}
