package com.williambl.legacybrigadier.impl.server.sender;

import com.williambl.legacybrigadier.api.command.ExtendedSender;
import com.williambl.legacybrigadier.api.permission.PermissionManager;
import com.williambl.legacybrigadier.api.permission.PermissionNode;
import com.williambl.legacybrigadier.impl.mixin.ServerCommandSenderAccessor;
import com.williambl.legacybrigadier.impl.mixin.ServerGUIAccessor;
import com.williambl.legacybrigadier.impl.server.mixinhooks.ServerPlayPacketHandlerHooks;
import io.github.minecraftcursedlegacy.impl.command.ServerCommandSender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.Player;
import net.minecraft.level.Level;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.gui.ServerGUI;
import net.minecraft.server.network.ServerPlayPacketHandler;
import net.minecraft.util.Vec3i;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

@Environment(EnvType.SERVER)
public class WrappedServerCommandSender implements ExtendedSender {
    private final ServerCommandSender wrapped;

    public WrappedServerCommandSender(ServerCommandSender from) {
        this.wrapped = from;
    }

    @Override
    public String getName() {
        return wrapped.getName();
    }

    @Override
    public Level getWorld() {
        if (this.wrapped.getPlayer() != null) {
            return this.wrapped.getPlayer().level;
        }
        return null;
    }

    @Override
    public Vec3i getPosition() {
        Player player = this.wrapped.getPlayer();
        if (player != null) {
            return new Vec3i((int)player.x, (int)player.y, (int)player.z);
        }

        return new Vec3i(0, 0, 0);
    }

    @Override
    public MinecraftServer getServer() {
        CommandSource source = ((ServerCommandSenderAccessor)this.wrapped).getSource();
        if (source instanceof ServerPlayPacketHandler)
            return ((ServerPlayPacketHandlerHooks)source).getServer();
        if (source instanceof MinecraftServer)
            return (MinecraftServer) source;
        if (source instanceof ServerGUI)
            return ((ServerGUIAccessor)source).getServer();
        return null;
    }

    @Override
    public Set<PermissionNode> getPermissions() {
        CommandSource source = ((ServerCommandSenderAccessor)this.wrapped).getSource();
        if (source instanceof MinecraftServer || source instanceof ServerGUI)
            return getAllPermissions();
        return PermissionManager.getNodesForCommandSource(this);
    }

    @Override
    public Set<PermissionNode> getAllPermissions() {
        Set<PermissionNode> set = new HashSet<>();
        set.add(PermissionNode.ROOT);
        return set;
    }

    @Override
    public boolean satisfiesNode(PermissionNode nodeToCheck) {
        return nodeToCheck.isSatisfiedBy(getPermissions());
    }

    @Nullable
    @Override
    public Player getPlayer() {
        return this.wrapped.getPlayer();
    }

    @Override
    public void sendCommandFeedback(String s) {
        this.wrapped.sendCommandFeedback(s);
    }
}
