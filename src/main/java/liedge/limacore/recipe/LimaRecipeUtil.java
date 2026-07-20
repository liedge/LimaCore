package liedge.limacore.recipe;

import liedge.limacore.recipe.result.RecipeResult;
import liedge.limacore.util.LimaStreamsUtil;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.resource.ResourceStack;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public final class LimaRecipeUtil
{
    private LimaRecipeUtil() { }

    public static <T extends Resource> List<ResourceStack<T>> generateResultStacks(RandomSource random, Collection<? extends RecipeResult<?, T>> results)
    {
        return results.stream()
                .map(r -> r.createResource(random))
                .filter(rs -> !rs.isEmpty())
                .collect(LimaStreamsUtil.toObjectList());
    }

    public static <T extends Resource> boolean insertResultStacks(Collection<? extends RecipeResult<?, T>> results, RandomSource random, @Nullable ResourceHandler<T> destination, @Nullable TransactionContext tx)
    {
        try (Transaction sub = Transaction.open(tx))
        {
            for (RecipeResult<?, T> result : results)
            {
                ResourceStack<T> stack = result.createResource(random);
                if (stack.isEmpty()) continue;

                int inserted = ResourceHandlerUtil.insertStacking(destination, stack.resource(), stack.amount(), sub);
                if (result.required() && inserted < stack.amount()) return false;
            }

            sub.commit();
        }

        return true;
    }

    public static <T extends Resource> boolean canInsertResults(@Nullable ResourceHandler<T> destination, Collection<? extends RecipeResult<?, T>> results)
    {
        if (results.isEmpty()) return true;

        try (Transaction tx = Transaction.openRoot())
        {
            for (RecipeResult<?, T> result : results)
            {
                if (!result.required()) continue;

                int required = result.count().max();
                int inserted = ResourceHandlerUtil.insertStacking(destination, result.getResource(), required, tx);

                if (inserted < required) return false;
            }
        }

        return true;
    }
}