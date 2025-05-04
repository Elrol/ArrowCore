package dev.elrol.arrow.libs;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokeball.PokeBall;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.elrol.arrow.ArrowCore;
import dev.elrol.arrow.data.adapter.RegistryKeyWorldTypeAdapter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Constants {

    public static String MODID = "arrowcore";
    public static final File ARROW_DATA_DIR = new File(FabricLoader.getInstance().getGameDir().toFile(), "/arrow_data");
    public static final File PLAYER_DATA_DIR = new File(ARROW_DATA_DIR, "/player_data");
    public static File SERVER_DATA_DIR;

    public static List<Stats> IV_STATS = Arrays.asList(Stats.HP, Stats.ATTACK, Stats.DEFENCE, Stats.SPECIAL_ATTACK, Stats.SPECIAL_DEFENCE, Stats.SPEED);

    public static Gson makeGSON() {
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().registerTypeAdapter(new TypeToken<RegistryKey<World>>(){}.getType(), new RegistryKeyWorldTypeAdapter()).create();
    }

    public static int getPokeballID(PokeBall ball) {
        if(ArrowCore.CONFIG.isDebug) ArrowCore.LOGGER.warn(ball.getName().toString());
        return switch (ball.getName().toString().replace("cobblemon:", "").replace("_ball", "")) {
            case "poke" -> 1;
            case "citrine" -> 2;
            case "verdant" -> 3;
            case "azure" -> 4;
            case "roseate" -> 5;
            case "slate" -> 6;
            case "premier" -> 7;
            case "great" -> 8;
            case "ultra" -> 9;
            case "safari" -> 10;
            case "fast" -> 11;
            case "level" -> 12;
            case "lure" -> 13;
            case "heavy" -> 14;
            case "love" -> 15;
            case "friend" -> 16;
            case "moon" -> 17;
            case "sport" -> 18;
            case "park" -> 19;
            case "net" -> 20;
            case "dive" -> 21;
            case "nest" -> 22;
            case "repeat" -> 23;
            case "timer" -> 24;
            case "luxury" -> 25;
            case "dusk" -> 26;
            case "heal" -> 27;
            case "quick" -> 28;
            case "dream" -> 29;
            case "beast" -> 30;
            case "master" -> 31;
            case "cherish" -> 32;
            case "ancient_poke" -> 33;
            case "ancient_citrine" -> 34;
            case "ancient_verdant" -> 35;
            case "ancient_azure" -> 36;
            case "ancient_roseate" -> 37;
            case "ancient_slate" -> 38;
            case "ancient_ivory" -> 39;
            case "ancient_great" -> 40;
            case "ancient_ultra" -> 41;
            case "ancient_feather" -> 42;
            case "ancient_wing" -> 43;
            case "ancient_jet" -> 44;
            case "ancient_heavy" -> 45;
            case "ancient_leaden" -> 46;
            case "ancient_gigaton" -> 47;
            case "ancient_origin" -> 48;
            default -> 0;
        };
    }
}
