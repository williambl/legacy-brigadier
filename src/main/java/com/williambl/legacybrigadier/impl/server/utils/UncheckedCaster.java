package com.williambl.legacybrigadier.impl.server.utils;

import com.mojang.brigadier.context.CommandContext;

import java.util.List;

@SuppressWarnings({"unchecked", "rawtypes"})
public final class UncheckedCaster {
    public static <T> List<T> list(List theList) {
        return (List<T>)theList;
    }

    public static <T> CommandContext<T> context(CommandContext<?> theContext) {
        return (CommandContext<T>)theContext;
    }
}
