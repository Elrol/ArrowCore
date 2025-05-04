package dev.elrol.arrow.libs;

import com.chocohead.mm.api.ClassTinkerers;
import dev.elrol.arrow.ArrowCore;
import net.minecraft.entity.boss.BossBar;

public class BarColor {

    public static BossBar.Color BLACK;
    public static BossBar.Color DARK_BLUE;
    public static BossBar.Color DARK_GREEN;
    public static BossBar.Color DARK_AQUA;
    public static BossBar.Color DARK_RED;
    public static BossBar.Color DARK_PURPLE;
    public static BossBar.Color GOLD;
    public static BossBar.Color GRAY;
    public static BossBar.Color DARK_GRAY;
    public static BossBar.Color BLUE;
    public static BossBar.Color GREEN;
    public static BossBar.Color AQUA;
    public static BossBar.Color RED;
    public static BossBar.Color LIGHT_PURPLE;
    public static BossBar.Color YELLOW;
    public static BossBar.Color WHITE;

    public static void register() {
        BLACK           = get("A_BLACK");
        DARK_BLUE       = get("A_DARK_BLUE");
        DARK_GREEN      = get("A_DARK_GREEN");
        DARK_AQUA       = get("A_DARK_AQUA");
        DARK_RED        = get("A_DARK_RED");
        DARK_PURPLE     = get("A_DARK_PURPLE");
        GOLD            = get("A_GOLD");
        GRAY            = get("A_GRAY");
        DARK_GRAY       = get("A_DARK_GRAY");
        BLUE            = get("A_BLUE");
        GREEN           = get("A_GREEN");
        AQUA            = get("A_AQUA");
        RED             = get("A_RED");
        LIGHT_PURPLE    = get("A_LIGHT_PURPLE");
        YELLOW          = get("A_YELLOW");
        WHITE           = get("A_WHITE");

        ArrowCore.LOGGER.warn("Black BarColor is {}", BarColor.BLACK.getName());
    }

    public static BossBar.Color get(String name) {
        BossBar.Color color;

        try {
            color = ClassTinkerers.getEnum(BossBar.Color.class, name);
        } catch(IllegalArgumentException e) {
            ArrowCore.LOGGER.error(e.getMessage());
            color = BossBar.Color.WHITE;
        }

        return color;
    }
}
