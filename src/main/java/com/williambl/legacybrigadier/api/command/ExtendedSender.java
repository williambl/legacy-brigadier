package com.williambl.legacybrigadier.api.command;

import com.williambl.legacybrigadier.api.permission.PermissionNode;
import com.williambl.legacybrigadier.impl.server.sender.WrappedServerCommandSender;
import io.github.minecraftcursedlegacy.api.command.Sender;
import io.github.minecraftcursedlegacy.impl.command.ServerCommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.Player;
import net.minecraft.level.Level;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Vec3i;
import net.minecraft.util.maths.Vec3d;
import org.lwjgl.util.vector.Vector2f;

import javax.annotation.Nullable;
import java.util.Set;

public interface ExtendedSender extends Sender {
    String getName();

    Level getWorld();

    Vec3d getPosition();

    Vector2f getRotation();

    MinecraftServer getServer();

    Set<PermissionNode> getPermissions();

    Set<PermissionNode> getAllPermissions();

    boolean satisfiesNode(PermissionNode nodeToCheck);

    Entity getEntity();

    static @Nullable ExtendedSender extend(Sender sender) {
        return sender instanceof ExtendedSender ? (ExtendedSender) sender : (sender instanceof ServerCommandSender ? new WrappedServerCommandSender((ServerCommandSender) sender) : null);
    }
}
