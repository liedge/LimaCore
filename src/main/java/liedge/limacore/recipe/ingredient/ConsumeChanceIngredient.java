package liedge.limacore.recipe.ingredient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.registry.game.LimaCoreIngredientTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.Arrays;
import java.util.stream.Stream;

public record ConsumeChanceIngredient(Ingredient child, float consumeChance) implements ICustomIngredient
{
    private static DataResult<Float> checkRange(float value)
    {
        if (value >= 0f && value < 1f)
        {
            return DataResult.success(value);
        }
        else
        {
            return DataResult.error(() -> "Ingredient consume chance " + value + " outside of range [0,1)");
        }
    }

    public static final MapCodec<ConsumeChanceIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("child").forGetter(ConsumeChanceIngredient::child),
            Codec.FLOAT.flatXmap(ConsumeChanceIngredient::checkRange, ConsumeChanceIngredient::checkRange).fieldOf("consume_chance").forGetter(ConsumeChanceIngredient::consumeChance))
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