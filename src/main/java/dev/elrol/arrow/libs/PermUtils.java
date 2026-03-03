package dev.elrol.arrow.libs;

import dev.elrol.arrow.ArrowCore;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.platform.PlayerAdapter;
import net.luckperms.api.query.QueryOptions;
import net.luckperms.api.track.Track;
import net.luckperms.api.util.Tristate;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PermUtils {

    public static User getUser(ServerPlayerEntity player) {
        return getPlayerAdapter().getUser(player);
    }

    @Nullable
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
        return permData.checkPermission(permNode);
    }

    public static boolean hasTrack(ServerPlayerEntity player, String trackID) {
        Track track = getTrack(trackID);
        assert track != null;
        for(String groupID : track.getGroups()) {
            if(isInGroup(player, groupID)) return true;
        }
        return false;
    }

    public static int calcMetaInts(ServerPlayerEntity player, String name) {
        CachedMetaData meta = getMetaData(player);
        return meta.getMetaValue(name, Integer::parseInt).orElse(0) +
                meta.getMetaValue("premium_" + name, Integer::parseInt).orElse(0) +
                meta.getMetaValue("extra_" + name, Integer::parseInt).orElse(0) +
                meta.getMetaValue("staff_" + name, Integer::parseInt).orElse(0);
    }

    public static void changeMetaInt(ServerPlayerEntity player, String meta, int change) {
        CachedMetaData metaData = getMetaData(player);
        String metaValue = metaData.getMetaValue(meta);

        if(metaValue == null) metaValue = "0";

        int original = Integer.parseInt(metaValue);
        MetaNode node = MetaNode.builder(meta, String.valueOf(original + change)).build();

        getInstance().getUserManager().modifyUser(player.getUuid(), user -> {
            user.data().add(node);
        });
    }

    public static Optional<String> getHighestGroupInTrack(UUID uuid, String trackID) {
        Track track = getTrack(trackID);
        User user = getUser(uuid);

        if(track == null || user == null) return Optional.empty();

        List<String> trackGroups = track.getGroups();

        return user.getNodes()
                .stream()
                .filter(node -> node.getKey().startsWith("group."))
                .map(node -> ((InheritanceNode) node).getGroupName())
                .filter(trackGroups::contains)
                .max(Comparator.comparingInt(trackGroups::indexOf));

    }

    @Nullable
    public static Group getGroup(String groupID) {
        return getInstance().getGroupManager().getGroup(groupID);
    }

    @Nullable
    public static Track getTrack(String trackID) {
        return getInstance().getTrackManager().getTrack(trackID);
    }

    public static void save(User user) {
        getInstance().getUserManager().saveUser(user);
    }

    public static void save(UUID uuid) {
        save(getUser(uuid));
    }

    public static void save(ServerPlayerEntity player) {
        save(player.getUuid());
    }

}
