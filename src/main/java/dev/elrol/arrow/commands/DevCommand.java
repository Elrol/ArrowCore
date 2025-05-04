package dev.elrol.arrow.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.elrol.arrow.ArrowCore;
import dev.elrol.arrow.data.PlayerDataCore;
import dev.elrol.arrow.libs.PermUtils;
import dev.elrol.arrow.menus._MenuBase;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class DevCommand extends _CommandBase {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        root = dispatcher.register(literal("dev")
                .requires(source -> {
                    if(source.isExecutedByPlayer() && source.getPlayer() != null)
                        return PermUtils.hasPerm(source.getPlayer(), "arrow.command.admin", "dev").asBoolean();
                    return true;
                })
                .executes(this::noArgs));
    }

    private int noArgs(CommandContext<ServerCommandSource> context) {
        _MenuBase menu = ArrowCore.INSTANCE.getMenuRegistry().createMenu("dev", getPlayer(context));

        PlayerDataCore coreData = ArrowCore.INSTANCE.getPlayerDataRegistry().getPlayerData(context.getSource().getPlayer()).get(new PlayerDataCore());

        if(menu != null) menu.open();

        ArrowCore.LOGGER.warn("Menu History: {}", coreData.menuHistory.size());

        return 1;
    }
}
