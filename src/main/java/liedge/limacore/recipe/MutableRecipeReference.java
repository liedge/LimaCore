package liedge.limacore.recipe;

import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaRecipesUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.function.*;

public final class MutableRecipeReference<T extends Recipe<?>> implements INBTSerializable<StringTag>
{
    private final RecipeType<T> recipeType;

    private @Nullable ResourceLocation id;
    private @Nullable RecipeHolder<T> holder;

    public MutableRecipeReference(RecipeType<T> recipeType)
    {
        this.recipeType = recipeType;
    }

    public MutableRecipeReference(Supplier<? extends RecipeType<T>> typeSupplier)
    {
        this(typeSupplier.get());
    }

    public boolean isPresent()
    {
        return holder != null || id != null;
    }

    public void setHolderValue(@Nullable RecipeHolder<T> holder)
    {
        this.holder = holder;
        this.id = holder != null ? holder.id() : null;
    }

    public @Nullable RecipeHolder<T> getHolderValue(@Nullable Level level)
    {
        if (holder != null)
        {
            return holder;
        }
        else if (id != null && level != null)
        {
            this.holder = LimaRecipesUtil.recipeHolderByKey(level, id, recipeType);
            return this.holder;
        }
        else
        {
            return null;
        }
    }

    public @Nullable T getRecipeValue(@Nullable Level level)
    {
        RecipeHolder<T> holderValue = getHolderValue(level);
        return holderValue != null ? holderValue.value() : null;
    }

    public <U> U mapOrElse(@Nullable Level level, Function<? super T, ? extends U> mapper, U absentValue)
    {
        T val = getRecipeValue(level);
        return val != null ? mapper.apply(val) : absentValue;
    }

    public boolean testValue(@Nullable Level level, Predicate<? super T> predicate)
    {
        T val = getRecipeValue(level);
        return val != null && predicate.test(val);
    }

    public int toIntOrElse(@Nullable Level level, ToIntFunction<? super T> mapper, int absentValue)
    {
        T val = getRecipeValue(level);
        return val != null ? mapper.applyAsInt(val) : absentValue;
    }

    public double toDoubleOrElse(@Nullable Level level, ToDoubleFunction<? super T> mapper, double absentValue)
    {
        T val = getRecipeValue(level);
        return val != null ? mapper.applyAsDouble(val) : absentValue;
    }

    public LimaDataWatcher<String> createDataWatcher()
    {
        return AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.STRING_UTF8, this::serialize, this::deserialize);
    }

    private String serialize()
    {
        return holder != null ? holder.id().toString() : "null";
    }

    private void deserialize(String value)
    {
        id = !value.equals("null") ? ResourceLocation.parse(value) : null;
        holder = null; // Fix: Changing ID should make holder be looked up again.
    }

    @Override
    public StringTag serializeNBT(HolderLookup.Provider provider)
    {
        return StringTag.valueOf(serialize());
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, StringTag nbt)
    {
        deserialize(nbt.getAsString());
    }
}