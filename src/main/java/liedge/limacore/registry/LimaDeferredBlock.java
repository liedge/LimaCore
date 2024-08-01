package liedge.limacore.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class LimaDeferredBlock<B extends Block, I> extends DeferredBlock<B>
{
    private @Nullable Holder<Item> blockItemHolder;

    LimaDeferredBlock(ResourceLocation id)
    {
        super(ResourceKey.create(Registries.BLOCK, id));
    }

    public Holder<Item> getBlockItemHolder()
    {
        bindItem();
        return Objects.requireNonNull(blockItemHolder, "Block '" + getId() + " does not a have block item registration.");
    }

    @SuppressWarnings("unchecked")
    public @Nullable I getBlockItem()
    {
        bindItem();
        if (blockItemHolder != null)
        {
            return (I) blockItemHolder.value();
        }
        else
        {
            return null;
        }
    }

    public I getBlockItemOrThrow()
    {
        return Objects.requireNonNull(getBlockItem(), "Block '" + getId() + "' does not have a block item registration.");
    }

    void bindBlock()
    {
        this.bind(false);
    }

    void bindItem()
    {
        if (blockItemHolder == null)
        {
            Registry<Item> itemRegistry = BuiltInRegistries.ITEM;
            blockItemHolder = itemRegistry.getHolder(ResourceKey.create(Registries.ITEM, getId())).orElse(null);
        }
    }
}