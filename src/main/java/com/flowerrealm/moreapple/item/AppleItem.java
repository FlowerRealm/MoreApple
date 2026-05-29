package com.flowerrealm.moreapple.item;

import com.flowerrealm.moreapple.apple.AppleModifiers;
import com.flowerrealm.moreapple.apple.AppleStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.FoodComponents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public final class AppleItem extends Item {
    public static final FoodComponent BASE_FOOD = FoodComponents.APPLE;
    public static final FoodComponent GOLDEN_FOOD = FoodComponents.GOLDEN_APPLE;
    private static final ThreadLocal<Boolean> USING_GOLDEN_FOOD = ThreadLocal.withInitial(() -> false);

    public AppleItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        FoodComponent food = isGolden(stack) ? GOLDEN_FOOD : BASE_FOOD;
        if (user.canConsume(food.isAlwaysEdible())) {
            user.setCurrentHand(hand);
            return TypedActionResult.consume(stack);
        }
        return TypedActionResult.fail(stack);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!isGolden(stack)) {
            return super.finishUsing(stack, world, user);
        }

        USING_GOLDEN_FOOD.set(true);
        try {
            return user.eatFood(world, stack);
        } finally {
            USING_GOLDEN_FOOD.set(false);
        }
    }

    @Override
    public FoodComponent getFoodComponent() {
        return USING_GOLDEN_FOOD.get() ? GOLDEN_FOOD : BASE_FOOD;
    }

    private static boolean isGolden(ItemStack stack) {
        return AppleStack.from(stack).level(AppleModifiers.GOLDEN.id()) > 0;
    }
}
