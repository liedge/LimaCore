package liedge.limacore.recipe.ingredient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.registry.LimaCoreIngredientTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.Arrays;
import java.util.stream.Stream;

public record ConsumeChanceIngredient(Ingredient child, float consumeChance) implements ICustomIngredient
{
    public static final MapCodec<ConsumeChanceIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("child").forGetter(ConsumeChanceIngredient::child),
            Codec.floatRange(0f, 1f).fieldOf("consume_chance").forGetter(ConsumeChanceIngredient::consumeChance))
            .apply(instance, ConsumeChanceIngredient::new));

    @Override
    public boolean test(ItemStack stack)
    {
        return child.test(stack);
    }

    @Override
    public Stream<ItemStack> getItems()
    {
        return Arrays.stream(child.getItems());
    }

    @Override
    public boolean isSimple()
    {
        return child.isSimple(); // TODO Should this be a simple or ingredient or not?
    }

    @Override
    public IngredientType<?> getType()
    {
        return LimaCoreIngredientTypes.CONSUME_CHANCE.get();
    }
}