package liedge.limacore.world.generation;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.Objects;

public record LimaPlacementType<T extends PlacementModifier>(Identifier id, MapCodec<T> codec) implements PlacementModifierType<T>
{
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof LimaPlacementType<?> that)) return false;
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
        return "PlacementType[" + id + "]";
    }
}