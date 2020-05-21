package com.williambl.legacybrigadier.server.api.permission;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.Player;
import net.minecraft.server.command.CommandSource;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Environment(EnvType.SERVER)
public final class PermissionManager {

    /**
     * Get the permission nodes for a given player.
     * @param player the player to check.
     * @return a list of the player's permission nodes.
     */
    @Nonnull
    public static Set<PermissionNode> getNodesForPlayer(Player player) {
        return getNodesForName(player.name);
    }

    /**
     * Get the permission nodes for a command source.
     * @param source the source to check.
     * @return a list of the source's permission nodes.
     */
    @Nonnull
    public static Set<PermissionNode> getNodesForCommandSource(CommandSource source) {
        return getNodesForName(source.getName());
    }

    /**
     * Get the permission nodes for a (player) name.
     * @param name the name to check.
     * @return a list of the permission nodes.
     */
    @Nonnull
    public static Set<PermissionNode> getNodesForName(String name) {
        final Set<PermissionNode> nodes = permissionsMap.get(name);
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
    public static boolean addNodeToPlayer(Player player, PermissionNode node) {
        return addNodeToName(player.name, node);
    }

    /**
     * Add a node associated with the command source given.
     * @param source the command source which will be given a new node.
     * @param node the node to add.
     * @return whether the node was successfully added.
     */
    public static boolean addNodeToCommandSource(CommandSource source, PermissionNode node) {
        return addNodeToName(source.getName(), node);
    }

    /**
     * Add a node associated with the name given.
     * @param name the name which will be given a new node.
     * @param node the node to add.
     * @return whether the node was successfully added.
     */
    public static boolean addNodeToName(String name, PermissionNode node) {
        final Set<PermissionNode> nodes = permissionsMap.get(name);
        if (nodes == null) {
            final Set<PermissionNode> newNodes = new HashSet<>();
            newNodes.add(node);
            permissionsMap.put(name, newNodes);
        } else {
            nodes.add(node);
        }
        return tryUpdatePermissionsFile();
    }

    private static final Map<String, Set<PermissionNode>> permissionsMap = new HashMap<>();
    private static final Gson GSON = new GsonBuilder().create();
    private static final String permissionsFilePath = "config/legacybrigadier/permissions.json";
    private static final TypeToken<Map<String, Set<String>>> string2StringSetType = new TypeToken<Map<String, Set<String>>>() {};
    static {
        setupPermissionManager();
    }

    private static void setupPermissionManager() {
        final File permissionsFile = new File(permissionsFilePath);
        try {
            loadPermissions(permissionsFile);
        } catch (FileNotFoundException e) {
            try {
                if (permissionsFile.createNewFile()) {
                    System.out.println("Created perms file.");
                } else {
                    System.out.println("Couldn't create perms file!");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static boolean tryUpdatePermissionsFile() {
        final File permissionsFile = new File(permissionsFilePath);
        try {
            savePermissions(permissionsFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void loadPermissions(File file) throws FileNotFoundException {
        final Map<String, Set<String>> stringMap = GSON.fromJson(new FileReader(file), string2StringSetType.getType());
        PermissionManager.permissionsMap.clear();
        stringMap.forEach((names, perms) ->
                PermissionManager.permissionsMap.put(names, perms.stream().map(PermissionNode::new).collect(Collectors.toSet()))
        );
    }

    private static void savePermissions(File file) throws IOException {
        final Map<String, Set<String>> stringMap = new HashMap<>();
        PermissionManager.permissionsMap.forEach((name, perms) ->
                stringMap.put(name, perms.stream().map(PermissionNode::toString).collect(Collectors.toSet()))
        );
        final FileWriter writer = new FileWriter(file);
        writer.write(GSON.toJson(stringMap, string2StringSetType.getType()));
    }

}
