package liedge.limacore.world.generation;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;

import java.util.Objects;

public record LimaBlockPredicateType<T extends BlockPredicate>(ResourceLocation id, MapCodec<T> codec) implements BlockPredicateType<T>
{
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof LimaBlockPredicateType<?> that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(id);
    }

    @Override
    public String toString()
    {
        return "BlockPredicateType[" + id + "]";
    }
}