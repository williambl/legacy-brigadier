package com.williambl.legacybrigadier.server.api.permission;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.Player;
import net.minecraft.server.command.CommandSource;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Environment(EnvType.SERVER)
public final class PermissionManager {

    /**
     * Get the permission nodes for a given player.
     * @param player the player to check.
     * @return a list of the player's permission nodes.
     */
    @Nonnull
    public static List<PermissionNode> getNodesForPlayer(Player player) {
        final List<PermissionNode> nodes = permissionsMap.get(player.name);
        if (nodes == null)
            return Collections.emptyList();
        return nodes;
    }

    /**
     * Get the permission nodes for a command source.
     * @param source the source to check.
     * @return a list of the source's permission nodes.
     */
    @Nonnull
    public static List<PermissionNode> getNodesForCommandSource(CommandSource source) {
        final List<PermissionNode> nodes = permissionsMap.get(source.getName());
        if (nodes == null)
            return Collections.emptyList();
        return nodes;
    }

    /**
     * Get the permission nodes for a (player) name.
     * @param name the name to check.
     * @return a list of the permission nodes.
     */
    @Nonnull
    public static List<PermissionNode> getNodesForName(String name) {
        final List<PermissionNode> nodes = permissionsMap.get(name);
        if (nodes == null)
            return Collections.emptyList();
        return nodes;
    }

    private static final Map<String, List<PermissionNode>> permissionsMap = new HashMap<>();
    private static final Gson GSON = new GsonBuilder().create();
    private static final String permissionsFilePath = "config/legacybrigadier/permissions.json";
    static {
        setupPermissionManager();
    }

    private static void setupPermissionManager() {
        final File permissionsFile = new File(permissionsFilePath);
        try {
            final TypeToken<Map<String, List<String>>> typeToken = new TypeToken<Map<String, List<String>>>() {};
            final Map<String, List<String>> stringMap = GSON.fromJson(new FileReader(permissionsFile), typeToken.getType());

            stringMap.forEach((player, perms) ->
                    permissionsMap.put(player, perms.stream().map(PermissionNode::new).collect(Collectors.toList()))
            );
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

}
