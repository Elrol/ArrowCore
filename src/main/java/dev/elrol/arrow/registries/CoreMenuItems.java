package dev.elrol.arrow.registries;

import dev.elrol.arrow.ArrowCore;
import dev.elrol.arrow.api.registries.IItemRegistry;
import dev.elrol.arrow.libs.Constants;
import dev.elrol.arrow.libs.ModUtils;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class CoreMenuItems implements IItemRegistry {

    public static Item RIGHT_BUTTON_DISABLED;
    public static Item LEFT_BUTTON_DISABLED;
    public static Item RIGHT_BUTTON;
    public static Item LEFT_BUTTON;
    public static Item BACK_BUTTON;

    public void register() {
        RIGHT_BUTTON_DISABLED   = get("gui_right_button_disabled");
        LEFT_BUTTON_DISABLED    = get("gui_left_button_disabled");
        RIGHT_BUTTON            = get("gui_right_button");
        LEFT_BUTTON             = get("gui_left_button");
        BACK_BUTTON             = get("gui_back_button");

        if(RIGHT_BUTTON.equals(Items.AIR)) ArrowCore.LOGGER.error("Right Button was not found");
        else ArrowCore.LOGGER.warn("Right Button was found");
    }

    @Override
    public String getModID() {
        return Constants.MODID;
    }

}
