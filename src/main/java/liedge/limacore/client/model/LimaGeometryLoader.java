package liedge.limacore.client.model;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

public interface LimaGeometryLoader<T extends IUnbakedGeometry<T>> extends IGeometryLoader<T>
{
    ResourceLocation getLoaderId();

    default void registerLoader(final ModelEvent.RegisterGeometryLoaders event)
    {
        event.register(getLoaderId(), this);
    }
}