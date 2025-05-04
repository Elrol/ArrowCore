package dev.elrol.arrow.menus;

import dev.elrol.arrow.ArrowCore;
import dev.elrol.arrow.data.PlayerData;
import dev.elrol.arrow.data.PlayerDataCore;
import dev.elrol.arrow.libs.MenuUtils;
import dev.elrol.arrow.libs.ModTranslations;
import dev.elrol.arrow.registries.CoreMenuItems;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public abstract class _MenuBase {
    public ServerPlayerEntity player;
    public PlayerData data;
    public _ModMenu menu;

    public <T extends ScreenHandler> _MenuBase(ServerPlayerEntity player, ScreenHandlerType<T> type) {
        this.player = player;
        this.data = ArrowCore.INSTANCE.getPlayerDataRegistry().getPlayerData(player.getUuid());
        this.menu = new _ModMenu(type, player, false);
        this.menu.setTitle(Text.literal("七七七七七七七七" + getMenuUnicode()).formatted(Formatting.WHITE));
        this.menu.onClose();
    }

    public void open() {
        open(false);
    }

    public void open(boolean clearHistory) {
        if(clearHistory) {
            PlayerDataCore coreData = data.get(new PlayerDataCore());
            coreData.menuHistory.clear();
            data.put(coreData);
        }
        drawMenu();
        menu.open();
    }

    public void close() {
        menu.close();
    }

    protected void drawMenu() {
        setSlot(8, backButton());
    }

    protected GuiElementBuilder backButton() {
        return MenuUtils.item(CoreMenuItems.BACK_BUTTON, 1, ModTranslations.translate("arrow.menu.item.name.back").formatted(Formatting.RED, Formatting.BOLD)).setCallback((index, clickType, slotActionType, slotGuiInterface) -> {
            click();
            returnToMenu();
        });
    }

    public void returnToMenu() {
        PlayerDataCore coreData = data.get(new PlayerDataCore());

        if (!coreData.menuHistory.isEmpty()) {
            _MenuBase newMenu = ArrowCore.INSTANCE.getMenuRegistry().createMenu(coreData.menuHistory.getFirst(), player);
            coreData.menuHistory.removeFirst();
            data.put(coreData);
            if (newMenu != null) {
                newMenu.open();
                close();
            } else {
                close();
            }
        } else {
            close();
        }
    }

    protected void navigateToMenu(_MenuBase menu) {
        PlayerDataCore coreData = data.get(new PlayerDataCore());
        coreData.menuHistory.addFirst(getMenuName());
        data.put(coreData);
        menu.open();
        close();
    }

    protected void navigateToMenu(String name) {
        _MenuBase newMenu = ArrowCore.INSTANCE.getMenuRegistry().createMenu(name, player);
        if (newMenu != null) {
            navigateToMenu(newMenu);
        } else {
            ArrowCore.LOGGER.error("Menu was null for type {}", name);
        }
    }

    public abstract int getMenuID();

    public void setSlot(int index, GuiElementBuilder element, int dataOffset) {
        setSlot(index, element.build());
    }

    public void setSlot(int index, GuiElementBuilder element) {
        if(element == null) return;
        setSlot(index, element.build());
    }

    public void setSlot(int index, GuiElement element) {
        if(element == null) return;
        menu.setSlot(index, element);
    }

    protected void click() {
        player.getServerWorld().playSound(null, player.getBlockPos(), SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.MASTER, 1.0f, 1.0f);
    }

    public abstract String getMenuName();
    public abstract char getMenuUnicode();
}
