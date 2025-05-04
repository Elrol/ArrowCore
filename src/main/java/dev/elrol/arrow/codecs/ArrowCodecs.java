package dev.elrol.arrow.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ArrowCodecs {

    public static final Codec<byte[]> BYTE_ARRAY_CODEC = Codec.BYTE_BUFFER.xmap(
            ByteBuffer::array,
            ByteBuffer::wrap
    );

    public static final Codec<BigInteger> BIG_INT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("signum").forGetter(BigInteger::signum),
                    BYTE_ARRAY_CODEC.fieldOf("magnitude").forGetter(BigInteger::toByteArray)
            ).apply(instance, BigInteger::new)
    );

    public static final Codec<BigDecimal> BIG_DECIMAL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    BIG_INT_CODEC.fieldOf("unscaledValue").forGetter(BigDecimal::unscaledValue),
                    Codec.INT.fieldOf("scale").forGetter(BigDecimal::scale)
            ).apply(instance, BigDecimal::new)
    );

    public static final Codec<LocalDate> DATE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("year").forGetter(LocalDate::getYear),
            Codec.INT.fieldOf("month").forGetter(LocalDate::getMonthValue),
            Codec.INT.fieldOf("day").forGetter(LocalDate::getDayOfMonth)
    ).apply(instance, LocalDate::of));

    public static final Codec<LocalTime> TIME_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("hour").forGetter(LocalTime::getHour),
            Codec.INT.fieldOf("minute").forGetter(LocalTime::getMinute),
            Codec.INT.fieldOf("second").forGetter(LocalTime::getSecond),
            Codec.INT.fieldOf("nano").forGetter(LocalTime::getNano)
    ).apply(instance, LocalTime::of));

    public static final Codec<LocalDateTime> DATE_TIME_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ArrowCodecs.DATE_CODEC.fieldOf("date").forGetter(LocalDateTime::toLocalDate),
        ArrowCodecs.TIME_CODEC.fieldOf("time").forGetter(LocalDateTime::toLocalTime)
    ).apply(instance, LocalDateTime::of));

}
