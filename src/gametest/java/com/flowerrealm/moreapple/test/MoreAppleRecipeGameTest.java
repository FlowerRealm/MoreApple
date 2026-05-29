package com.flowerrealm.moreapple.test;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.Reader;
import java.util.Set;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.Items;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;

public final class MoreAppleRecipeGameTest implements FabricGameTest {
    private static final Identifier GOLDEN_APPLE_ID = new Identifier("minecraft", "golden_apple");
    private static final Identifier ENCHANTED_GOLDEN_APPLE_ID = new Identifier("minecraft", "enchanted_golden_apple");
    private static final Identifier MOJANG_BANNER_PATTERN_ID = new Identifier("minecraft", "mojang_banner_pattern");
    private static final Identifier TCONSTRUCT_GOLDEN_APPLE_CASTING_ID = new Identifier("tconstruct", "smeltery/casting/metal/gold/apple");

    private static final Set<Identifier> REMOVED_RECIPE_IDS = Set.of(
        GOLDEN_APPLE_ID,
        ENCHANTED_GOLDEN_APPLE_ID,
        MOJANG_BANNER_PATTERN_ID,
        TCONSTRUCT_GOLDEN_APPLE_CASTING_ID
    );

    private static final Set<Identifier> COVERED_CHEST_LOOT_TABLES = Set.of(
        new Identifier("minecraft", "chests/abandoned_mineshaft"),
        new Identifier("minecraft", "chests/ancient_city"),
        new Identifier("minecraft", "chests/bastion_hoglin_stable"),
        new Identifier("minecraft", "chests/bastion_other"),
        new Identifier("minecraft", "chests/bastion_treasure"),
        new Identifier("minecraft", "chests/desert_pyramid"),
        new Identifier("minecraft", "chests/igloo_chest"),
        new Identifier("minecraft", "chests/ruined_portal"),
        new Identifier("minecraft", "chests/simple_dungeon"),
        new Identifier("minecraft", "chests/stronghold_corridor"),
        new Identifier("minecraft", "chests/underwater_ruin_big"),
        new Identifier("minecraft", "chests/woodland_mansion")
    );

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void vanillaGoldenApplesAreNotSurvivalObtainable(TestContext context) {
        assertItemsStayRegistered(context);
        assertRecipesDoNotCreateGoldenApples(context);
        assertCoveredLootTablesDoNotContainGoldenApples(context);
        assertPiglinsDoNotLoveGoldenApples(context);
        context.complete();
    }

    private static void assertItemsStayRegistered(TestContext context) {
        context.assertTrue(Registries.ITEM.containsId(GOLDEN_APPLE_ID), "golden apple item must stay registered");
        context.assertTrue(Registries.ITEM.containsId(ENCHANTED_GOLDEN_APPLE_ID), "enchanted golden apple item must stay registered");
    }

    private static void assertRecipesDoNotCreateGoldenApples(TestContext context) {
        var recipes = context.getWorld().getRecipeManager();
        var registryManager = context.getWorld().getRegistryManager();

        for (Identifier id : REMOVED_RECIPE_IDS) {
            context.assertFalse(recipes.get(id).isPresent(), id + " recipe should be removed");
        }

        for (Recipe<?> recipe : recipes.values()) {
            var output = recipe.getOutput(registryManager);
            context.assertFalse(output.isOf(Items.GOLDEN_APPLE), recipe.getId() + " should not create golden apples");
            context.assertFalse(output.isOf(Items.ENCHANTED_GOLDEN_APPLE), recipe.getId() + " should not create enchanted golden apples");
        }
    }

    private static void assertCoveredLootTablesDoNotContainGoldenApples(TestContext context) {
        var resourceManager = context.getWorld().getServer().getResourceManager();

        for (Identifier id : COVERED_CHEST_LOOT_TABLES) {
            Identifier resourceId = new Identifier(id.getNamespace(), "loot_tables/" + id.getPath() + ".json");
            var resource = resourceManager.getResource(resourceId).orElseThrow(() ->
                new AssertionError(resourceId + " resource should exist")
            );

            try (Reader reader = resource.getReader()) {
                JsonElement json = JsonParser.parseReader(reader);
                context.assertFalse(containsItem(json, "minecraft:golden_apple"), id + " should not contain golden apples");
                context.assertFalse(
                    containsItem(json, "minecraft:enchanted_golden_apple"),
                    id + " should not contain enchanted golden apples"
                );
            } catch (IOException e) {
                throw new AssertionError("Failed to read " + resourceId, e);
            }
        }
    }

    private static void assertPiglinsDoNotLoveGoldenApples(TestContext context) {
        context.assertFalse(Items.GOLDEN_APPLE.getDefaultStack().isIn(ItemTags.PIGLIN_LOVED), "piglins should not love golden apples");
        context.assertFalse(
            Items.ENCHANTED_GOLDEN_APPLE.getDefaultStack().isIn(ItemTags.PIGLIN_LOVED),
            "piglins should not love enchanted golden apples"
        );
    }

    private static boolean containsItem(JsonElement element, String itemId) {
        if (element.isJsonObject()) {
            var object = element.getAsJsonObject();

            if (object.has("name") && object.get("name").isJsonPrimitive() && itemId.equals(object.get("name").getAsString())) {
                return true;
            }

            for (JsonElement value : object.asMap().values()) {
                if (containsItem(value, itemId)) {
                    return true;
                }
            }
        } else if (element.isJsonArray()) {
            for (JsonElement value : element.getAsJsonArray()) {
                if (containsItem(value, itemId)) {
                    return true;
                }
            }
        }

        return false;
    }
}
