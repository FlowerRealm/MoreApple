package com.flowerrealm.moreapple.client;

import com.flowerrealm.moreapple.MoreApple;
import com.flowerrealm.moreapple.item.AppleItem;
import com.flowerrealm.moreapple.item.MoreAppleItems;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.item.ModelPredicateProviderRegistry;

public final class MoreAppleClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelPredicateProviderRegistry.register(
            MoreAppleItems.APPLE,
            MoreApple.id("golden"),
            (stack, world, entity, seed) -> AppleItem.hasGoldenModifier(stack) ? 1.0F : 0.0F
        );
    }
}
