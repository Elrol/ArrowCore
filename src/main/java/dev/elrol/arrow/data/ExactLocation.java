package dev.elrol.arrow.data;

import com.google.gson.GsonBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.arrow.ArrowCore;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;

public class ExactLocation {
    public static final Codec<ExactLocation> CODEC;

    static {
        CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Vec3d.CODEC.fieldOf("position").forGetter(location -> location.position),
                        Codec.FLOAT.fieldOf("yaw").forGetter(location -> location.yaw),
                        Codec.FLOAT.fieldOf("pitch").forGetter(location -> location.pitch),
                        Codec.STRING.fieldOf("world").forGetter(location -> location.world)                        )
                        .apply(instance, (position, yaw, pitch, world) -> new ExactLocation(world, position, yaw, pitch))
        );
    }

    Vec3d position;
    float yaw;
    float pitch;
    String world;

    public ExactLocation() {
        world = ServerWorld.OVERWORLD.getValue().toString();
        position = new Vec3d(0,0,0);
        yaw = 0;
        pitch = 0;
    }

    public ExactLocation(ServerWorld world, Vec3d pos, float yaw, float pitch){
        this.world = world.getRegistryKey().getValue().toString();
        this.position = pos;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public ExactLocation(ServerWorld world, double x, double y, double z, float yaw, float pitch) {
        this.world = world.getRegistryKey().getValue().toString();
        this.position = new Vec3d(x,y,z);
        this.yaw = yaw;
        this.pitch = pitch;
    }
    public ExactLocation(String world, Vec3d position, float yaw, float pitch) {
        this.world = world;
        this.position = position;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public double getX() {
        return position.x;
    }
    public double getY() {
        return position.y;
    }
    public double getZ() {
        return position.z;
    }
    public double getYaw() {
        return yaw;
    }
    public double getPitch() {
        return pitch;
    }

    public Vec3d getPosition() {
        return position;
    }

    public static ExactLocation from(ServerPlayerEntity player) {
        return new ExactLocation(
                player.getServerWorld(),
                player.getX(),
                player.getY(),
                player.getZ(),
                player.getHeadYaw(),
                player.getPitch());
    }

    public void teleport(ServerPlayerEntity player) {
        teleport(player, true);
    }

    public void teleport(ServerPlayerEntity player, boolean log){
        if(log) {
            PlayerData data = ArrowCore.INSTANCE.getPlayerDataRegistry().getPlayerData(player);
            PlayerDataCore coreData = data.get(new PlayerDataCore());
            coreData.logTeleport(player);
            data.put(coreData);
        }
        player.teleport(getWorld(player), getX(), getY(), getZ(), yaw, pitch);
    }

    public ServerWorld getWorld(ServerPlayerEntity player) {
        return Objects.requireNonNull(player.getServer()).getWorld(RegistryKey.of(RegistryKeys.WORLD,Identifier.of(world)));
    }

    @Override
    public String toString() {
        return new GsonBuilder().disableHtmlEscaping().create().toJson(this, ExactLocation.class);
    }
}
