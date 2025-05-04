package dev.elrol.arrow.registries;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.elrol.arrow.ArrowCore;
import dev.elrol.arrow.api.registries.IEconomyRegistry;
import dev.elrol.arrow.api.registries.IPlayerDataRegistry;
import dev.elrol.arrow.data.Account;
import dev.elrol.arrow.data.Currency;
import dev.elrol.arrow.data.PlayerData;
import dev.elrol.arrow.data.PlayerDataCore;
import dev.elrol.arrow.libs.Constants;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

public class ModEconomyRegistry implements IEconomyRegistry {
    private static final File configDir = new File(FabricLoader.getInstance().getConfigDir().toFile(), "/Arrow/Economy/");

    Currency primary;
    Map<String, Currency> currencyMap = new HashMap<>();

    public ModEconomyRegistry() {
        load();
    }

    @Override
    public @Nullable Currency getPrimary() {
        return primary;
    }

    @Override
    public @Nullable Currency getCurrency(String id) {
        return currencyMap.get(id);
    }

    @Override
    public @NotNull Map<String, Currency> getCurrencies() {
        return currencyMap;
    }

    @Override
    public void addCurrency(Currency currency) {
        if(currency.isPrimary() && primary != null) {
            if(primary.isPrimary()) {
                ArrowCore.LOGGER.error("Conflicting primary currencies: {} - {}", primary.getID(), currency.getID());
            } else {
                primary = currency;
            }
        }

        if(primary == null)
            primary = currency;

        currencyMap.put(currency.getID(), currency);
        save();
    }

    @Override
    public void changeAccount(UUID uuid, Currency currency, AccountFunction function) {
        if(currency == null) currency = primary;
        PlayerData data = ArrowCore.INSTANCE.getPlayerDataRegistry().getPlayerData(uuid);
        PlayerDataCore coreData = data.get(new PlayerDataCore());
        Account account = function.change(coreData.getAccount(currency));
        coreData.putAccount(currency, account);
        data.put(coreData);
    }

    @Override
    public Account getAccount(UUID uuid) {
        return getAccount(uuid, primary);
    }

    @Override
    public Account getAccount(UUID uuid, Currency currency) {
        if(currency == null) currency = primary;
        PlayerData data = ArrowCore.INSTANCE.getPlayerDataRegistry().getPlayerData(uuid);
        PlayerDataCore coreData = data.get(new PlayerDataCore());
        Account account = coreData.getAccount(currency);
        data.put(coreData);
        return account;
    }

    @Override
    public Map<String, Account> getAccounts(UUID uuid) {
        Map<String, Account> accounts = new HashMap<>();
        currencyMap.forEach((id, currency) -> accounts.put(id, getAccount(uuid, currency)));
        return accounts;
    }

    @Override
    public BigDecimal getBal(UUID uuid) {
        return getBal(uuid, primary);
    }

    @Override
    public BigDecimal getBal(UUID uuid, Currency currency) {
        if(currency == null) currency = primary;
        return getAccount(uuid, currency).getBalance();
    }

    @Override
    public boolean canAfford(UUID uuid, BigDecimal amount) {
        return canAfford(uuid, amount, primary);
    }

    @Override
    public boolean canAfford(UUID uuid, BigDecimal amount, Currency currency) {
        if(currency == null) currency = primary;
        return getBal(uuid, currency).compareTo(amount) >= 0;
    }

    @Override
    public void set(UUID uuid, BigDecimal amount) {
        set(uuid, amount, primary);
    }

    @Override
    public void set(UUID uuid, BigDecimal amount, Currency currency) {
        if(currency == null) currency = primary;
        changeAccount(uuid, currency, (account) -> {
            account.setBalance(amount);
            return account;
        });
    }

    @Override
    public void set(ServerPlayerEntity player, BigDecimal amount) {
        set(player.getUuid(), amount, primary);
    }

    @Override
    public void set(ServerPlayerEntity player, BigDecimal amount, Currency currency) {
        set(player.getUuid(), amount, currency);
    }

    @Override
    public void withdraw(UUID uuid, BigDecimal amount) {
        withdraw(uuid, amount, primary);
    }

    @Override
    public void withdraw(UUID uuid, BigDecimal amount, Currency currency) {
        if(currency == null) currency = primary;
        changeAccount(uuid, currency, (account) -> {
            account.withdraw(amount);
            return account;
        });
    }

    @Override
    public void withdraw(ServerPlayerEntity player, BigDecimal amount) {
        withdraw(player.getUuid(), amount);
    }

    @Override
    public void withdraw(ServerPlayerEntity player, BigDecimal amount, Currency currency) {
        withdraw(player.getUuid(), amount, currency);
    }

    @Override
    public void deposit(UUID uuid, BigDecimal amount) {
        deposit(uuid, amount, primary);
    }

    @Override
    public void deposit(UUID uuid, BigDecimal amount, Currency currency) {
        if(currency == null) currency = primary;
        changeAccount(uuid, currency, (account -> {
            account.deposit(amount);
            return account;
        }));
    }

    @Override
    public void deposit(ServerPlayerEntity player, BigDecimal amount) {
        deposit(player.getUuid(), amount);
    }

    @Override
    public void deposit(ServerPlayerEntity player, BigDecimal amount, Currency currency) {
        deposit(player.getUuid(), amount, currency);
    }

    @Override
    public MutableText getAmount(Number amount) {
        return getAmount(amount, primary);
    }

    @Override
    public MutableText getAmount(Number amount, Currency currency) {
        if(currency == null) currency = primary;
        return Text.literal(formatAmount(amount, currency)).setStyle(Style.EMPTY.withItalic(false)).formatted(currency.getColor());
    }

    @Override
    public String formatAmount(Number amount) {
        return formatAmount(amount, primary);
    }

    @Override
    public String formatAmount(Number amount, Currency currency) {
        DecimalFormat format = new DecimalFormat("#,###");
        format.setMaximumFractionDigits(0);
        return currency.getSymbol() + format.format(amount);
    }

    @Override
    public List<UUID> getTopBalances(Currency currency) {
        Map<UUID, BigDecimal> map = new HashMap<>();
        List<UUID> uuids = new ArrayList<>();

        IPlayerDataRegistry playerDataRegistry = ArrowCore.INSTANCE.getPlayerDataRegistry();
        playerDataRegistry.getLoadedData().forEach((uuid, data) -> {
            PlayerDataCore coreData = data.get(new PlayerDataCore());
            BigDecimal bal = coreData.getAccount(currency).getBalance();

            for(int i = 0; i < 10; i++) {
                if(uuids.isEmpty() || uuids.size() <= i) {
                    uuids.add(uuid);
                    map.put(uuid, bal);
                    if(ArrowCore.CONFIG.isDebug) {
                        Text text = coreData.username;
                        String name = text == null ? uuid.toString() : text.getString();
                        ArrowCore.LOGGER.warn("Adding {} to empty slot with a balance of {}", name, bal.toPlainString());
                    }
                    break;
                } else {
                    UUID otherUUID = uuids.get(i);
                    BigDecimal otherBal = map.get(otherUUID);
                    if(otherBal.compareTo(bal) < 0) {
                        uuids.add(i, uuid);
                        map.put(uuid, bal);

                        if(ArrowCore.CONFIG.isDebug) {
                            ArrowCore.LOGGER.warn("{} is less than {}", otherBal.toPlainString(), bal.toPlainString());
                        }

                        while(!uuids.isEmpty() && uuids.size() >= 11) {
                            map.remove(uuids.getLast());
                            uuids.removeLast();
                        }
                        break;
                    } else {
                        if(ArrowCore.CONFIG.isDebug) {
                            ArrowCore.LOGGER.warn("{} is greater than or equal to {}", otherBal.toPlainString(), bal.toPlainString());
                        }
                    }
                }
            }
        });

        return uuids;
    }

    private void load() {
        if(configDir.mkdirs())
            ArrowCore.LOGGER.warn("Economy Config Folder Created");

        File[] files = configDir.listFiles();
        Gson gson = Constants.makeGSON();

        if(files != null) {
            for (File file : files) {
                try(FileReader reader = new FileReader(file)) {
                    JsonElement json = gson.fromJson(reader, JsonElement.class);
                    DataResult<Pair<Currency, JsonElement>> result = Currency.CODEC.decode(JsonOps.INSTANCE, json);
                    if(result.isSuccess()) {
                        String id = file.getName().replace(".json", "");
                        Currency currency = result.getOrThrow().getFirst();
                        if(currency.getID().equals(id)) {
                            addCurrency(currency);
                        } else {
                            ArrowCore.LOGGER.error("Currency file [{}] has an ID that is different than the file name.", file.getName());
                        }
                    }
                } catch (IOException e) {
                    ArrowCore.LOGGER.error(e.getMessage());
                }
            }
            if(!currencyMap.isEmpty()) return;
        }
        addCurrency(new Currency(
                "pokedollar",
                "Pokédollar",
                "Pokédollars",
                "₽",
                Formatting.RED,
                new BigDecimal(100),
                true));
    }

    private void save() {
        currencyMap.forEach((id, currency) -> {
            File file = new File(configDir, id + ".json");

            try(FileWriter writer = new FileWriter(file)) {
                DataResult<JsonElement> json = Currency.CODEC.encodeStart(JsonOps.INSTANCE, currency);
                if(json.isSuccess()) {
                    Constants.makeGSON().toJson(json.getOrThrow(), writer);
                }
            } catch (IOException e) {
                ArrowCore.LOGGER.error(e.getMessage());
            }
        });
    }

    @FunctionalInterface
    public interface AccountFunction {
        Account change(Account account);
    }
}
