package com.williambl.legacybrigadier.server.api.argument;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.williambl.legacybrigadier.server.mixinhooks.CommandSourceHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Vec3i;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.SERVER)
public class CoordinateArgumentType implements ArgumentType<Vec3i> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "1 2 3");
    public static final SimpleCommandExceptionType INCOMPLETE_EXCEPTION = new SimpleCommandExceptionType(new LiteralMessage("Incomplete"));

    public CoordinateArgumentType() {
    }

    public static CoordinateArgumentType coordinate() {
        return new CoordinateArgumentType();
    }

    public static Vec3i getCoordinate(final CommandContext<?> context, final String name) {
        return context.getArgument(name, Vec3i.class);
    }

    public Vec3i parse(StringReader stringReader) throws CommandSyntaxException {
        int cursor = stringReader.getCursor();
        int x = stringReader.readInt();
        if (stringReader.canRead() && stringReader.peek() == ' ') {
            stringReader.skip();
            int y = stringReader.readInt();
            if (stringReader.canRead() && stringReader.peek() == ' ') {
                stringReader.skip();
                int z = stringReader.readInt();
                return new Vec3i(x, y, z);
            } else {
                stringReader.setCursor(cursor);
                throw INCOMPLETE_EXCEPTION.createWithContext(stringReader);
            }
        } else {
            stringReader.setCursor(cursor);
            throw INCOMPLETE_EXCEPTION.createWithContext(stringReader);
        }
    }


    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        builder.suggest(vec3iToString(((CommandSourceHooks)context.getSource()).getPosition()));
        return builder.buildFuture();
    }

    private String vec3iToString(Vec3i vec) {
        return vec.x + " " + vec.y + " " + vec.z;
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}

