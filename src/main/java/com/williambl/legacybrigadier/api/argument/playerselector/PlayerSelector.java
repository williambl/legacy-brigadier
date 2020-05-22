package com.williambl.legacybrigadier.api.argument.playerselector;

import com.williambl.legacybrigadier.impl.server.mixinhooks.CommandSourceHooks;
import com.williambl.legacybrigadier.impl.server.mixinhooks.ServerPlayerPacketHandlerHooks;
import com.williambl.legacybrigadier.impl.server.utils.UncheckedCaster;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.Player;
import net.minecraft.level.Level;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.network.ServerPlayerPacketHandler;

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
     * Get the names of all players in the same level as the given {@link CommandSource}. If the CommandSource is levelless, all
     * players in the server are retrieved.
     * @param commandSource the {@link CommandSource} whose level will be used.
     * @return the list of player names.
     */
    public List<String> getPlayerNames(CommandSource commandSource) {
        List<String> result = new ArrayList<>();
        switch (type) {
            case RAW:
                result.add(selectorString);
                break;
            case A:
                Level level = ((CommandSourceHooks)commandSource).getWorld();
                if (level != null) {
                    List<Player> players = UncheckedCaster.list(level.players);
                    players.forEach(it -> result.add(it.name));
                } else {
                    List<Player> players = UncheckedCaster.list(((CommandSourceHooks)commandSource).getServer().field_2842.field_578);
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
     * Get all players in the same level as the given {@link CommandSource}. If the CommandSource is levelless, all
     * players in the server are retrieved.
     * @param commandSource the {@link CommandSource} whose level will be used.
     * @return the list of players.
     */
    public List<Player> getPlayers(CommandSource commandSource) {
        List<Player> result = new ArrayList<>();
        Level level = ((CommandSourceHooks)commandSource).getWorld();
        List<Player> players;
        if (level != null) {
            players = UncheckedCaster.list(level.players);
        } else {
            players = UncheckedCaster.list(((CommandSourceHooks)commandSource).getServer().field_2842.field_578);
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
                if (commandSource instanceof ServerPlayerPacketHandler)
                    result.add(((ServerPlayerPacketHandlerHooks)commandSource).getPlayer());
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
