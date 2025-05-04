package dev.elrol.arrow.api.data;

import com.mojang.serialization.Codec;

public interface IPlayerData {

    String getDataID();

    <T extends IPlayerData> Codec<T> getCodec();

}
