package com.flowerrealm.moreapple.apple;

import com.flowerrealm.moreapple.MoreApple;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

public final class AppleStack {
    public static final String ROOT_KEY = "MoreApple";
    public static final String SCHEMA_VERSION_KEY = "SchemaVersion";
    public static final String MODIFIERS_KEY = "Modifiers";
    public static final String LEGACY_DEFINITION_KEY = "MoreAppleDefinition";
    public static final int SCHEMA_VERSION = 1;

    private final ItemStack stack;
    private final List<AppleModifierEntry> entries;

    private AppleStack(ItemStack stack, List<AppleModifierEntry> entries) {
        this.stack = stack;
        this.entries = List.copyOf(entries);
    }

    public static AppleStack from(ItemStack stack) {
        return new AppleStack(stack, readEntries(stack));
    }

    public ItemStack stack() {
        return stack;
    }

    public List<AppleModifierEntry> entries() {
        return entries;
    }

    public List<AppleModifierEntry> sortedEntries() {
        return entries.stream()
            .sorted(Comparator
                .comparingInt((AppleModifierEntry entry) -> AppleModifiers.get(entry.id()).map(AppleModifier::priority).orElse(Integer.MAX_VALUE))
                .thenComparing(entry -> entry.id().toString()))
            .toList();
    }

    public int level(Identifier id) {
        return entry(id).map(AppleModifierEntry::level).orElse(0);
    }

    public Optional<AppleModifierEntry> entry(Identifier id) {
        return entries.stream().filter(entry -> entry.id().equals(id)).findFirst();
    }

    public void write(List<AppleModifierEntry> newEntries) {
        NbtCompound root = stack.getOrCreateSubNbt(ROOT_KEY);
        root.putInt(SCHEMA_VERSION_KEY, SCHEMA_VERSION);
        NbtList modifiers = new NbtList();
        newEntries.stream()
            .sorted(Comparator.comparing(entry -> entry.id().toString()))
            .forEach(entry -> modifiers.add(entry.toNbt()));
        root.put(MODIFIERS_KEY, modifiers);
    }

    private static List<AppleModifierEntry> readEntries(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) {
            return List.of();
        }

        if (nbt.contains(ROOT_KEY, NbtElement.COMPOUND_TYPE)) {
            NbtCompound root = nbt.getCompound(ROOT_KEY);
            if (root.contains(MODIFIERS_KEY, NbtElement.LIST_TYPE)) {
                return readModifierList(root.getList(MODIFIERS_KEY, NbtElement.COMPOUND_TYPE));
            }
        }

        return readLegacyDefinition(nbt);
    }

    private static List<AppleModifierEntry> readModifierList(NbtList modifiers) {
        List<AppleModifierEntry> entries = new ArrayList<>();
        for (int i = 0; i < modifiers.size(); i++) {
            AppleModifierEntry entry = AppleModifierEntry.fromNbt(modifiers.getCompound(i));
            if (entry != null) {
                entries.add(entry);
            }
        }
        return List.copyOf(entries);
    }

    private static List<AppleModifierEntry> readLegacyDefinition(NbtCompound nbt) {
        if (!nbt.contains(LEGACY_DEFINITION_KEY, NbtElement.STRING_TYPE)) {
            return List.of();
        }

        Identifier id = Identifier.tryParse(nbt.getString(LEGACY_DEFINITION_KEY));
        if (id == null) {
            return List.of();
        }
        if (MoreApple.id("golden_apple").equals(id)) {
            return List.of(new AppleModifierEntry(AppleModifiers.GOLDEN.id(), 1));
        }
        if (MoreApple.id("enchanted_golden_apple").equals(id)) {
            return List.of(new AppleModifierEntry(AppleModifiers.ENCHANTED.id(), 1));
        }
        return List.of();
    }
}
