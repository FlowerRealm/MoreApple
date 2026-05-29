package com.flowerrealm.moreapple.apple;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public final class AppleProcessing {
    private AppleProcessing() {
    }

    public static Result applyModifier(ItemStack stack, Identifier modifierId, int amount) {
        if (amount < 1) {
            return Result.failure(Failure.INVALID_AMOUNT, stack.copy());
        }

        AppleModifier modifier = AppleModifiers.get(modifierId).orElse(null);
        if (modifier == null) {
            return Result.failure(Failure.UNKNOWN_MODIFIER, stack.copy());
        }

        AppleStack apple = AppleStack.from(stack);
        int currentLevel = apple.level(modifierId);
        int newLevel = currentLevel + amount;
        if (newLevel > modifier.maxLevel()) {
            return Result.failure(Failure.MAX_LEVEL_REACHED, stack.copy());
        }

        for (AppleModifierEntry entry : apple.entries()) {
            AppleModifier existing = AppleModifiers.get(entry.id()).orElse(null);
            if (existing != null && modifier.conflictsWith(existing)) {
                return Result.failure(Failure.EXCLUSIVE_CONFLICT, stack.copy());
            }
        }

        ItemStack result = stack.copy();
        AppleStack resultApple = AppleStack.from(result);
        resultApple.write(upsert(resultApple.entries(), modifierId, newLevel));
        return Result.success(result);
    }

    private static List<AppleModifierEntry> upsert(List<AppleModifierEntry> entries, Identifier id, int level) {
        List<AppleModifierEntry> result = new ArrayList<>();
        boolean replaced = false;
        for (AppleModifierEntry entry : entries) {
            if (entry.id().equals(id)) {
                result.add(entry.withLevel(level));
                replaced = true;
            } else {
                result.add(entry);
            }
        }
        if (!replaced) {
            result.add(new AppleModifierEntry(id, level));
        }
        return result;
    }

    public enum Failure {
        NONE,
        INVALID_AMOUNT,
        UNKNOWN_MODIFIER,
        MAX_LEVEL_REACHED,
        EXCLUSIVE_CONFLICT
    }

    public record Result(boolean success, Failure failure, ItemStack stack) {
        private static Result success(ItemStack stack) {
            return new Result(true, Failure.NONE, stack);
        }

        private static Result failure(Failure failure, ItemStack stack) {
            return new Result(false, failure, stack);
        }
    }
}
