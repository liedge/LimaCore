package liedge.limacore.recipe.ingredient;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.registry.LimaCoreIngredientTypes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.List;
import java.util.stream.Stream;

public final class ItemWithCountCustomIngredient extends LimaCustomCountIngredient
{
    public static final MapCodec<ItemWithCountCustomIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance)
            .and(ItemStack.ITEM_NON_AIR_CODEC.fieldOf("item").forGetter(o -> o.ingredientStack.getItemHolder()))
            .apply(instance, (count, holder) -> new ItemWithCountCustomIngredient(new ItemStack(holder, count))));

    private final ItemStack ingredientStack;
    private final List<ItemStack> asList;

    ItemWithCountCustomIngredient(ItemStack ingredientStack)
    {
        this.ingredientStack = ingredientStack;
        this.asList = List.of(ingredientStack);
    }

    @Override
    public int getCount()
    {
        return ingredientStack.getCount();
    }

    @Override
    public boolean testItemOnly(ItemStack stack)
    {
        return stack.is(ingredientStack.getItem());
    }

    @Override
    public Stream<ItemStack> getItems()
    {
        return asList.stream();
    }

    @Override
    public IngredientType<?> getType()
    {
        return LimaCoreIngredientTypes.ITEM_WITH_COUNT.get();
    }
}