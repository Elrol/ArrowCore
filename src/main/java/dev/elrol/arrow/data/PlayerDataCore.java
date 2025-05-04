package dev.elrol.arrow.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.arrow.api.data.IPlayerData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.util.*;

public class PlayerDataCore implements IPlayerData {

public static final Codec<PlayerDataCore> CODEC;

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                TextCodecs.CODEC.fieldOf("username").forGetter(data -> {
                    if(data.username == null) return Text.empty();
                    return data.username;
                }),
                Codec.STRING.listOf().fieldOf("menuHistory").forGetter(data -> data.menuHistory),
                ExactLocation.CODEC.listOf().fieldOf("teleportHistory").forGetter(core -> core.teleportHistory),
                Codec.unboundedMap(Codec.STRING, Account.CODEC).fieldOf("account").forGetter(data -> data.accounts)
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
        return "core";
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
    public <T extends IPlayerData> Codec<T> getCodec() {
        return (Codec<T>) CODEC;
    }
}
