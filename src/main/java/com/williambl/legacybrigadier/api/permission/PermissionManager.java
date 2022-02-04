package com.williambl.legacybrigadier.api.permission;

import com.williambl.legacybrigadier.api.command.ExtendedSender;
import com.williambl.legacybrigadier.impl.server.permission.PermissionManagerImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.Player;
import net.minecraft.server.command.CommandSource;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Environment(EnvType.SERVER)
public final class PermissionManager {

    /**
     * Get the permission nodes for a given player.
     * @param player the player to check.
     * @return a list of the player's permission nodes.
     */
    @Nonnull
    public static Set<PermissionNode> getNodesForPlayer(@Nonnull Player player) {
        return getNodesForName(player.name);
    }

    /**
     * Get the permission nodes for a command source.
     * @param source the source to check.
     * @return a list of the source's permission nodes.
     */
    @Nonnull
    public static Set<PermissionNode> getNodesForCommandSource(@Nonnull ExtendedSender source) {
        return getNodesForName(source.getName());
    }

    /**
     * Get the permission nodes for a (player) name.
     * @param name the name to check.
     * @return a list of the permission nodes.
     */
    @Nonnull
    public static Set<PermissionNode> getNodesForName(@Nonnull String name) {
        final Set<PermissionNode> nodes = PermissionManagerImpl.getPermissionsMap().get(name);
        if (nodes == null)
            return Collections.emptySet();
        return nodes;
    }

    /**
     * Add a node associated with the player given.
     * @param player the player which will be given a new node.
     * @param node the node to add.
     * @return whether the node was successfully added.
     */
    public static boolean addNodeToPlayer(@Nonnull Player player, @Nonnull PermissionNode node) {
        return addNodeToName(player.name, node);
    }

    /**
     * Add a node associated with the command source given.
     * @param source the command source which will be given a new node.
     * @param node the node to add.
     * @return whether the node was successfully added.
     */
    public static boolean addNodeToCommandSource(@Nonnull CommandSource source, @Nonnull PermissionNode node) {
        return addNodeToName(source.getName(), node);
    }

    /**
     * Add a node associated with the name given.
     * @param name the name which will be given a new node.
     * @param node the node to add.
     * @return whether the node was successfully added.
     */
    public static boolean addNodeToName(@Nonnull String name, @Nonnull PermissionNode node) {
        final Set<PermissionNode> nodes = PermissionManagerImpl.getPermissionsMap().get(name);
        if (nodes == null) {
            final Set<PermissionNode> newNodes = new HashSet<>();
            newNodes.add(node);
            PermissionManagerImpl.getPermissionsMap().put(name, newNodes);
        } else {
            nodes.add(node);
        }
        return PermissionManagerImpl.tryUpdatePermissionsFile();
    }

    /**
     * Remove a node associated with the player given.
     * @param player the player which will lose the node.
     * @param node the node to remove.
     * @return whether the node was successfully removed.
     */
    public static boolean removeNodeFromPlayer(@Nonnull Player player, @Nonnull PermissionNode node) {
        return removeNodeFromName(player.name, node);
    }

    /**
     * Remove a node associated with the command source given.
     * @param source the command source which will lose the node.
     * @param node the node to remove.
     * @return whether the node was successfully removed.
     */
    public static boolean removeNodeFromCommandSource(@Nonnull CommandSource source, @Nonnull PermissionNode node) {
        return removeNodeFromName(source.getName(), node);
    }

    /**
     * Remove a node associated with the name given.
     * @param name the name which will lose the node.
     * @param node the node to remove.
     * @return whether the node was successfully removed.
     */
    public static boolean removeNodeFromName(@Nonnull String name, @Nonnull PermissionNode node) {
        final Set<PermissionNode> nodes = PermissionManagerImpl.getPermissionsMap().get(name);
        if (nodes == null) {
            final Set<PermissionNode> newNodes = new HashSet<>();
            PermissionManagerImpl.getPermissionsMap().put(name, newNodes);
        } else {
            nodes.remove(node);
        }
        return PermissionManagerImpl.tryUpdatePermissionsFile();
    }

}
