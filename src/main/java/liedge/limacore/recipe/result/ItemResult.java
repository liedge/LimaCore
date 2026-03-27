package liedge.limacore.recipe.result;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.data.EmptyFieldMapCodec;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.List;

public record ItemResult(Holder<Item> item, ResultCount count, DataComponentPatch components, String group, boolean required) implements RecipeResult<Item, ItemResource>
{
    public static final Codec<ItemResult> CODEC = RecipeResult.codec(Item.CODEC, ResultCount.codec(Item.DEFAULT_MAX_STACK_SIZE).fieldOf("count"), ItemResult::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemResult> STREAM_CODEC = RecipeResult.streamCodec(Item.STREAM_CODEC, ItemResult::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, List<ItemResult>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());

    public static final String MAP_CODEC_KEY = "item_results";
    public static final MapCodec<List<ItemResult>> LIST_UNIT_MAP_CODEC = EmptyFieldMapCodec.emptyListField(MAP_CODEC_KEY);

    public static MapCodec<List<ItemResult>> listMapCodec(int minInclusive, int maxInclusive)
    {
        return RecipeResult.listCodec(CODEC, MAP_CODEC_KEY, minInclusive, maxInclusive);
    }

    public static ItemResult of(Holder<Item> item, ResultCount count, DataComponentPatch components, String group, boolean required)
    {
        return new ItemResult(item, count, components, group, required);
    }

    public static ItemResult of(Item item, ResultCount count, DataComponentPatch components, String group, boolean required)
    {
        return new ItemResult(LimaRegistryUtil.builtInHolder(item), count, components, group, required);
    }

    public static ItemResult of(Holder<Item> item, ResultCount count, DataComponentPatch components)
    {
        return of(item, count, components, NO_GROUP, true);
    }

    public static ItemResult of(Item item, ResultCount count, DataComponentPatch components)
    {
        return of(LimaRegistryUtil.builtInHolder(item), count, components);
    }

    public static ItemResult of(Holder<Item> item, ResultCount count, String group, boolean required)
    {
        return of(item, count, DataComponentPatch.EMPTY, group, required);
    }

    public static ItemResult of(Item item, ResultCount count, String group, boolean required)
    {
        return of(item, count, DataComponentPatch.EMPTY, group, required);
    }

    public static ItemResult of(Holder<Item> item, ResultCount count)
    {
        return of(item, count, DataComponentPatch.EMPTY);
    }

    public static ItemResult of(Item item, ResultCount count)
    {
        return of(item, count, DataComponentPatch.EMPTY);
    }

    public static ItemResult of(Holder<Item> item, int count)
    {
        return of(item, ResultCount.exactly(count), DataComponentPatch.EMPTY);
    }

    public static ItemResult of(Item item, int count)
    {
        return of(LimaRegistryUtil.builtInHolder(item), count);
    }

    public static ItemResult of(Holder<Item> item)
    {
        return of(item, 1);
    }

    public static ItemResult of(Item item)
    {
        return of(item, 1);
    }

    public static ItemResult fromVanilla(ItemStackTemplate template)
    {
        return of(template.item(), ResultCount.exactly(template.count()), template.components());
    }

    @Override
    public Holder<Item> typeHolder()
    {
        return item;
    }

    @Override
    public ItemResource getResource()
    {
        return ItemResource.of(item, components);
    }

    public ItemStack display(int count)
    {
        if (count <= 0) return ItemStack.EMPTY;
        return new ItemStack(item, count, components);
    }

    public ItemStack display()
    {
        return display(count.max());
    }
}