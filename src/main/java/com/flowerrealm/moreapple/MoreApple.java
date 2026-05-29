package com.flowerrealm.moreapple;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MoreApple implements ModInitializer {
    public static final String MOD_ID = "moreapple";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        registerBuiltinDataPacks();
        LOGGER.info("More Apple initialized");
    }

    private static void registerBuiltinDataPacks() {
        var container = FabricLoader.getInstance().getModContainer(MOD_ID)
            .orElseThrow(() -> new IllegalStateException(MOD_ID + " mod container is missing"));

        boolean registered = ResourceManagerHelper.registerBuiltinResourcePack(
            new Identifier(MOD_ID, "tconstruct_overrides"),
            container,
            ResourcePackActivationType.ALWAYS_ENABLED
        );

        if (!registered) {
            throw new IllegalStateException("Failed to register More Apple TConstruct data overrides");
        }
    }
}
