package dev.elrol.arrow.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.arrow.codecs.ArrowCodecs;
import net.minecraft.util.Formatting;

import java.math.BigDecimal;

public class Currency {

    public static final Codec<Currency> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(data -> data.id),
            Codec.STRING.fieldOf("singular").forGetter(data -> data.singular),
            Codec.STRING.fieldOf("plural").forGetter(data -> data.plural),
            Codec.STRING.fieldOf("symbol").forGetter(data -> data.symbol),
            Formatting.CODEC.fieldOf("color").forGetter(data -> data.color),
            ArrowCodecs.BIG_DECIMAL_CODEC.fieldOf("startBal").forGetter(data -> data.startBal),
            Codec.BOOL.fieldOf("primary").forGetter(data -> data.primary)
    ).apply(instance, Currency::new));

    private final String id;
    private final String singular;
    private final String plural;
    private final String symbol;
    private final Formatting color;
    private final BigDecimal startBal;
    private final boolean primary;

    public Currency(String id, String singular, String plural, String symbol, Formatting color, BigDecimal startBal, boolean isPrimary){
        this.id = id;
        this.singular = singular;
        this.plural = plural;
        this.symbol = symbol;
        this.color = color;
        this.startBal = startBal;
        this.primary = isPrimary;
    }

    public String getID() {
        return id;
    }

    public String getSingular() {
        return singular;
    }

    public String getPlural() {
        return plural;
    }

    public String getSymbol() {
        return symbol;
    }

    public Formatting getColor() {
        return color;
    }

    public BigDecimal getStartBal() {
        return startBal;
    }

    public boolean isPrimary() {
        return primary;
    }
}
