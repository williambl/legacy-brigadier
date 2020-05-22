package com.williambl.legacybrigadier.api.predicate;

import com.williambl.legacybrigadier.api.permission.PermissionNode;
import com.williambl.legacybrigadier.impl.server.mixinhooks.CommandSourceHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.command.CommandSource;

import java.util.function.Predicate;

@Environment(EnvType.SERVER)
public class HasPermission implements Predicate<CommandSource> {

    private final PermissionNode node;

    private HasPermission(PermissionNode node) {
        this.node = node;
    }

    /**
     * Create a predicate that requires the node path given.
     * @param nodePath the node path that must be satisfied by the {@link CommandSource}
     * @return the predicate.
     */
    public static HasPermission permission(String nodePath) {
        return new HasPermission(new PermissionNode(nodePath));
    }

    @Override
    public boolean test(CommandSource commandSource) {
        return ((CommandSourceHooks)commandSource).satisfiesNode(node);
    }
}
