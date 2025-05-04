package dev.elrol.arrow.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.elrol.arrow.api.commands.ICommandBase;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class _CommandBase implements ICommandBase {

    protected LiteralCommandNode<ServerCommandSource> root;

    @Override
    public abstract void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment);

    @Override
    public ServerPlayerEntity getPlayer(CommandContext<ServerCommandSource> context) {
        return context.getSource().getPlayer();
    }

    @Override
    public <T> RequiredArgumentBuilder<ServerCommandSource, T> argument(String name, ArgumentType<T> type) {
        return CommandManager.argument(name, type);
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> literal(String name) {
        return CommandManager.literal(name);
    }

    @Override
    public void redirect(CommandDispatcher<ServerCommandSource> dispatcher, String command) {
        dispatcher.register(literal(command).redirect(getRoot()));
    }

    @Override
    public LiteralCommandNode<ServerCommandSource> getRoot() {
        return root;
    }
}
