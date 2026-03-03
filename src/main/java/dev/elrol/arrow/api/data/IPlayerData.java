package dev.elrol.arrow.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.elrol.arrow.data.PlayerDataType;

public interface IPlayerData {

    Codec<IPlayerData> PLAYER_DATA_CODEC = PlayerDataType.REGISTRY.getCodec().dispatch("type", IPlayerData::getType, PlayerDataType::codec);

    String getDataID();

    <T extends IPlayerData> MapCodec<T> getCodec();

    PlayerDataType<?> getType();

}
