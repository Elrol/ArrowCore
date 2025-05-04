package dev.elrol.arrow.api.data;

import com.mojang.serialization.Codec;

public interface IServerData {

    String getDataID();
    <T extends IServerData> Codec<T> getCodec();

}
