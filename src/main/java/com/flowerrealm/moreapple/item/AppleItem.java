package com.flowerrealm.moreapple.item;

import com.flowerrealm.moreapple.MoreApple;
import com.flowerrealm.moreapple.apple.AppleModifier;
import com.flowerrealm.moreapple.apple.AppleModifierEntry;
import com.flowerrealm.moreapple.apple.AppleModifiers;
import com.flowerrealm.moreapple.apple.AppleStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.FoodComponents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import java.util.List;
import java.util.Optional;

public final class AppleItem extends Item {
    public static final FoodComponent BASE_FOOD = FoodComponents.APPLE;
    public static final FoodComponent GOLDEN_FOOD = FoodComponents.GOLDEN_APPLE;
    private static final String BASE_MODIFIER_NAME_KEY = "item." + MoreApple.MOD_ID + ".apple.with_base_modifier";
    private static final String MODIFIER_TRANSLATION_PREFIX = "modifier";
    private static final String MODIFIER_NAME_FRAGMENT_SUFFIX = ".name_fragment";
    private static final String BASE_NAME_CATEGORY = "nutrition";
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

    @Override
    public Text getName(ItemStack stack) {
        AppleStack apple = AppleStack.from(stack);
        Optional<Text> baseModifierName = apple.sortedEntries().stream()
            .map(entry -> AppleModifiers.get(entry.id()))
            .flatMap(Optional::stream)
            .filter(modifier -> BASE_NAME_CATEGORY.equals(modifier.category()))
            .findFirst()
            .map(modifier -> Text.translatable(BASE_MODIFIER_NAME_KEY, modifierNameFragment(modifier)));

        return baseModifierName.orElseGet(() -> super.getName(stack));
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        AppleStack.from(stack).sortedEntries().stream()
            .map(AppleModifierEntry::id)
            .map(AppleModifiers::get)
            .flatMap(Optional::stream)
            .map(modifier -> Text.translatable(modifierTranslationKey(modifier)).formatted(Formatting.GRAY))
            .forEach(tooltip::add);
    }

    public static boolean hasGoldenModifier(ItemStack stack) {
        return AppleStack.from(stack).level(AppleModifiers.GOLDEN.id()) > 0;
    }

    private static boolean isGolden(ItemStack stack) {
        return hasGoldenModifier(stack);
    }

    private static Text modifierNameFragment(AppleModifier modifier) {
        return Text.translatable(modifierTranslationKey(modifier) + MODIFIER_NAME_FRAGMENT_SUFFIX);
    }

    private static String modifierTranslationKey(AppleModifier modifier) {
        return MODIFIER_TRANSLATION_PREFIX + "." + modifier.id().getNamespace() + "." + modifier.id().getPath();
    }
}
