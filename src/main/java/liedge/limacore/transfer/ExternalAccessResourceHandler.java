package liedge.limacore.transfer;

import liedge.limacore.blockentity.IOAccess;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

import java.util.function.IntSupplier;

public class ExternalAccessResourceHandler<T extends Resource> extends LimitingResourceHandler<T>
{
    private final IOAccess topLevelAccess;
    private final ResourceLevelAccess<T> resourceLevelAccess;

    public ExternalAccessResourceHandler(ResourceHandler<T> delegate, IntSupplier transferLimit, IOAccess topLevelAccess, @Nullable ResourceLevelAccess<T> resourceLevelAccess)
    {
        super(delegate, transferLimit);
        this.topLevelAccess = topLevelAccess;
        this.resourceLevelAccess = resourceLevelAccess != null ? resourceLevelAccess : (_, _) -> topLevelAccess;
    }

    public ExternalAccessResourceHandler(ResourceHandler<T> delegate, int transferLimit, IOAccess topLevelAccess, @Nullable ResourceLevelAccess<T> resourceLevelAccess)
    {
        this(delegate, () -> transferLimit, topLevelAccess, resourceLevelAccess);
    }

    @Override
    public int insert(int index, T resource, int amount, TransactionContext transaction)
    {
        if (topLevelAccess.allowsInput() && resourceLevelAccess.get(index, resource).allowsInput())
        {
            return super.insert(index, resource, amount, transaction);
        }
        else
        {
            return 0;
        }
    }

    @Override
    public int extract(int index, T resource, int amount, TransactionContext transaction)
    {
        if (topLevelAccess.allowsOutput() && resourceLevelAccess.get(index, resource).allowsOutput())
        {
            return super.extract(index, resource, amount, transaction);
        }
        else
        {
            return 0;
        }
    }

    @FunctionalInterface
    public interface ResourceLevelAccess<T extends Resource>
    {
        IOAccess get(int index, T resource);
    }
}