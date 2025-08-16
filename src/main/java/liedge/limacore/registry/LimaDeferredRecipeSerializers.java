package liedge.limacore.registry;

import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.LimaRecipeSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public class LimaDeferredRecipeSerializers extends DeferredRegister<RecipeSerializer<?>>
{
    public static LimaDeferredRecipeSerializers create(String namespace)
    {
        return new LimaDeferredRecipeSerializers(namespace);
    }

    private LimaDeferredRecipeSerializers(String namespace)
    {
        super(Registries.RECIPE_SERIALIZER, namespace);
    }

    public <R extends LimaCustomRecipe<?>> DeferredHolder<RecipeSerializer<?>, LimaRecipeSerializer<R>> registerSerializer(String name, LimaCustomRecipe.RecipeFactory<R> factory, UnaryOperator<LimaRecipeSerializer.Builder<R>> builderOp)
    {
        return register(name, id -> builderOp.apply(LimaRecipeSerializer.builder(factory)).build(id));
    }
}