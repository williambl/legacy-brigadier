package com.williambl.legacybrigadier.server.api.argument;

import com.williambl.legacybrigadier.server.mixinhooks.CommandSourceHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_39;
import net.minecraft.entity.player.Player;

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

    public List<String> getPlayers(class_39 commandSource) {
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
                result.add(commandSource.method_1410());
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
