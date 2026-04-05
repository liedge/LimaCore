package liedge.limacore.client.model;

import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.standalone.UnbakedStandaloneModel;

public interface IdentityStandaloneModel<T> extends UnbakedStandaloneModel<T>, ModelDebugName
{
    Identifier model();

    default T bake(ModelBaker baker)
    {
        return bake(baker, this);
    }

    @Override
    default String debugName()
    {
        return model().toString();
    }

    @Override
    default void resolveDependencies(Resolver resolver)
    {
        resolver.markDependency(model());
    }
}