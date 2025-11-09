package liedge.limacore.recipe.ingredient;

import com.mojang.serialization.MapCodec;
import liedge.limacore.registry.game.LimaCoreIngredientTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.Arrays;
import java.util.stream.Stream;

public record DeterministicItemIngredient(Ingredient child, float consumeChance) implements ICustomIngredient, DeterministicIngredient<Ingredient>
{
    public static final MapCodec<DeterministicItemIngredient> CODEC = DeterministicIngredient.codec(Ingredient.CODEC_NONEMPTY, DeterministicItemIngredient::new);

    public static Ingredient of(Ingredient child, float consumeChance)
    {
        return new Ingredient(new DeterministicItemIngredient(child, consumeChance));
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
        return LimaCoreIngredientTypes.DETERMINISTIC_ITEM.get();
    }
}