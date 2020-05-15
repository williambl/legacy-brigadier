package com.williambl.legacybrigadier.server.api.permission;

import com.williambl.legacybrigadier.server.mixinhooks.CommandSourceHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_39;

import java.util.function.Predicate;

@Environment(EnvType.SERVER)
public class RequiresPermission implements Predicate<class_39> {

    private final PermissionNode node;

    private RequiresPermission(PermissionNode node) {
        this.node = node;
    }

    public static RequiresPermission permission(String nodePath) {
        return new RequiresPermission(new PermissionNode(nodePath));
    }

    @Override
    public boolean test(class_39 commandSource) {
        return ((CommandSourceHooks)commandSource).satisfiesNode(node);
    }
}
