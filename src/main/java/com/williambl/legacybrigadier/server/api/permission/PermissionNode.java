package com.williambl.legacybrigadier.server.api.permission;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Objects;
import java.util.Set;

@Environment(EnvType.SERVER)
public class PermissionNode {

    private final String path;

    public PermissionNode(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return path;
    }

    public boolean satisfies(PermissionNode nodeToCheck) {
        String[] pathElements = this.toString().split("\\.");
        String[] checkPathElements = nodeToCheck.toString().split("\\.");
        final int length = Math.max(pathElements.length, checkPathElements.length);

        for (int i = 0; i < length; i++) {
            final String node1 = i < pathElements.length ? pathElements[i] : "*";
            final String node2 = i < checkPathElements.length ? checkPathElements[i] : null;
            if (node2 == null || !nodeSatisfiesNode(node1, node2))
                return false;
        }
        return true;
    }

    public boolean isSatisfiedBy(Set<PermissionNode> nodesToCheck) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissionNode that = (PermissionNode) o;
        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
