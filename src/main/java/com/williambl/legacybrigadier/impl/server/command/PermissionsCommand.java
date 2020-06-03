package com.williambl.legacybrigadier.impl.server.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.williambl.legacybrigadier.api.argument.playerselector.PlayerSelector;
import com.williambl.legacybrigadier.api.command.CommandProvider;
import com.williambl.legacybrigadier.api.permission.PermissionManager;
import com.williambl.legacybrigadier.api.permission.PermissionNode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.command.CommandSource;

import java.util.Set;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.williambl.legacybrigadier.api.argument.playerselector.PlayerSelectorArgumentType.getPlayer;
import static com.williambl.legacybrigadier.api.argument.playerselector.PlayerSelectorArgumentType.player;
import static com.williambl.legacybrigadier.api.predicate.HasPermission.permission;

@Environment(EnvType.SERVER)
public class PermissionsCommand implements CommandProvider {

    @Override
    public LiteralArgumentBuilder<CommandSource> get() {
        return LiteralArgumentBuilder.<CommandSource>literal("permissions")
                .requires(permission("command.permissions"))
                .then(LiteralArgumentBuilder.<CommandSource>literal("get")
                        .then(RequiredArgumentBuilder.<CommandSource, PlayerSelector>argument("player", player())
                                .executes(context -> {
                                    final StringBuilder builder = new StringBuilder();
                                    for (String playerName : getPlayer(context, "player").getPlayerNames(context.getSource())) {
                                        final Set<PermissionNode> nodes = PermissionManager.getNodesForName(playerName);
                                        builder.append(playerName);
                                        builder.append(" has permissions:");
                                        for (PermissionNode node : nodes) {
                                            builder.append(" ");
                                            builder.append(node.toString());
                                        }
                                        builder.append("\n");
                                    }
                                    builder.deleteCharAt(builder.length()-1); // Remove last newline
                                    context.getSource().sendFeedback(builder.toString());
                                    return 0;
                                })
                        )
                )
                .then(LiteralArgumentBuilder.<CommandSource>literal("add")
                        .then(RequiredArgumentBuilder.<CommandSource, PlayerSelector>argument("player", player())
                                .then(RequiredArgumentBuilder.<CommandSource, String>argument("node", string())
                                        .executes(context -> {
                                            final StringBuilder builder = new StringBuilder();
                                            final PermissionNode node = new PermissionNode(getString(context, "node"));
                                            for (String playerName : getPlayer(context, "player").getPlayerNames(context.getSource())) {
                                                final boolean success = PermissionManager.addNodeToName(playerName, node);
                                                builder.append(success ? "Added" : "Failed to add");
                                                builder.append(" node ");
                                                builder.append(node.toString());
                                                builder.append(" to ");
                                                builder.append(playerName);
                                                builder.append("\n");
                                            }
                                            builder.deleteCharAt(builder.length()-1); // Remove last newline
                                            sendFeedbackAndLog(context.getSource(), builder.toString());
                                            return 0;
                                        })
                                )
                        )
                )
                .then(LiteralArgumentBuilder.<CommandSource>literal("remove")
                        .then(RequiredArgumentBuilder.<CommandSource, PlayerSelector>argument("player", player())
                                .then(RequiredArgumentBuilder.<CommandSource, String>argument("node", string())
                                        .executes(context -> {
                                            final StringBuilder builder = new StringBuilder();
                                            final PermissionNode node = new PermissionNode(getString(context, "node"));
                                            for (String playerName : getPlayer(context, "player").getPlayerNames(context.getSource())) {
                                                final boolean success = PermissionManager.removeNodeFromName(playerName, node);
                                                builder.append(success ? "Removed" : "Failed to remove");
                                                builder.append(" node ");
                                                builder.append(node.toString());
                                                builder.append(" from ");
                                                builder.append(playerName);
                                                builder.append("\n");
                                            }
                                            builder.deleteCharAt(builder.length()-1); // Remove last newline
                                            sendFeedbackAndLog(context.getSource(), builder.toString());
                                            return 0;
                                        })
                                )
                        )
                );
    }
}
