package liedge.limacore.recipe.ingredient;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.registry.game.LimaCoreIngredientTypes;
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
            LimaCoreCodecs.floatOpenEndRange(0f, 1f).fieldOf("consume_chance").forGetter(ConsumeChanceIngredient::consumeChance))
            .apply(instance, ConsumeChanceIngredient::new));

    public static Ingredient of(Ingredient child, float consumeChance)
    {
        return new Ingredient(new ConsumeChanceIngredient(child, consumeChance));
    }

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
        // We need to force sync
        return false;
    }

    @Override
    public IngredientType<?> getType()
    {
        return LimaCoreIngredientTypes.CONSUME_CHANCE.get();
    }
}