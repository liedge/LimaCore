package liedge.limacore.transfer;

import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.transfer.InfiniteResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public final class LimaInfiniteResources<T extends Resource> extends InfiniteResourceHandler<T>
{
    public static <T extends Resource> @Nullable LimaInfiniteResources<T> create(T resource)
    {
        return !resource.isEmpty() ? new LimaInfiniteResources<>(resource) : null;
    }

    public static @Nullable LimaInfiniteResources<FluidResource> fromFluidTemplate(@Nullable FluidStackTemplate template)
    {
        return create(FluidResource.of(template));
    }

    public LimaInfiniteResources(T infiniteResource)
    {
        super(infiniteResource);
    }

    @Override
    public int insert(int index, T resource, int amount, TransactionContext transaction)
    {
        Objects.checkIndex(index, size());
        return 0;
    }
}