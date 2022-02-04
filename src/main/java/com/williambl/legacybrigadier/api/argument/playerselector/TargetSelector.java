package com.williambl.legacybrigadier.api.argument.playerselector;

import com.williambl.legacybrigadier.api.command.ExtendedSender;
import com.williambl.legacybrigadier.impl.server.mixinhooks.ServerPlayPacketHandlerHooks;
import com.williambl.legacybrigadier.impl.server.utils.UncheckedCaster;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.Player;
import net.minecraft.level.Level;
import net.minecraft.server.network.ServerPlayPacketHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Environment(EnvType.SERVER)
public class TargetSelector {
    private final String selectorString;
    private final PlayerSelectorType type;

    TargetSelector(String selectorString) {
        this.selectorString = selectorString;
        switch (selectorString.toLowerCase()) {
            case "@a":
                type = PlayerSelectorType.A;
                break;
            case "@p":
                type = PlayerSelectorType.P;
                break;
            case "@e":
                type = PlayerSelectorType.E;
                break;
            default:
                type = PlayerSelectorType.RAW;
                break;
        }
    }

    /**
     * Get the raw selector string.
     * @return the selector.
     */
    public String getSelectorString() {
        return selectorString;
    }

    /**
     * Get the names of all players in the same level as the given {@link ExtendedSender}. If the Sender is levelless, all
     * players in the server are retrieved.
     * @param commandSource the {@link ExtendedSender} whose level will be used.
     * @return the list of player names.
     */
    public List<String> getEntityNames(ExtendedSender commandSource) {
        List<String> result = new ArrayList<>();
        switch (type) {
            case RAW:
                result.add(selectorString);
                break;
            case A: {
                Level level = (commandSource).getWorld();
                if (level != null) {
                    List<Player> players = UncheckedCaster.list(level.players);
                    players.forEach(it -> result.add(it.name));
                } else {
                    List<Player> players = UncheckedCaster.list((commandSource).getServer().playerManager.players);
                    players.forEach(it -> result.add(it.name));
                }
                break;
            }
            case P:
                result.add(commandSource.getName());
                break;
            case E: {
                Level level = commandSource.getWorld();
                List<Entity> entities;
                if (level != null) {
                    entities = UncheckedCaster.list(level.entities);
                } else {
                    entities = UncheckedCaster.list(Arrays.stream((commandSource).getServer().levels).flatMap(l -> UncheckedCaster.list(l.entities).stream()).collect(Collectors.toList()));
                }
                result.addAll(entities.stream().map(Object::toString).collect(Collectors.toList()));
            }
        }
        return result;
    }

    /**
     * Get all players in the same level as the given {@link ExtendedSender}. If the Sender is levelless, all
     * players in the server are retrieved.
     * @param sender the {@link ExtendedSender} whose level will be used.
     * @return the list of players.
     */
    public List<Entity> getEntities(ExtendedSender sender) {
        List<Entity> result = new ArrayList<>();
        Level level = sender.getWorld();
        List<Player> players;
        List<Entity> entities;
        if (level != null) {
            players = UncheckedCaster.list(level.players);
            entities = UncheckedCaster.list(level.entities);
        } else {
            players = UncheckedCaster.list((sender).getServer().playerManager.players);
            entities = UncheckedCaster.list(Arrays.stream((sender).getServer().levels).flatMap(l -> UncheckedCaster.list(l.entities).stream()).collect(Collectors.toList()));
        }
        switch (type) {
            case RAW:
                for (Player player : players) {
                    if (player.name.equals(selectorString))
                        result.add(player);
                }
                break;
            case A:
                result.addAll(players);
                break;
            case P:
                if (sender.getPlayer() != null)
                    result.add(sender.getPlayer());
                break;
            case E:
                result.addAll(entities);
                break;
        }
        return result;
    }

    private enum PlayerSelectorType {
        RAW,
        A,
        P,
        E
    }
}
