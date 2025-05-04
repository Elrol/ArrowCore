package dev.elrol.arrow.menus;

import dev.elrol.arrow.ArrowCore;
import dev.elrol.arrow.libs.ModTranslations;
import dev.elrol.arrow.registries.CoreMenuItems;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class _PageMenuBase extends _MenuBase {

    public int startingIndex;
    public int page;

    public _PageMenuBase(ServerPlayerEntity player) {
        super(player, ScreenHandlerType.GENERIC_9X5);
        page = 0;
    }

    public _PageMenuBase(ServerPlayerEntity player, int page) {
        super(player, ScreenHandlerType.GENERIC_9X5);
        this.page = page;
    }

    @Override
    protected void drawMenu() {
        startingIndex = this.page * 15;

        super.drawMenu();

        setSlot(36, GuiElementBuilder.from(new ItemStack(page <= 0 ? CoreMenuItems.LEFT_BUTTON_DISABLED : CoreMenuItems.LEFT_BUTTON)).setCallback((index, clickType, slotActionType, slotGuiInterface) -> {
                    if (page > 0) {
                        page--;
                        Objects.requireNonNull(ArrowCore.INSTANCE.getMenuRegistry().createMenu(getMenuName(), player)).open();
                    }
                }).setName(ModTranslations.translate("arrow.menu.item.page.next").formatted(page < getLastPage() ? Formatting.RED : Formatting.DARK_GRAY))
        );

        setSlot(44, GuiElementBuilder.from(new ItemStack(page >= getLastPage() ? CoreMenuItems.RIGHT_BUTTON_DISABLED : CoreMenuItems.RIGHT_BUTTON)).setCallback((index, clickType, slotActionType, slotGuiInterface) -> {
                    if (page < getLastPage()) {
                        page++;
                        //_PageMenuBase pageMenu = new _PageMenuBase(player, page);
                        _PageMenuBase pageMenu = (_PageMenuBase) ArrowCore.INSTANCE.getMenuRegistry().createMenu(getMenuName(), player);
                        if(pageMenu != null) {
                            pageMenu.page = page;
                            pageMenu.open();
                        }
                    }
                }).setName(ModTranslations.translate("arrow.menu.item.page.next").formatted(page < getLastPage() ? Formatting.GREEN : Formatting.DARK_GRAY))
        );
    }

    protected <T> void drawItems(Map<String, T> map) {
        List<String> keys = new ArrayList<>(map.keySet());

        for(int i = 0; i < 15; i++) {
            int itemIndex = i + startingIndex;
            if(itemIndex >= keys.size()) break;
            String key = keys.get(itemIndex);

            int x = i % 5;
            int y = (i - x) / 5;

            ArrowCore.LOGGER.warn("X: {} Y: {}", x, y);

            if(map.containsKey(key)) {
                int index = (y * 9) + x + 11;
                GuiElementBuilder element = createElement(key, map);
                menu.setSlot(index, element);
            }
        }
    }

    protected abstract <T> GuiElementBuilder createElement(String key, Map<String, T> map);

    @Override
    public void returnToMenu() {
        super.returnToMenu();
    }

    public abstract int getLastPage();
}
