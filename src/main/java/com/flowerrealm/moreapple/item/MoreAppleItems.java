package com.flowerrealm.moreapple.item;

import com.flowerrealm.moreapple.MoreApple;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class MoreAppleItems {
    public static final AppleItem APPLE = register("apple", new AppleItem(new Item.Settings().maxCount(64).food(AppleItem.BASE_FOOD)));

    private MoreAppleItems() {
    }

    public static void registerAll() {
        MoreApple.LOGGER.info("Registered More Apple items");
    }

    public static ItemStack appleStack() {
        return new ItemStack(APPLE);
    }

    private static <T extends Item> T register(String path, T item) {
        return Registry.register(Registries.ITEM, MoreApple.id(path), item);
    }
}
