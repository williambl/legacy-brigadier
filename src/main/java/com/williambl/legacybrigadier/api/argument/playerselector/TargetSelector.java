package com.williambl.legacybrigadier.api.argument.playerselector;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.williambl.legacybrigadier.api.command.ExtendedSender;
import com.williambl.legacybrigadier.api.utils.EntityUtils;
import com.williambl.legacybrigadier.impl.server.argument.SelfSelector;
import com.williambl.legacybrigadier.impl.server.utils.StringReaderUtils;
import com.williambl.legacybrigadier.impl.server.utils.UncheckedCaster;
import io.github.minecraftcursedlegacy.api.registry.Id;
import io.github.minecraftcursedlegacy.api.registry.Registries;
import io.github.minecraftcursedlegacy.impl.registry.EntityType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.Player;
import net.minecraft.level.Level;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Environment(EnvType.SERVER)
public class TargetSelector<E extends Entity> implements Predicate<Entity> {
    private static final SimpleCommandExceptionType INVALID_TARGET_SELECTOR = new SimpleCommandExceptionType(new LiteralMessage("Invalid Target Selector"));
    private static final SimpleCommandExceptionType INVALID_ENTITY_TYPE = new SimpleCommandExceptionType(new LiteralMessage("Invalid Entity Type"));
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

    public static TargetSelector<?> create(char selectorType, String optionsString) throws CommandSyntaxException {
        final Options options = new Options(StringReaderUtils.readTargetSelectorOptions(new StringReader(optionsString)));
        switch (selectorType) {
            case 'a': {
                return new TargetSelector<>(Player.class, options.name(), options.limit(), options.sort());
            }
            case 'p': {
                return new TargetSelector<>(Player.class, options.name(), 1, SortingMethod.NEAREST);
            }
            case 'r': {
                return new TargetSelector<>(Player.class, options.name(), 1, SortingMethod.RANDOM);
            }
            case 'e': {
                return new TargetSelector<>(options.clazz(), options.name(), options.limit(), options.sort());
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
        return this.clazz.isAssignableFrom(entity.getClass()) && (this.name == null || this.name.equals(EntityUtils.getName(entity)));
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
        FURTHEST(sender -> NEAREST.getComparator(sender).reversed()),
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

    private static class Options {
        private final Map<String, String> optionStrings;

        private Options(final Map<String, String> optionStrings) {
            this.optionStrings = optionStrings;
        }

        private String name() {
            return this.getString("name", null);
        }

        private int limit() {
            return this.getInt("limit", Integer.MAX_VALUE);
        }

        private SortingMethod sort() {
            return this.getEnum("sort", SortingMethod.class, SortingMethod.RANDOM);
        }

        private Class<? extends Entity> clazz() throws CommandSyntaxException {
            final String entityTypeName = this.getString("type", null);
            if (entityTypeName == null) {
                return Entity.class;
            } else {
                final EntityType entityType = Registries.ENTITY_TYPE.getById(new Id(entityTypeName));
                if (entityType == null) {
                    throw INVALID_ENTITY_TYPE.create();
                }

                return entityType.getClazz();
            }
        }

        private int getInt(final String key, final int defaultValue) {
            final String value = this.optionStrings.get(key);
            if (value != null) {
                return Integer.parseInt(value);
            } else {
                return defaultValue;
            }
        }

        private double getDouble(final String key, final double defaultValue) {
            final String value = this.optionStrings.get(key);
            if (value != null) {
                return Double.parseDouble(value);
            } else {
                return defaultValue;
            }
        }

        private <E extends Enum<E>> E getEnum(final String key, final Class<E> clazz, final E defaultValue) {
            final String value = this.optionStrings.get(key);
            final E[] enumValues = clazz.getEnumConstants();

            if (value != null) {
                for (E element : enumValues) {
                    if (element.name().equalsIgnoreCase(value)) {
                        return element;
                    }
                }
            }

            return defaultValue;
        }

        private String getString(final String key, final String defaultValue) {
            return this.optionStrings.getOrDefault(key, defaultValue);
        }
    }
}