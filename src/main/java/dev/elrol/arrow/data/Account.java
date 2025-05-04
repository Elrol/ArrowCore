package dev.elrol.arrow.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.arrow.codecs.ArrowCodecs;

import java.math.BigDecimal;

public class Account {
    public static final Codec<Account> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ArrowCodecs.BIG_DECIMAL_CODEC.fieldOf("balances").forGetter(data -> data.balance)
    ).apply(instance, Account::new));

    private BigDecimal balance;

    public Account(BigDecimal startingBal) {
        this.balance = startingBal;
    }

    public void setBalance(BigDecimal amount) {
        this.balance = amount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        balance = balance.subtract(amount);
    }
}
