package dev.elrol.arrow.api.registries;

import dev.elrol.arrow.data.Account;
import dev.elrol.arrow.data.Currency;
import dev.elrol.arrow.registries.ModEconomyRegistry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IEconomyRegistry {

    @Nullable Currency getPrimary();
    @Nullable Currency getCurrency(String id);
    @NotNull Map<String, Currency> getCurrencies();
    void addCurrency(Currency currency);

    void changeAccount(UUID uuid, Currency currency, ModEconomyRegistry.AccountFunction function);

    Account getAccount(UUID uuid);
    Account getAccount(UUID uuid, Currency currency);

    Map<String, Account> getAccounts(UUID uuid);

    BigDecimal getBal(UUID uuid);
    BigDecimal getBal(UUID uuid, Currency currency);

    boolean canAfford(UUID uuid, BigDecimal amount);
    boolean canAfford(UUID uuid, BigDecimal amount, Currency currency);

    void set(UUID uuid, BigDecimal amount);
    void set(UUID uuid, BigDecimal amount, Currency currency);
    void set(ServerPlayerEntity player, BigDecimal amount);
    void set(ServerPlayerEntity player, BigDecimal amount, Currency currency);

    void withdraw(UUID uuid, BigDecimal amount);
    void withdraw(UUID uuid, BigDecimal amount, Currency currency);
    void withdraw(ServerPlayerEntity player, BigDecimal amount);
    void withdraw(ServerPlayerEntity player, BigDecimal amount, Currency currency);

    void deposit(UUID uuid, BigDecimal amount);
    void deposit(UUID uuid, BigDecimal amount, Currency currency);
    void deposit(ServerPlayerEntity player, BigDecimal amount);
    void deposit(ServerPlayerEntity player, BigDecimal amount, Currency currency);

    MutableText getAmount(Number amount);
    MutableText getAmount(Number amount, Currency currency);
    String formatAmount(Number amount);
    String formatAmount(Number amount, Currency currency);

    List<UUID> getTopBalances(Currency currency);
}
