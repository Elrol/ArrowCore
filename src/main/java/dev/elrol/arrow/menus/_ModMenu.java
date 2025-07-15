package dev.elrol.arrow.menus;

import dev.elrol.arrow.api.events.MenuCloseCallback;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Timer;
import java.util.TimerTask;

public class _ModMenu extends SimpleGui {

    protected Timer timer;
    public final String menuName;
    boolean canClose = true;

    /**
     * Constructs a new simple container gui for the supplied player.
     *
     * @param type                  the screen handler that the client should display
     * @param player                the player to server this gui to
     * @param manipulatePlayerSlots if <code>true</code> the players inventory
     *                              will be treated as slots of this gui
     */
    public _ModMenu(String menuName, ScreenHandlerType<?> type, ServerPlayerEntity player, boolean manipulatePlayerSlots) {
        super(type, player, manipulatePlayerSlots);
        this.menuName = menuName;
    }

    @Override
    public void onClose() {
        if(timer != null) cancelTimer();
        MenuCloseCallback.EVENT.invoker().onClose(this);
        super.onClose();
    }

    public void setCanClose(boolean canClose) {
        this.canClose = canClose;
    }

    @Override
    public boolean canPlayerClose() {
        return canClose;
    }

    public void createTimer(TimerTask task, long delay) {
        if(timer == null) timer = new Timer();

        timer.schedule(task, delay);
    }

    public void createTimer(TimerTask task, long delay, long period) {
        if(timer == null) timer = new Timer();

        timer.schedule(task, delay, period);
    }

    public void cancelTimer() {
        timer.cancel();
    }
}
