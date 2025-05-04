package dev.elrol.arrow.menus;

import dev.elrol.arrow.ArrowCore;
import dev.elrol.arrow.libs.MenuUtils;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class DevMenu extends _PageMenuBase {

    public static Map<String, DevMenuButton> buttonsToAdd = new HashMap<>();

    public final Map<String, GuiElementBuilder> DEV_MENU_MAP = new HashMap<>();

    private final GuiElementBuilder goldTest;
    private final GuiElementBuilder eggTest;
    private final GuiElementBuilder slimeTest;

    public <T extends ScreenHandler> DevMenu(ServerPlayerEntity player) {
        super(player);

        goldTest = MenuUtils.item(Items.GOLD_INGOT, 1, "Get 100$");

        eggTest = MenuUtils.item(Items.EGG, 1, "Egg");
        eggTest.addLoreLine(Text.literal("Lore"));
        eggTest.addLoreLine(Text.literal(" - 0"));

        slimeTest = MenuUtils.item(Items.SLIME_BALL, 1, "dev");

        DEV_MENU_MAP.putIfAbsent("gold", goldTest);
        DEV_MENU_MAP.putIfAbsent("egg", eggTest);
        DEV_MENU_MAP.putIfAbsent("slime", slimeTest);
    }



    @Override
    protected void drawMenu() {
        super.drawMenu();

        goldTest.setCallback((type) -> ArrowCore.INSTANCE.getEconomyRegistry().deposit(player, BigDecimal.valueOf(100)));
        slimeTest.setCallback(() -> navigateToMenu("dev"));

        menu.createTimer(new TimerTask() {
            @Override
            public void run() {
                List<Text> lore = new ArrayList<>();
                lore.add(Text.literal("Lore"));
                lore.add(Text.literal(" - " + LocalDateTime.now().getSecond()));

                eggTest.setLore(lore);
            }
        }, 1000, 1000);

        buttonsToAdd.forEach((id, function) -> DEV_MENU_MAP.put(id, function.addButton(player)));

        drawItems(DEV_MENU_MAP);
    }

    @Override
    protected <T> GuiElementBuilder createElement(String key, Map<String, T> map) {
        return (GuiElementBuilder) map.get(key);
    }

    @Override
    public int getLastPage() {
        return Math.floorDiv(DEV_MENU_MAP.size(), 15);
    }

    @Override
    public int getMenuID() {
        return 0;
    }

    @Override
    public String getMenuName() {
        return "dev";
    }

    @Override
    public char getMenuUnicode() {
        return '≇';
    }

    @FunctionalInterface
    public interface DevMenuButton {
        GuiElementBuilder addButton(ServerPlayerEntity player);
    }
}
