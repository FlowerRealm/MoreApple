package com.flowerrealm.moreapple.apple;

import com.flowerrealm.moreapple.MoreApple;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.util.Identifier;

public final class AppleModifiers {
    private static final Map<Identifier, AppleModifier> BY_ID = new LinkedHashMap<>();

    public static final AppleModifier GOLDEN = register(new AppleModifier(
        MoreApple.id("golden"),
        1,
        "nutrition",
        Optional.of("apple_base"),
        100
    ));
    public static final AppleModifier ENCHANTED = register(new AppleModifier(
        MoreApple.id("enchanted"),
        1,
        "nutrition",
        Optional.of("apple_base"),
        100
    ));
    public static final AppleModifier HEALING = register(new AppleModifier(
        MoreApple.id("healing"),
        3,
        "effect",
        Optional.empty(),
        200
    ));
    public static final AppleModifier FIRE_RESISTANCE = register(new AppleModifier(
        MoreApple.id("fire_resistance"),
        2,
        "effect",
        Optional.empty(),
        210
    ));

    private AppleModifiers() {
    }

    public static void registerAll() {
        MoreApple.LOGGER.info("Registered {} apple modifiers", BY_ID.size());
    }

    public static Collection<AppleModifier> values() {
        return Collections.unmodifiableCollection(BY_ID.values());
    }

    public static Optional<AppleModifier> get(Identifier id) {
        return Optional.ofNullable(BY_ID.get(id));
    }

    private static AppleModifier register(AppleModifier modifier) {
        AppleModifier previous = BY_ID.putIfAbsent(modifier.id(), modifier);
        if (previous != null) {
            throw new IllegalArgumentException("Duplicate apple modifier: " + modifier.id());
        }
        return modifier;
    }
}
