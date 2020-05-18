package com.williambl.legacybrigadier.server.api.argument;

import com.williambl.legacybrigadier.server.mixinhooks.CommandSourceHooks;
import com.williambl.legacybrigadier.server.mixinhooks.ServerPlayerPacketHandlerHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.Player;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.network.ServerPlayerPacketHandler;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.SERVER)
public class PlayerSelector {
    private final String id;
    private PlayerIdType type;

    public PlayerSelector(String id, PlayerIdType type) {
        this.id = id;
        this.type = type;
    }

    public PlayerSelector(String id) {
        this.id = id;
        switch (id.toLowerCase()) {
            case "@a":
                type = PlayerIdType.A;
                break;
            case "@p":
                type = PlayerIdType.P;
                break;
            default:
                type = PlayerIdType.RAW;
                break;
        }
    }

    public String getId() {
        return id;
    }

    public PlayerIdType getType() {
        return type;
    }

    public List<String> getPlayerNames(CommandSource commandSource) {
        List<String> result = new ArrayList<>();
        switch (type) {
            case RAW:
                result.add(id);
                break;
            case A:
                List<Player> players = ((CommandSourceHooks)commandSource).getWorld().players;
                players.forEach(it -> result.add(it.name));
                break;
            case P:
                result.add(commandSource.getName());
                break;
        }
        return result;
    }

    public List<Player> getPlayers(CommandSource commandSource) {
        List<Player> result = new ArrayList<>();
        List<Player> players = ((CommandSourceHooks)commandSource).getWorld().players;
        switch (type) {
            case RAW:
                for (Player player : players) {
                    if (player.name.equals(id))
                        result.add(player);
                }
                break;
            case A:
                result.addAll(players);
                break;
            case P:
                if (commandSource instanceof ServerPlayerPacketHandler)
                    result.add(((ServerPlayerPacketHandlerHooks)commandSource).getPlayer());
                break;
        }
        return result;
    }

    public enum PlayerIdType {
        RAW,
        A,
        P
    }
}
