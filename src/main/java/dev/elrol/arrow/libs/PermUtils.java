package dev.elrol.arrow.libs;

import dev.elrol.arrow.ArrowCore;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.platform.PlayerAdapter;
import net.luckperms.api.query.QueryOptions;
import net.luckperms.api.util.Tristate;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class PermUtils {

    public static User getUser(ServerPlayerEntity player) {
        return getPlayerAdapter().getUser(player);
    }

    public static User getUser(UUID uuid) {
        return getInstance().getUserManager().getUser(uuid);
    }

    public static LuckPerms getInstance() {
        return LuckPermsProvider.get();
    }

    public static PlayerAdapter<ServerPlayerEntity> getPlayerAdapter() {
        return getInstance().getPlayerAdapter(ServerPlayerEntity.class);
    }

    public static CachedMetaData getMetaData(ServerPlayerEntity player) {
        return getPlayerAdapter().getMetaData(player);
    }

    public static boolean isInGroup(ServerPlayerEntity player, String group) {
        //return Permissions.check(player, "group." + group);
        return hasPerm(player,"group",group).asBoolean();
    }

    public static Tristate hasPerm(ServerPlayerEntity player, String perm, String node) {
        CachedPermissionData permData = getUser(player).getCachedData().getPermissionData(QueryOptions.nonContextual());
        String permNode = perm + "." + node;
        if(ArrowCore.CONFIG.isDebug) {
            ArrowCore.LOGGER.warn("PermNode check: {}", permNode);
        }
        Tristate state;

        if(permData.checkPermission(permNode).asBoolean()) {
            state = Tristate.TRUE;
        } else {
            // TODO get OP perm check fixed
            int lastIndex = perm.lastIndexOf(".");
            if(lastIndex == -1) {
                state = permData.
            } else {
                state = hasPerm(player, perm.substring(0, lastIndex), "*");
            }
        }

        if(hasPerm(player, perm, "*").asBoolean()) {
            state = Tristate.TRUE;
            if(ArrowCore.CONFIG.isDebug)
                ArrowCore.LOGGER.warn("Is Op");
        } else {
            state = ;
        }
        if(ArrowCore.CONFIG.isDebug)
            ArrowCore.LOGGER.warn("Value: {}", state);
        return state;
    }

}
