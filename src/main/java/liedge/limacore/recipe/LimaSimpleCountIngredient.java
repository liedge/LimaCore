package liedge.limacore.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.registry.LimaCoreIngredientTypes;
import liedge.limacore.util.LimaCollectionsUtil;
import liedge.limacore.util.LimaCoreUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class LimaSimpleCountIngredient implements ICustomIngredient
{
    public static final MapCodec<LimaSimpleCountIngredient> INGREDIENT_MAP_CODEC = IngredientValue.VALUE_MAP_CODEC.xmap(LimaSimpleCountIngredient::new, i -> i.value);

    public static Ingredient itemValue(ItemStack stack)
    {
        if (stack.getCount() > 1)
        {
            return new Ingredient(new LimaSimpleCountIngredient(new ItemValue(stack)));
        }
        else
        {
            return Ingredient.of(stack);
        }
    }

    public static Ingredient itemValue(ItemLike item, int count)
    {
        return itemValue(new ItemStack(item, count));
    }

    public static Ingredient tagValue(TagKey<Item> itemTag, int count)
    {
        if (count > 1)
        {
            return new Ingredient(new LimaSimpleCountIngredient(new TagValue(itemTag, count)));
        }
        else
        {
            return Ingredient.of(itemTag);
        }
    }

    private final IngredientValue value;

    private LimaSimpleCountIngredient(IngredientValue value)
    {
        this.value = value;
    }

    public int getIngredientCount()
    {
        return value.ingredientCount();
    }

    public boolean matchesItem(ItemStack stack)
    {
        return value.matchesItem(stack);
    }

    @Override
    public boolean test(ItemStack stack)
    {
        return value.matchesItemAndCount(stack);
    }

    @Override
    public Stream<ItemStack> getItems()
    {
        return value.getItems().stream();
    }

    @Override
    public boolean isSimple()
    {
        return true;
    }

    @Override
    public IngredientType<?> getType()
    {
        return LimaCoreIngredientTypes.LIMA_SIMPLE_COUNT_INGREDIENT.get();
    }

    private static class ItemValue implements IngredientValue
    {
        private static final MapCodec<ItemValue> ITEM_MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.ITEM_NON_AIR_CODEC.fieldOf("item").forGetter(o -> o.stack.getItemHolder()),
                ExtraCodecs.intRange(1, 99).optionalFieldOf("count", 1).forGetter(ItemValue::ingredientCount))
                .apply(instance, (holder, count) -> new ItemValue(new ItemStack(holder, count))));

        private final ItemStack stack;
        private final List<ItemStack> list;

        private ItemValue(ItemStack stack)
        {
            this.stack = stack;
            this.list = List.of(stack);
        }

        @Override
        public int ingredientCount()
        {
            return stack.getCount();
        }

        @Override
        public boolean matchesItem(ItemStack stackToTest)
        {
            return stackToTest.is(stack.getItem());
        }

        @Override
        public Collection<ItemStack> getItems()
        {
            return list;
        }
    }

    private static class TagValue implements IngredientValue
    {
        private static final MapCodec<TagValue> TAG_MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                TagKey.codec(Registries.ITEM).fieldOf("tag").forGetter(o -> o.itemTag),
                ExtraCodecs.intRange(1, 99).fieldOf("count").forGetter(o -> o.count))
                .apply(instance, TagValue::new));

        private final TagKey<Item> itemTag;
        private final int count;
        private List<ItemStack> items;

        private TagValue(TagKey<Item> itemTag, int count)
        {
            this.itemTag = itemTag;
            this.count = count;
        }

        @Override
        public int ingredientCount()
        {
            return count;
        }

        @Override
        public boolean matchesItem(ItemStack stackToTest)
        {
            return stackToTest.is(itemTag);
        }

        @Override
        public Collection<ItemStack> getItems()
        {
            if (items == null)
            {
                items = StreamSupport.stream(BuiltInRegistries.ITEM.getTagOrEmpty(itemTag).spliterator(), false)
                        .map(holder -> new ItemStack(holder, count))
                        .collect(LimaCollectionsUtil.toUnmodifiableObjectArrayList());
            }

            return items;
        }
    }

    private interface IngredientValue extends Ingredient.Value
    {
        MapCodec<IngredientValue> VALUE_MAP_CODEC = NeoForgeExtraCodecs.xor(ItemValue.ITEM_MAP_CODEC, TagValue.TAG_MAP_CODEC).xmap(
                either -> either.map(Function.identity(), Function.identity()),
                value -> LimaCoreUtil.eitherFromSubclasses(value, ItemValue.class, TagValue.class));

        int ingredientCount();

        boolean matchesItem(ItemStack stackToTest);

        default boolean matchesItemAndCount(ItemStack stackToTest)
        {
            return matchesItem(stackToTest) && stackToTest.getCount() >= ingredientCount();
        }
    }
}