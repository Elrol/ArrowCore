package dev.elrol.arrow.libs;

import dev.elrol.arrow.ArrowCore;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.UUID;

public class ModUtils {

    public static String formatSeconds(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static boolean temptFate(float chance, float min, float max) {
        float selected = new Random().nextFloat(min, max);
        return chance > selected;
    }

    public static Item getItem(String id) {
        return Registries.ITEM.get(Identifier.of(id));
    }

    @Nullable
    public static ServerPlayerEntity getPlayer(UUID uuid) {
        MinecraftServer server = ArrowCore.getServer();
        if(server != null) {
            return server.getPlayerManager().getPlayer(uuid);
        }
        return null;
    }
}
