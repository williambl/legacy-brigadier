package com.williambl.legacybrigadier.api.argument.playerselector;

import com.williambl.legacybrigadier.api.command.ExtendedSender;
import com.williambl.legacybrigadier.impl.server.mixinhooks.ServerPlayPacketHandlerHooks;
import com.williambl.legacybrigadier.impl.server.utils.UncheckedCaster;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.Player;
import net.minecraft.level.Level;
import net.minecraft.server.network.ServerPlayPacketHandler;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.SERVER)
public class PlayerSelector {
    private final String selectorString;
    private final PlayerSelectorType type;

    PlayerSelector(String selectorString) {
        this.selectorString = selectorString;
        switch (selectorString.toLowerCase()) {
            case "@a":
                type = PlayerSelectorType.A;
                break;
            case "@p":
                type = PlayerSelectorType.P;
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
    public List<String> getPlayerNames(ExtendedSender commandSource) {
        List<String> result = new ArrayList<>();
        switch (type) {
            case RAW:
                result.add(selectorString);
                break;
            case A:
                Level level = (commandSource).getWorld();
                if (level != null) {
                    List<Player> players = UncheckedCaster.list(level.players);
                    players.forEach(it -> result.add(it.name));
                } else {
                    List<Player> players = UncheckedCaster.list((commandSource).getServer().playerManager.players);
                    players.forEach(it -> result.add(it.name));
                }
                break;
            case P:
                result.add(commandSource.getName());
                break;
        }
        return result;
    }

    /**
     * Get all players in the same level as the given {@link ExtendedSender}. If the Sender is levelless, all
     * players in the server are retrieved.
     * @param sender the {@link ExtendedSender} whose level will be used.
     * @return the list of players.
     */
    public List<Player> getPlayers(ExtendedSender sender) {
        List<Player> result = new ArrayList<>();
        Level level = sender.getWorld();
        List<Player> players;
        if (level != null) {
            players = UncheckedCaster.list(level.players);
        } else {
            players = UncheckedCaster.list((sender).getServer().playerManager.players);
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
        }
        return result;
    }

    private enum PlayerSelectorType {
        RAW,
        A,
        P
    }
}
