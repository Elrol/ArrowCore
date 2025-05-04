package dev.elrol.arrow.registries;

import dev.elrol.arrow.ArrowCore;
import dev.elrol.arrow.api.commands.ICommandBase;
import dev.elrol.arrow.api.registries.ICommandRegistry;
import dev.elrol.arrow.commands.DevCommand;
import dev.elrol.arrow.commands.RefreshCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ModCommandRegistry implements ICommandRegistry {

    @Override
    public void registerCommand(ICommandBase command) {
        ArrowCore.LOGGER.info("Command Registered: {}", command);
        CommandRegistrationCallback.EVENT.register(command::register);
    }

}
