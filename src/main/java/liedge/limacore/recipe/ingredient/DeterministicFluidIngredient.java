package liedge.limacore.recipe.ingredient;

import com.mojang.serialization.MapCodec;
import liedge.limacore.registry.game.LimaCoreIngredientTypes;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredientType;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public final class DeterministicFluidIngredient extends FluidIngredient implements DeterministicIngredient<FluidIngredient>
{
    public static final MapCodec<DeterministicFluidIngredient> CODEC = DeterministicIngredient.codec(FluidIngredient.CODEC, DeterministicFluidIngredient::new);

    private final FluidIngredient child;
    private final float consumeChance;

    public DeterministicFluidIngredient(FluidIngredient child, float consumeChance)
    {
        this.child = child;
        this.consumeChance = consumeChance;
    }

    @Override
    public FluidIngredient child()
    {
        return child;
    }

    @Override
    public float consumeChance()
    {
        return consumeChance;
    }

    @Override
    public boolean test(FluidStack fluidStack)
    {
        return child.test(fluidStack);
    }

    @Override
    protected Stream<FluidStack> generateStacks()
    {
        return Arrays.stream(child.getStacks());
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }

    @Override
    public FluidIngredientType<?> getType()
    {
        return LimaCoreIngredientTypes.DETERMINISTIC_FLUID.get();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(child, consumeChance);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        else if (!(obj instanceof DeterministicFluidIngredient other)) return false;
        else
        {
            return this.child.equals(other.child) && this.consumeChance == other.consumeChance;
        }
    }
}