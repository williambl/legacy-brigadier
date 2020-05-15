package com.williambl.legacybrigadier.server.api.permission;

public class PermissionNode {

    private final String path;

    public PermissionNode(String path) {
        this.path = path;
    }

    String getPath() {
        return path;
    }

    public boolean matches(PermissionNode nodeToCheck) {
        String[] pathElements = getPath().split(".");
        String[] checkPathElements = nodeToCheck.getPath().split(".");

        for (int i = 0; i < checkPathElements.length; i++) {
            String node1 = i < pathElements.length ? pathElements[i] : "*";
            String node2 = checkPathElements[i];
            if (!nodesMatch(node1, node2))
                return false;
        }
        return true;
    }

    private static boolean nodesMatch(String node1, String node2) {
        if (node1.equals("*"))
            return true;
        return node1.equals(node2);
    }

    public static final PermissionNode ROOT = new PermissionNode("*");
    public static final PermissionNode OPERATOR = new PermissionNode("minecraft.operator");
}
