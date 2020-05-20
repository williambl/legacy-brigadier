package com.williambl.legacybrigadier.server.api.permission;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

@Environment(EnvType.SERVER)
public class PermissionNode {

    private final String path;

    public PermissionNode(String path) {
        this.path = path;
    }

    String getPath() {
        return path;
    }

    public boolean satisfies(PermissionNode nodeToCheck) {
        String[] pathElements = getPath().split("\\.");
        String[] checkPathElements = nodeToCheck.getPath().split("\\.");

        for (int i = 0; i < checkPathElements.length; i++) {
            String node1 = i < pathElements.length ? pathElements[i] : "*";
            String node2 = checkPathElements[i];
            if (!nodeSatisfiesNode(node1, node2))
                return false;
        }
        return true;
    }

    public boolean isSatisfiedBy(List<PermissionNode> nodesToCheck) {
        for (PermissionNode nodeToCheck : nodesToCheck) {
            if (nodeToCheck.satisfies(this))
                return true;
        }
        return false;
    }

    private static boolean nodeSatisfiesNode(String node1, String node2) {
        if (node1.equals("*"))
            return true;
        return node1.equals(node2);
    }

    /**
     * The root permission node. Satisfies all nodes, equivalent to having every permission.
     */
    public static final PermissionNode ROOT = new PermissionNode("*");
    /**
     * The operator node. Having this is equivalent to being in the ops file in vanilla mc.
     */
    public static final PermissionNode OPERATOR = new PermissionNode("minecraft.operator");
}
