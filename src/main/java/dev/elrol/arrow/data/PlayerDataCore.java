package dev.elrol.arrow.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.arrow.api.data.IPlayerData;
import dev.elrol.arrow.registries.PlayerDataTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.util.*;

public class PlayerDataCore implements IPlayerData {

    public static final MapCodec<PlayerDataCore> CODEC;
    public static final String DATA_ID = "core";

    static {
        CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                TextCodecs.CODEC.optionalFieldOf("username", Text.empty()).forGetter(data -> data.username),
                Codec.STRING.listOf().optionalFieldOf("menuHistory", List.of()).forGetter(data -> data.menuHistory),
                ExactLocation.CODEC.listOf().optionalFieldOf("teleportHistory", List.of()).forGetter(core -> core.teleportHistory),
                Codec.unboundedMap(Codec.STRING, Account.CODEC).optionalFieldOf("account", Map.of()).forGetter(data -> data.accounts)
        ).apply(instance, (username, menuHistory, teleportHistory, account) -> {
            PlayerDataCore data = new PlayerDataCore();
            data.username = username;
            data.menuHistory = new ArrayList<>(menuHistory);
            data.teleportHistory = new ArrayList<>(teleportHistory);
            data.accounts.putAll(account);
            return data;
        }));
    }

    public Text username;
    public List<String> menuHistory = new ArrayList<>();
    public List<ExactLocation> teleportHistory = new ArrayList<>();
    private final Map<String, Account> accounts = new HashMap<>();

    @Override
    public String getDataID() {
        return DATA_ID;
    }

    public void logTeleport(ServerPlayerEntity player) {
        logTeleport(ExactLocation.from(player));
    }

    public void logTeleport(ExactLocation pos) {
        teleportHistory.addFirst(pos);
        if(teleportHistory.size() > 10) {
            teleportHistory.removeLast();
        }
    }

    public void putAccount(Currency currency, Account account) {
        accounts.put(currency.getID(), account);
    }

    public Account getAccount(Currency currency) {
        Account account = new Account(currency.getStartBal());
        String id = currency.getID();

        if(accounts.containsKey(id)) {
            account = accounts.get(id);
        } else {
            accounts.put(id, account);
        }

        return account;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T extends IPlayerData> MapCodec<T> getCodec() {
        return (MapCodec<T>) CODEC;
    }

    @Override
    public PlayerDataType<?> getType() {
        return PlayerDataTypes.get(getDataID());
    }
}
