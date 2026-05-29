package com.flowerrealm.moreapple.apple;

import java.util.Objects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;

public record AppleModifierEntry(Identifier id, int level) {
    private static final String ID_KEY = "Id";
    private static final String LEVEL_KEY = "Level";

    public AppleModifierEntry {
        Objects.requireNonNull(id, "id");
        if (level < 1) {
            throw new IllegalArgumentException("level must be positive");
        }
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString(ID_KEY, id.toString());
        nbt.putInt(LEVEL_KEY, level);
        return nbt;
    }

    public static AppleModifierEntry fromNbt(NbtCompound nbt) {
        if (!nbt.contains(ID_KEY, NbtElement.STRING_TYPE) || !nbt.contains(LEVEL_KEY, NbtElement.NUMBER_TYPE)) {
            return null;
        }

        Identifier id = Identifier.tryParse(nbt.getString(ID_KEY));
        int level = nbt.getInt(LEVEL_KEY);
        if (id == null || level < 1) {
            return null;
        }
        return new AppleModifierEntry(id, level);
    }

    public AppleModifierEntry withLevel(int newLevel) {
        return new AppleModifierEntry(id, newLevel);
    }
}
