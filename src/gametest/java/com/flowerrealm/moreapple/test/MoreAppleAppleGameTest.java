package com.flowerrealm.moreapple.test;

import com.flowerrealm.moreapple.MoreApple;
import com.flowerrealm.moreapple.apple.AppleProcessing;
import com.flowerrealm.moreapple.apple.AppleStack;
import com.flowerrealm.moreapple.apple.AppleModifiers;
import com.flowerrealm.moreapple.item.MoreAppleItems;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class MoreAppleAppleGameTest implements FabricGameTest {
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void appleItemIsRegistered(TestContext context) {
        context.assertTrue(Registries.ITEM.containsId(MoreApple.id("apple")), "moreapple apple item must be registered");
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void appleStartsWithoutModifiers(TestContext context) {
        AppleStack apple = AppleStack.from(MoreAppleItems.appleStack());

        context.assertTrue(apple.entries().isEmpty(), "fresh apple should not have modifiers");
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void processingAddsAndLevelsModifiers(TestContext context) {
        ItemStack stack = MoreAppleItems.appleStack();
        AppleProcessing.Result golden = AppleProcessing.applyModifier(stack, AppleModifiers.GOLDEN.id(), 1);
        AppleProcessing.Result healing = AppleProcessing.applyModifier(golden.stack(), AppleModifiers.HEALING.id(), 2);
        AppleStack apple = AppleStack.from(healing.stack());

        context.assertTrue(golden.success(), "golden modifier should apply");
        context.assertTrue(healing.success(), "healing modifier should apply twice");
        context.assertTrue(apple.level(AppleModifiers.GOLDEN.id()) == 1, "golden level should be 1");
        context.assertTrue(apple.level(AppleModifiers.HEALING.id()) == 2, "healing level should be 2");
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void processingRejectsOverMaxWithoutMutation(TestContext context) {
        ItemStack stack = MoreAppleItems.appleStack();
        AppleProcessing.Result first = AppleProcessing.applyModifier(stack, AppleModifiers.HEALING.id(), 3);
        AppleProcessing.Result overflow = AppleProcessing.applyModifier(first.stack(), AppleModifiers.HEALING.id(), 1);
        AppleStack apple = AppleStack.from(overflow.stack());

        context.assertTrue(first.success(), "healing should apply up to max level");
        context.assertFalse(overflow.success(), "healing above max level should fail");
        context.assertTrue(
            overflow.failure() == AppleProcessing.Failure.MAX_LEVEL_REACHED,
            "failure should be max level reached"
        );
        context.assertTrue(apple.level(AppleModifiers.HEALING.id()) == 3, "failed processing should keep previous level");
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void processingRejectsExclusiveConflict(TestContext context) {
        ItemStack stack = MoreAppleItems.appleStack();
        AppleProcessing.Result golden = AppleProcessing.applyModifier(stack, AppleModifiers.GOLDEN.id(), 1);
        AppleProcessing.Result enchanted = AppleProcessing.applyModifier(golden.stack(), AppleModifiers.ENCHANTED.id(), 1);
        AppleStack apple = AppleStack.from(enchanted.stack());

        context.assertTrue(golden.success(), "golden modifier should apply");
        context.assertFalse(enchanted.success(), "enchanted modifier should conflict with golden");
        context.assertTrue(
            enchanted.failure() == AppleProcessing.Failure.EXCLUSIVE_CONFLICT,
            "failure should be exclusive conflict"
        );
        context.assertTrue(apple.level(AppleModifiers.GOLDEN.id()) == 1, "golden level should stay");
        context.assertTrue(apple.level(AppleModifiers.ENCHANTED.id()) == 0, "enchanted level should not be written");
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void processingHasNoTotalModifierLimit(TestContext context) {
        ItemStack stack = MoreAppleItems.appleStack();
        AppleProcessing.Result golden = AppleProcessing.applyModifier(stack, AppleModifiers.GOLDEN.id(), 1);
        AppleProcessing.Result healing = AppleProcessing.applyModifier(golden.stack(), AppleModifiers.HEALING.id(), 1);
        AppleProcessing.Result fireResistance = AppleProcessing.applyModifier(healing.stack(), AppleModifiers.FIRE_RESISTANCE.id(), 1);
        AppleStack apple = AppleStack.from(fireResistance.stack());

        context.assertTrue(fireResistance.success(), "third modifier should apply");
        context.assertTrue(apple.entries().size() == 3, "apple should allow at least three modifiers");
        context.assertTrue(apple.level(AppleModifiers.GOLDEN.id()) == 1, "golden level should be present");
        context.assertTrue(apple.level(AppleModifiers.HEALING.id()) == 1, "healing level should be present");
        context.assertTrue(apple.level(AppleModifiers.FIRE_RESISTANCE.id()) == 1, "fire resistance level should be present");
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void legacyDefinitionMapsToBaseModifier(TestContext context) {
        ItemStack golden = legacyStack("golden_apple");
        ItemStack enchanted = legacyStack("enchanted_golden_apple");

        context.assertTrue(
            AppleStack.from(golden).level(AppleModifiers.GOLDEN.id()) == 1,
            "legacy golden apple definition should map to golden modifier"
        );
        context.assertTrue(
            AppleStack.from(enchanted).level(AppleModifiers.ENCHANTED.id()) == 1,
            "legacy enchanted apple definition should map to enchanted modifier"
        );
        context.complete();
    }

    private static ItemStack legacyStack(String idPath) {
        ItemStack stack = MoreAppleItems.appleStack();
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putString(AppleStack.LEGACY_DEFINITION_KEY, MoreApple.id(idPath).toString());
        return stack;
    }
}
