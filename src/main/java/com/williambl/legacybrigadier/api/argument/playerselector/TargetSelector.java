package com.williambl.legacybrigadier.api.argument.playerselector;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.williambl.legacybrigadier.api.command.ExtendedSender;
import com.williambl.legacybrigadier.api.utils.EntityUtils;
import com.williambl.legacybrigadier.impl.server.argument.SelfSelector;
import com.williambl.legacybrigadier.impl.server.utils.UncheckedCaster;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.Player;
import net.minecraft.level.Level;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Environment(EnvType.SERVER)
public class TargetSelector<E extends Entity> implements Predicate<Entity> {
    private static final SimpleCommandExceptionType INVALID_TARGET_SELECTOR = new SimpleCommandExceptionType(new LiteralMessage("Invalid Target Selector"));
    private final Class<E> clazz;
    private final String name;
    private final int limit;
    private final SortingMethod sortingMethod;

    protected TargetSelector(Class<E> clazz, String name, int limit, SortingMethod sortingMethod) {
        this.clazz = clazz;
        this.name = name;
        this.limit = limit;
        this.sortingMethod = sortingMethod;
    }

    public static TargetSelector<?> literal(String name) throws CommandSyntaxException {
        return new TargetSelector<>(Entity.class, name, 1, SortingMethod.RANDOM);
    }

    public static TargetSelector<?> create(char selectorType, String options) throws CommandSyntaxException {
        switch (selectorType) {
            case 'a': {
                return new TargetSelector<>(Player.class, null, Integer.MAX_VALUE, SortingMethod.RANDOM);
            }
            case 'p': {
                return new TargetSelector<>(Player.class, null, 1, SortingMethod.NEAREST);
            }
            case 'r': {
                return new TargetSelector<>(Player.class, null, 1, SortingMethod.RANDOM);
            }
            case 'e': {
                return new TargetSelector<>(Entity.class, null, Integer.MAX_VALUE, SortingMethod.RANDOM);
            }
            case 's': {
                return new SelfSelector();
            }
            default: {
                throw INVALID_TARGET_SELECTOR.create();
            }
        }
    }

    public boolean isPlayerOnly() {
        return Player.class.isAssignableFrom(this.clazz);
    }

    public boolean isSingleOnly() {
        return this.limit == 1;
    }

    @Override
    public boolean test(Entity entity) {
        return entity.getClass().isAssignableFrom(this.clazz) && (this.name == null || this.name.equals(EntityUtils.getName(entity)));
    }

    public List<E> getEntities(ExtendedSender sender) {
        return this.getMatchingEntities(sender);
    }

    public List<String> getNames(ExtendedSender sender) {
        return this.getEntities(sender).stream().map(EntityUtils::getName).collect(Collectors.toList());
    }

    protected List<E> getMatchingEntities(ExtendedSender sender) {
        return getAllEntities(sender).stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .filter(this)
                .sorted(sortingMethod.getComparator(sender))
                .limit(limit)
                .collect(Collectors.toList());
    }

    protected static List<Entity> getAllEntities(ExtendedSender sender) {
        Level level = sender.getWorld();
        if (level != null) {
            return UncheckedCaster.list(level.entities);
        } else {
            return UncheckedCaster.list(Arrays.stream((sender).getServer().levels).flatMap(l -> UncheckedCaster.list(l.entities).stream()).collect(Collectors.toList()));
        }
    }

    public enum SortingMethod {
        NEAREST(sender ->
            Comparator.comparingDouble(e -> EntityUtils.distanceBetween(e, sender.getPosition()))
        ),
        RANDOM(sender -> {
            int randomValue = sender.getWorld().rand.nextInt();
            return Comparator.<Entity>comparingInt(e -> e.hashCode()^randomValue)
                    .thenComparing(NEAREST.getComparator(sender)); // if they have the same hash code, just resort to nearest
        });

        private final Function<ExtendedSender, Comparator<Entity>> implementation;

        SortingMethod(Function<ExtendedSender, Comparator<Entity>> implementation) {
            this.implementation = implementation;
        }

        public Comparator<Entity> getComparator(ExtendedSender sender) {
            return implementation.apply(sender);
        }
    }
}