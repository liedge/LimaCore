package liedge.limacore.recipe.ingredient;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;

public abstract class LimaCustomCountIngredient implements ICustomIngredient
{
    protected static <T extends LimaCustomCountIngredient> Products.P1<RecordCodecBuilder.Mu<T>, Integer> commonFields(RecordCodecBuilder.Instance<T> instance)
    {
        return instance.group(ExtraCodecs.intRange(1, 99).optionalFieldOf("count", 1).forGetter(LimaCustomCountIngredient::getCount));
    }

    public static Ingredient of(ItemStack stack)
    {
        if (stack.getCount() > 1)
        {
            return new Ingredient(new ItemWithCountCustomIngredient(stack));
        }
        else
        {
            return Ingredient.of(stack);
        }
    }

    public static Ingredient of(ItemLike item, int count)
    {
        return of(new ItemStack(item, count));
    }

    public static Ingredient of(TagKey<Item> tagKey, int count)
    {
        if (count > 1)
        {
            return new Ingredient(new TagWithCountCustomIngredient(tagKey, count));
        }
        else
        {
            return Ingredient.of(tagKey);
        }
    }

    public abstract int getCount();

    public abstract boolean testItemOnly(ItemStack stack);

    @Override
    public final boolean test(ItemStack stack)
    {
        return testItemOnly(stack) && stack.getCount() >= this.getCount();
    }

    @Override
    public final boolean isSimple()
    {
        return true;
    }
}