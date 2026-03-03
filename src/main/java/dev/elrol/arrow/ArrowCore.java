package dev.elrol.arrow;

import de.tomalbrc.filament.api.FilamentLoader;
import dev.elrol.arrow.api.events.FirstJoinCallback;
import dev.elrol.arrow.api.events.MenuCloseCallback;
import dev.elrol.arrow.data.*;
import dev.elrol.arrow.api.IArrowAPI;
import dev.elrol.arrow.api.events.ArrowEvents;
import dev.elrol.arrow.api.registries.*;
import dev.elrol.arrow.commands.DevCommand;
import dev.elrol.arrow.commands.RefreshCommand;
import dev.elrol.arrow.config.ArrowConfig;
import dev.elrol.arrow.libs.ArrowCoreConstants;
import dev.elrol.arrow.libs.MenuUtils;
import dev.elrol.arrow.menus.DevMenu;
import dev.elrol.arrow.registries.CoreMenuItems;
import dev.elrol.arrow.registries.PlayerDataTypes;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.economy.EconomyService;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class ArrowCore implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger(ArrowCoreConstants.MODID);
    public static ArrowConfig CONFIG = new ArrowConfig();
    public static final IArrowAPI INSTANCE = new ArrowAPI();
    public static final CoreMenuItems MENU_ITEMS = new CoreMenuItems();

    private static MinecraftServer serverInstance = null;

    public static void registerMod(String modid) {
        FilamentLoader.loadModels(modid, modid);
        FilamentLoader.loadItems(modid);
        FilamentLoader.loadBlocks(modid);
        PolymerResourcePackUtils.addModAssets(modid);
    }

    static {
        registerMod(ArrowCoreConstants.MODID);
    }

    @Override
    public void onInitialize() {
        if(FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT)) return;
        CONFIG = CONFIG.load();

        PlayerDataTypes.register(PlayerDataCore.DATA_ID, PlayerDataCore.CODEC);

        registerCommands();
        registerEvents();

        DevMenu.buttonsToAdd.put("null", player -> MenuUtils.item(Items.BEDROCK, 1, "Fail").setCallback(() -> {
            player.sendMessage(Text.literal("You clicked bedrock"));
        }));

        ArrowEvents.CONFIG_LOADED_EVENT.invoker().loaded();

        PolymerResourcePackUtils.markAsRequired();
    }

    private void registerMenus() {
        IMenuRegistry menuReg = INSTANCE.getMenuRegistry();
        menuReg.registerMenu("dev", DevMenu.class);
    }

    private void registerCommands() {
        ICommandRegistry commandRegistry = INSTANCE.getCommandRegistry();

        commandRegistry.registerCommand(new RefreshCommand());
        commandRegistry.registerCommand(new DevCommand());
    }

    private void registerEvents() {

        MenuCloseCallback.EVENT.register((menu -> ActionResult.PASS));

        ServerMessageEvents.CHAT_MESSAGE.register(((message, sender, params) -> {
            //sender.sendMessage(Text.literal("This is the chat message"));
        }));

        ServerMessageEvents.GAME_MESSAGE.register(((server, message, overlay) -> {
            //server.sendMessage(Text.literal("This is the game message"));
        }));

        IEventRegistry eventRegistry = INSTANCE.getEventRegistry();
        IEconomyRegistry economyRegistry = INSTANCE.getEconomyRegistry();

        eventRegistry.registerEvent(() -> ArrowEvents.PLAYER_DATA_LOADED_EVENT.register((player, data) -> {

        }));

        eventRegistry.registerEvent(() -> ArrowEvents.ALL_PLAYER_DATA_LOADED_EVENT.register(list -> {
            list.forEach(data -> {
                PlayerDataCore corePlayerData = data.get(new PlayerDataCore());
                data.put(corePlayerData);
            });
        }));

        ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
            serverInstance = server;
            eventRegistry.register();
        });

        eventRegistry.registerEvent(() -> ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            ArrowCoreConstants.SERVER_DATA_DIR = new File(ArrowCoreConstants.ARROW_DATA_DIR, "/server_data");
            ArrowCore.LOGGER.warn("Server Started: {}", ArrowCoreConstants.SERVER_DATA_DIR);
            if(ArrowCoreConstants.SERVER_DATA_DIR.mkdirs()) {
                ArrowCore.LOGGER.warn("Server Data Folder created: {}", ArrowCoreConstants.SERVER_DATA_DIR);
            } else {
                ArrowCore.LOGGER.info("Server Data Folder Found: {}", ArrowCoreConstants.SERVER_DATA_DIR);
            }

            MENU_ITEMS.register();
            registerMenus();

            INSTANCE.getPlayerDataRegistry().loadAll();
            IServerDataRegistry serverDataRegistry = INSTANCE.getServerDataRegistry();

            if(CONFIG.transferImpactorToArrowEcon) {
                EconomyService service = Impactor.instance().services().provide(EconomyService.class);

                service.currencies().registered().forEach(currency -> {
                    String ID = currency.key().value();

                    Currency arrowCurrency = economyRegistry.getCurrency(ID);

                    if(arrowCurrency == null) {
                        TextComponent textSingular = (TextComponent) currency.singular();
                        TextComponent textPlural = (TextComponent) currency.plural();
                        TextComponent textSymbol = (TextComponent) currency.symbol();

                        arrowCurrency = new Currency(
                                ID,
                                textSingular.content(),
                                textPlural.content(),
                                textSymbol.content(),
                                Formatting.GOLD,
                                currency.defaultAccountBalance(),
                                currency.primary()
                        );
                        economyRegistry.addCurrency(arrowCurrency);
                    }

                    try {
                        final Currency finalArrowCurrency = arrowCurrency;
                        service.accounts(currency).get().forEach(account -> {
                            UUID uuid = account.owner();
                            BigDecimal bal = economyRegistry.getBal(uuid, finalArrowCurrency);
                            if(bal.compareTo(currency.defaultAccountBalance()) == 0) {
                                economyRegistry.deposit(uuid, account.balance(), finalArrowCurrency);
                                LOGGER.warn("Deposited {} into a new account for {}", account.balance(), uuid);
                            }
                        });
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            serverDataRegistry.load();

            ServerDataCore serverDataCore = serverDataRegistry.get(new ServerDataCore());
            if(CONFIG.isDebug) LOGGER.warn("ServerDataCore loaded: {}", serverDataCore.placeholder);
        }));

        eventRegistry.registerEvent(() -> ServerLifecycleEvents.SERVER_STOPPING.register((server) -> {
            INSTANCE.getPlayerDataRegistry().saveAll();
            INSTANCE.getServerDataRegistry().save();
        }));

        eventRegistry.registerEvent(() -> ServerPlayConnectionEvents.JOIN.register((networkHandler, packetSender, server) -> {
            IPlayerDataRegistry registry = INSTANCE.getPlayerDataRegistry();
            ServerPlayerEntity player = networkHandler.player;

            if(!registry.hasPlayerData(player.getUuid())) {
                FirstJoinCallback.EVENT.invoker().welcome(player);
            }

            ArrowPlayerData data = registry.load(player);
            PlayerDataCore coreData = data.get(new PlayerDataCore());

            coreData.username = player.getName();
            data.put(coreData);

            economyRegistry.getTopBalances(economyRegistry.getPrimary()).forEach((uuid) -> {
                BigDecimal bal = economyRegistry.getBal(uuid);
                if(CONFIG.isDebug)
                    LOGGER.warn("{}'s balance is: {}", uuid.toString(), bal.toPlainString());
            });

            LOGGER.warn("Player Data Loaded");
            ArrowEvents.PLAYER_DATA_LOADED_EVENT.invoker().loaded(player, data);
        }));

        eventRegistry.registerEvent(() -> ServerPlayConnectionEvents.DISCONNECT.register((networkHandler, server) -> {
            ServerPlayerEntity player = networkHandler.player;
            IPlayerDataRegistry playerDataRegistry = INSTANCE.getPlayerDataRegistry();
            ArrowPlayerData data = playerDataRegistry.getPlayerData(player);
            PlayerDataCore coreData = data.get(new PlayerDataCore());

            coreData.teleportHistory.clear();
            data.put(coreData);

            ArrowEvents.PLAYER_DATA_UNLOADING_EVENT.invoker().unloading(player, data);

            playerDataRegistry.save(player.getUuid());
            if(CONFIG.isDebug) LOGGER.warn("{} has disconnected. Data was saved.", networkHandler.player.getName().getLiteralString());
        }));
    }

    public static MinecraftServer getServer() {
        return serverInstance;
    }

}
