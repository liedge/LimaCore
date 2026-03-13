package liedge.limacore.recipe;

import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

public record SimpleResourceInput(@Nullable ResourceHandler<ItemResource> items, @Nullable ResourceHandler<FluidResource> fluids) implements LimaRecipeInput
{ }