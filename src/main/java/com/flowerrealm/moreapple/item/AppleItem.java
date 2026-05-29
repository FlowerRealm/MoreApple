package com.flowerrealm.moreapple.item;

import com.flowerrealm.moreapple.apple.AppleModifiers;
import com.flowerrealm.moreapple.apple.AppleStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.FoodComponents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;

public final class AppleItem extends Item {
    public static final FoodComponent BASE_FOOD = FoodComponents.APPLE;
    public static final FoodComponent GOLDEN_FOOD = FoodComponents.GOLDEN_APPLE;

    public AppleItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!isGolden(stack)) {
            return super.finishUsing(stack, world, user);
        }

        if (user instanceof PlayerEntity player) {
            player.getHungerManager().add(GOLDEN_FOOD.getHunger(), GOLDEN_FOOD.getSaturationModifier());
            player.incrementStat(Stats.USED.getOrCreateStat(this));
        }

        applyGoldenEffects(stack, world, user);
        if (!(user instanceof PlayerEntity player) || !player.getAbilities().creativeMode) {
            stack.decrement(1);
        }
        user.emitGameEvent(net.minecraft.world.event.GameEvent.EAT);
        return stack;
    }

    private static boolean isGolden(ItemStack stack) {
        return AppleStack.from(stack).level(AppleModifiers.GOLDEN.id()) > 0;
    }

    private static void applyGoldenEffects(ItemStack stack, World world, LivingEntity user) {
        for (Pair<StatusEffectInstance, Float> entry : GOLDEN_FOOD.getStatusEffects()) {
            StatusEffectInstance effect = entry.getFirst();
            if (!world.isClient() && effect != null && world.random.nextFloat() < entry.getSecond()) {
                user.addStatusEffect(new StatusEffectInstance(effect));
            }
        }
    }
}
