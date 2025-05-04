package dev.elrol.arrow.api.events;

import dev.elrol.arrow.data.PlayerData;
import dev.elrol.arrow.registries.ModServerDataRegistry;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public final class ArrowEvents {

    public static final Event<PlayerDataLoaded> PLAYER_DATA_LOADED_EVENT = EventFactory.createArrayBacked(PlayerDataLoaded.class, (listeners) -> (player, data) -> {
        for(PlayerDataLoaded listener : listeners) {
            listener.loaded(player, data);
        }
    });

    public static final Event<PlayerDataUnloading> PLAYER_DATA_UNLOADING_EVENT = EventFactory.createArrayBacked(PlayerDataUnloading.class, (listeners) -> (player, data) -> {
        for(PlayerDataUnloading listener : listeners) {
            listener.unloading(player, data);
        }
    });

    public static final Event<ServerDataLoaded> SERVER_DATA_LOADED_EVENT = EventFactory.createArrayBacked(ServerDataLoaded.class, (listeners) -> (data -> {
        for(ServerDataLoaded listener : listeners) {
            listener.loaded(data);
        }
    }));

    public static final Event<ConfigLoaded> CONFIG_LOADED_EVENT = EventFactory.createArrayBacked(ConfigLoaded.class, (listeners) -> () -> {
        for(ConfigLoaded listener : listeners) {
            listener.loaded();
        }
    });

    @FunctionalInterface
    public interface PlayerDataLoaded {
        void loaded(ServerPlayerEntity player, PlayerData data);
    }

    @FunctionalInterface
    public interface PlayerDataUnloading {
        void unloading(ServerPlayerEntity player, PlayerData data);
    }

    @FunctionalInterface
    public interface ServerDataLoaded {
        void loaded(ModServerDataRegistry data);
    }

    @FunctionalInterface
    public interface ConfigLoaded {
        void loaded();
    }
}
