package com.williambl.legacybrigadier.impl.server.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.williambl.legacybrigadier.api.argument.playerselector.TargetSelector;
import com.williambl.legacybrigadier.api.command.CommandProvider;
import com.williambl.legacybrigadier.api.command.ExtendedSender;
import com.williambl.legacybrigadier.api.permission.PermissionManager;
import com.williambl.legacybrigadier.api.permission.PermissionNode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Set;

import static com.williambl.legacybrigadier.api.argument.permissionnode.PermissionNodeArgumentType.getPermissionNode;
import static com.williambl.legacybrigadier.api.argument.permissionnode.PermissionNodeArgumentType.permissionNode;
import static com.williambl.legacybrigadier.api.argument.playerselector.TargetSelectorArgumentType.getEntities;
import static com.williambl.legacybrigadier.api.argument.playerselector.TargetSelectorArgumentType.entities;
import static com.williambl.legacybrigadier.api.predicate.HasPermission.permission;

@Environment(EnvType.SERVER)
public class PermissionsCommand implements CommandProvider {

    @Override
    public LiteralArgumentBuilder<ExtendedSender> get() {
        return LiteralArgumentBuilder.<ExtendedSender>literal("permissions")
                .requires(permission("command.permissions"))
                .then(LiteralArgumentBuilder.<ExtendedSender>literal("get")
                        .then(RequiredArgumentBuilder.<ExtendedSender, TargetSelector>argument("player", entities())
                                .executes(context -> {
                                    final StringBuilder builder = new StringBuilder();
                                    for (String playerName : getEntities(context, "player").getEntityNames(context.getSource())) {
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
                                    context.getSource().sendCommandFeedback(builder.toString());
                                    return 0;
                                })
                        )
                )
                .then(LiteralArgumentBuilder.<ExtendedSender>literal("add")
                        .then(RequiredArgumentBuilder.<ExtendedSender, TargetSelector>argument("player", entities())
                                .then(RequiredArgumentBuilder.<ExtendedSender, PermissionNode>argument("node", permissionNode())
                                        .executes(context -> {
                                            final StringBuilder builder = new StringBuilder();
                                            final PermissionNode node = getPermissionNode(context, "node");
                                            for (String playerName : getEntities(context, "player").getEntityNames(context.getSource())) {
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
                .then(LiteralArgumentBuilder.<ExtendedSender>literal("remove")
                        .then(RequiredArgumentBuilder.<ExtendedSender, TargetSelector>argument("player", entities())
                                .then(RequiredArgumentBuilder.<ExtendedSender, PermissionNode>argument("node", permissionNode())
                                        .executes(context -> {
                                            final StringBuilder builder = new StringBuilder();
                                            final PermissionNode node = getPermissionNode(context, "node");
                                            for (String playerName : getEntities(context, "player").getEntityNames(context.getSource())) {
                                                final boolean success = PermissionManager.removeNodeFromName(playerName, node);
                                                builder.append(success ? "Removed" : "Failed to remove");
                                                builder.append(" node ");
                                                builder.append(node);
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
