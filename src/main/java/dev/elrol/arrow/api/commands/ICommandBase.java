package dev.elrol.arrow.api.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public interface ICommandBase {

    LiteralCommandNode<ServerCommandSource> getRoot();

    void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment);

    ServerPlayerEntity getPlayer(CommandContext<ServerCommandSource> context);

    LiteralArgumentBuilder<ServerCommandSource> literal(String string);

    <T> RequiredArgumentBuilder<ServerCommandSource, T> argument(String name, ArgumentType<T> type);

    void redirect(CommandDispatcher<ServerCommandSource> dispatcher, String command);
}
