package liedge.limacore.recipe;

import liedge.limacore.LimaCore;
import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaRecipesUtil;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public final class LimaRecipeCheck<I extends RecipeInput, R extends Recipe<I>> implements RecipeManager.CachedCheck<I, R>, INBTSerializable<StringTag>
{
    private static final String NULL_MARKER = "null";

    public static <I extends RecipeInput, R extends Recipe<I>> LimaRecipeCheck<I, R> create(RecipeType<R> type)
    {
        return new LimaRecipeCheck<>(type);
    }

    public static <I extends RecipeInput, R extends Recipe<I>> LimaRecipeCheck<I, R> create(Supplier<? extends RecipeType<R>> typeSupplier)
    {
        return new LimaRecipeCheck<>(typeSupplier.get());
    }

    private final RecipeType<R> recipeType;

    private @Nullable ResourceLocation lastUsedId;

    private LimaRecipeCheck(RecipeType<R> recipeType)
    {
        this.recipeType = recipeType;
    }

    public RecipeType<R> getRecipeType()
    {
        return recipeType;
    }

    @Override
    public Optional<RecipeHolder<R>> getRecipeFor(I input, Level level)
    {
        Optional<RecipeHolder<R>> optional = level.getRecipeManager().getRecipeFor(recipeType, input, level, lastUsedId);
        optional.ifPresent(holder -> this.lastUsedId = holder.id());

        return optional;
    }

    public Optional<RecipeHolder<R>> getLastUsedRecipe(@Nullable Level level)
    {
        if (level == null || lastUsedId == null) return Optional.empty();
        return LimaRecipesUtil.getRecipeById(level, lastUsedId, recipeType);
    }

    public void setLastUsedRecipe(@Nullable RecipeHolder<R> recipe)
    {
        lastUsedId = recipe != null ? recipe.id() : null;
    }

    public LimaDataWatcher<String> createDataWatcher()
    {
        return AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.STRING_UTF8, this::serialize, this::deserialize);
    }

    // Serialization stuff
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

    private String serialize()
    {
        return lastUsedId != null ? lastUsedId.toString() : NULL_MARKER;
    }

    private void deserialize(String value)
    {
        try
        {
            lastUsedId = !value.equals(NULL_MARKER) ? ResourceLocation.parse(value) : null;
        }
        catch (ResourceLocationException ex)
        {
            LimaCore.LOGGER.warn("Tried to deserialize invalid recipe ID '{}'.", value);
            lastUsedId = null;
        }
    }
}