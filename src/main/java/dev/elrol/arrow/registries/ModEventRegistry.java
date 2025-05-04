package dev.elrol.arrow.registries;

import dev.elrol.arrow.ArrowCore;
import dev.elrol.arrow.api.events.RegisterEventCallback;
import dev.elrol.arrow.api.registries.IEventRegistry;

import java.util.ArrayList;
import java.util.List;

public class ModEventRegistry implements IEventRegistry {

    List<Runnable> suppliers = new ArrayList<>();

    @Override
    public void register() {

        RegisterEventCallback.EVENT.invoker().register();

        suppliers.forEach(Runnable::run);

        if(ArrowCore.CONFIG.isDebug)
            ArrowCore.LOGGER.warn("Events Registered");
    }

    @Override
    public void registerEvent(Runnable registerEvent) {
        suppliers.add(registerEvent);
    }
}
