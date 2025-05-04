package dev.elrol.arrow.api.registries;

import dev.elrol.arrow.api.commands.ICommandBase;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public interface ICommandRegistry {

    void registerCommand(ICommandBase command);

}
