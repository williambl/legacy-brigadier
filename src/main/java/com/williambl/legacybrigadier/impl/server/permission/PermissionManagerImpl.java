package com.williambl.legacybrigadier.impl.server.permission;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.williambl.legacybrigadier.api.permission.PermissionNode;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class PermissionManagerImpl {
    private static final Map<String, Set<PermissionNode>> permissionsMap = new HashMap<>();
    private static final Gson GSON = new GsonBuilder().create();
    private static final String permissionsFilePath = "config/legacybrigadier/permissions.json";
    private static final TypeToken<Map<String, Set<String>>> string2StringSetType = new TypeToken<Map<String, Set<String>>>() {};
    static {
        setupPermissionManager();
    }

    public static Map<String, Set<PermissionNode>> getPermissionsMap() {
        return permissionsMap;
    }

    private static void setupPermissionManager() {
        final File permissionsFile = new File(permissionsFilePath);
        try {
            loadPermissions(permissionsFile);
        } catch (FileNotFoundException e) {
            try {
                final FileWriter writer = new FileWriter(permissionsFile);
                writer.write("{}");
                writer.close();
                System.out.println("Created perms file.");
            } catch (IOException ex) {
                System.out.println("Couldn't create perms file!");
                ex.printStackTrace();
            }
        }
    }

    public static boolean tryUpdatePermissionsFile() {
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
        permissionsMap.clear();
        stringMap.forEach((names, perms) ->
                permissionsMap.put(names, perms.stream().map(PermissionNode::new).collect(Collectors.toSet()))
        );
    }

    private static void savePermissions(File file) throws IOException {
        final Map<String, Set<String>> stringMap = new HashMap<>();
        permissionsMap.forEach((name, perms) ->
                stringMap.put(name, perms.stream().map(PermissionNode::toString).collect(Collectors.toSet()))
        );
        final FileWriter writer = new FileWriter(file);
        writer.write(GSON.toJson(stringMap, string2StringSetType.getType()));
        writer.close();
    }
}
