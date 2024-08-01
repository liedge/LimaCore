package liedge.limacore.recipe;

import liedge.limacore.util.LimaCoreUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

public final class LimaRecipeReference<T extends Recipe<?>> implements INBTSerializable<StringTag>
{
    public static <R extends Recipe<?>> AttachmentType.Builder<LimaRecipeReference<R>> attachmentBuilder(RecipeType<R> recipeType)
    {
        return AttachmentType.serializable(() -> new LimaRecipeReference<>(recipeType));
    }

    public static <R extends Recipe<?>> AttachmentType.Builder<LimaRecipeReference<R>> attachmentBuilder(Supplier<RecipeType<R>> typeSupplier)
    {
        return AttachmentType.serializable(() -> new LimaRecipeReference<>(typeSupplier));
    }

    private final RecipeType<T> recipeType;

    private @Nullable ResourceLocation id;
    private @Nullable RecipeHolder<T> holder;

    public LimaRecipeReference(RecipeType<T> recipeType)
    {
        this.recipeType = recipeType;
    }

    public LimaRecipeReference(Supplier<RecipeType<T>> typeSupplier)
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
            this.holder = LimaCoreUtil.getRecipeByKey(level, id, recipeType);
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

    @Override
    public StringTag serializeNBT(HolderLookup.Provider provider)
    {
        return holder != null ? StringTag.valueOf(holder.id().toString()) : StringTag.valueOf("null");
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, StringTag nbt)
    {
        String val = nbt.getAsString();
        if (val.equals("null"))
        {
            id = null;
        }
        else
        {
            id = ResourceLocation.parse(val);
        }
    }
}