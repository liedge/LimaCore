package liedge.limacore.recipe.ingredient;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.registry.LimaCoreIngredientTypes;
import liedge.limacore.util.LimaStreamsUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class TagWithCountCustomIngredient extends LimaCustomCountIngredient
{
    public static final MapCodec<TagWithCountCustomIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TagKey.codec(Registries.ITEM).fieldOf("tag").forGetter(o -> o.tag))
            .and(commonFields(instance).t1())
            .apply(instance, TagWithCountCustomIngredient::new));

    private final TagKey<Item> tag;
    private final int count;

    private List<ItemStack> stacks;

    TagWithCountCustomIngredient(TagKey<Item> tag, int count)
    {
        this.tag = tag;
        this.count = count;
    }

    @Override
    public int getCount()
    {
        return count;
    }

    @Override
    public boolean testItemOnly(ItemStack stack)
    {
        return stack.is(tag);
    }

    @Override
    public Stream<ItemStack> getItems()
    {
        if (stacks == null)
        {
            stacks = StreamSupport.stream(BuiltInRegistries.ITEM.getTagOrEmpty(tag).spliterator(), false)
                    .map(holder -> new ItemStack(holder, count))
                    .collect(LimaStreamsUtil.toUnmodifiableObjectList());
        }

        return stacks.stream();
    }

    @Override
    public IngredientType<?> getType()
    {
        return LimaCoreIngredientTypes.TAG_WITH_COUNT.get();
    }
}