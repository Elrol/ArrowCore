package dev.elrol.arrow.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.arrow.api.data.IServerData;

public class ServerDataCore implements IServerData {

    public static final Codec<ServerDataCore> CODEC;

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.fieldOf("placeholder").forGetter((data) -> data.placeholder)
        ).apply(instance, (placeholder) -> {
            ServerDataCore data = new ServerDataCore();
            data.placeholder = placeholder;
            return data;
        }));
    }

    public boolean placeholder = true;

    public String getDataID() {
        return "core";
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IServerData> Codec<T> getCodec() {
        return (Codec<T>) CODEC;
    }

}
