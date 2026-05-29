package com.flowerrealm.moreapple.apple;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.Identifier;

public record AppleModifier(
    Identifier id,
    int maxLevel,
    String category,
    Optional<String> exclusiveGroup,
    int priority
) {
    public AppleModifier {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(exclusiveGroup, "exclusiveGroup");
        if (maxLevel < 1) {
            throw new IllegalArgumentException("maxLevel must be positive");
        }
        if (category.isBlank()) {
            throw new IllegalArgumentException("category must not be blank");
        }
        if (exclusiveGroup.isPresent() && exclusiveGroup.get().isBlank()) {
            throw new IllegalArgumentException("exclusiveGroup must not be blank");
        }
    }

    public boolean conflictsWith(AppleModifier other) {
        return exclusiveGroup.isPresent() && exclusiveGroup.equals(other.exclusiveGroup()) && !id.equals(other.id());
    }
}
