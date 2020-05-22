package com.williambl.legacybrigadier.api.permission;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Set;

@Environment(EnvType.SERVER)
public class PermissionNode {

    private final String path;

    public PermissionNode(String path) {
        this.path = path;
    }


    /**
     * Check if this node satisfies another.
     * @param nodeToCheck the node to check.
     * @return whether this node satisfies the other node.
     */
    public boolean satisfies(@Nonnull PermissionNode nodeToCheck) {
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

    /**
     * Check if this node is satisfied by a {@link Set} of others.
     * @param nodesToCheck the set of nodes to check.
     * @return whether this node is satisfied by any in the set.
     */
    public boolean isSatisfiedBy(@Nonnull Set<PermissionNode> nodesToCheck) {
        for (PermissionNode nodeToCheck : nodesToCheck) {
            if (nodeToCheck.satisfies(this))
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return path;
    }

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

    private static boolean nodeSatisfiesNode(@Nonnull String node1, String node2) {
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
