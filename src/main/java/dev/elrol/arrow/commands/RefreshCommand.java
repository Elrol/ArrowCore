package dev.elrol.arrow.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.elrol.arrow.ArrowCore;
import dev.elrol.arrow.api.events.RefreshCallback;
import dev.elrol.arrow.libs.ModTranslations;
import dev.elrol.arrow.libs.PermUtils;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.ActionResult;

public class RefreshCommand extends _CommandBase {

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        root = dispatcher.register(literal("refresh")
                .requires(source -> {
                    if(source.isExecutedByPlayer() && source.getPlayer() != null)
                        return PermUtils.hasPerm(source.getPlayer(), "arrow.command.admin", "refresh").asBoolean();
                    return true;
                })
                .executes(this::noArgs));
    }

    private int noArgs(CommandContext<ServerCommandSource> context) {
        //TODO reload data and configs
        ArrowCore.CONFIG.load();

        ActionResult result = RefreshCallback.EVENT.invoker().refresh();
        context.getSource().sendMessage(ModTranslations.msg("refreshed"));
        return result.equals(ActionResult.FAIL) ? 0 : 1;
    }

}
