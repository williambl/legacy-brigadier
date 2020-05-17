package com.williambl.legacybrigadier.mixin;

import com.williambl.legacybrigadier.server.LegacyBrigadierServer;
import com.williambl.legacybrigadier.server.api.permission.PermissionNode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_166;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Set;

@Mixin(class_166.class)
@Environment(EnvType.SERVER)
public class ServerConfigMixin {

    @SuppressWarnings("rawtypes")
    @Redirect(method = "method_584(Ljava/lang/String;)Z", at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"))
    boolean isOperator(Set set, Object o) {
        if (o instanceof String) {
            List<PermissionNode> nodes = LegacyBrigadierServer.permissionsMap.get(o);
            if (nodes == null)
                return false;
            return PermissionNode.OPERATOR.isSatisfiedBy(nodes);
        }
        return false;
    }
}
