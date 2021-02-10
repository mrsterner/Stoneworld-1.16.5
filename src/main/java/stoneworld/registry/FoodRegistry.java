package stoneworld.registry;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import org.lwjgl.system.CallbackI;

public class FoodRegistry {
    public static final FoodComponent.Builder EDIBLE_1_BUILDER = new FoodComponent.Builder().hunger(1).saturationModifier(0.4F);
    public static final FoodComponent.Builder EDIBLE_2_BUILDER = new FoodComponent.Builder().hunger(2).saturationModifier(0.4F);

    public static final FoodComponent.Builder POISONOUS_2_BUILDER = new FoodComponent.Builder().hunger(2).saturationModifier(0.4F)
            .statusEffect(new StatusEffectInstance(StatusEffects.POISON, 10, 1),1);


    public static final FoodComponent EDIBLE_3 = EDIBLE_1_BUILDER.build();
    public static final FoodComponent EDIBLE_1 = EDIBLE_2_BUILDER.build();

    public static final FoodComponent POISONOUS_1 = POISONOUS_2_BUILDER.build();

}
