package com.williambl.legacybrigadier.server.api.permission;

import com.williambl.legacybrigadier.server.mixinhooks.CommandSourceHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.command.CommandSource;

import java.util.function.Predicate;

@Environment(EnvType.SERVER)
public class RequiresPermission implements Predicate<CommandSource> {

    private final PermissionNode node;

    private RequiresPermission(PermissionNode node) {
        this.node = node;
    }

    /**
     * Create a predicate that requires the node path given.
     * @param nodePath the node path that must be satisfied by the {@link CommandSource}
     * @return the predicate.
     */
    public static RequiresPermission permission(String nodePath) {
        return new RequiresPermission(new PermissionNode(nodePath));
    }

    @Override
    public boolean test(CommandSource commandSource) {
        return ((CommandSourceHooks)commandSource).satisfiesNode(node);
    }
}
