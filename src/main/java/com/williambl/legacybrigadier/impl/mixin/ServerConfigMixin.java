package com.williambl.legacybrigadier.impl.mixin;

import com.williambl.legacybrigadier.api.permission.PermissionManager;
import com.williambl.legacybrigadier.api.permission.PermissionNode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.player.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin(PlayerManager.class)
@Environment(EnvType.SERVER)
public class ServerConfigMixin {

    @SuppressWarnings("rawtypes")
    @Redirect(method = "isOperator", at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"))
    boolean isOperator(Set set, Object o) {
        if (o instanceof String) {
            return PermissionNode.OPERATOR.isSatisfiedBy(PermissionManager.getNodesForName((String) o));
        }
        return false;
    }

    @Redirect(method = "isOperator", at = @At(value = "INVOKE", target = "Ljava/lang/String;toLowerCase()Ljava/lang/String;"))
    String dontLowercase(String s) {
        return s;
    }
}
