package com.flowerrealm.moreapple;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MoreApple implements ModInitializer {
    public static final String MOD_ID = "moreapple";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("More Apple initialized");
    }
}
