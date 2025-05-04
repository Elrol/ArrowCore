package dev.elrol.arrow.libs;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Random;

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

}
