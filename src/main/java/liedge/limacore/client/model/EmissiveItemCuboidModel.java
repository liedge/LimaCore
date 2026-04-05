package liedge.limacore.client.model;

import com.mojang.datafixers.util.Either;
import liedge.limacore.LimaCore;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.geometry.UnbakedGeometry;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import net.neoforged.neoforge.client.model.StandardModelParameters;
import org.jspecify.annotations.Nullable;

public final class EmissiveItemCuboidModel extends RenderRemapModel
{
    public static final Identifier LOADER_ID = LimaCore.RESOURCES.id("emissive_item");
    public static final Loader<EmissiveItemCuboidModel> LOADER = new Loader<>(0, EmissiveItemCuboidModel::new);

    private EmissiveItemCuboidModel(StandardModelParameters parameters, @Nullable UnbakedGeometry elements, int emissionTarget)
    {
        super(parameters, elements, emissionTarget);
    }

    @Override
    protected RenderRemapGeometry bakeGeometry(Either<Identifier, @Nullable UnbakedGeometry> source, int emissionTarget)
    {
        return new Geometry(source, emissionTarget);
    }

    private record Geometry(Either<Identifier, @Nullable UnbakedGeometry> source, int emissionTarget) implements RenderRemapGeometry
    {
        @Override
        public @Nullable RenderType cutout(Identifier texture)
        {
            return null;
        }

        @Override
        public @Nullable RenderType translucent(Identifier texture)
        {
            return null;
        }

        @Override
        public RenderType cutoutEmissive(Identifier texture)
        {
            return NeoForgeRenderTypes.getItemCutoutUnlit(texture);
        }

        @Override
        public RenderType translucentEmissive(Identifier texture)
        {
            return NeoForgeRenderTypes.getItemTranslucentUnlit(texture);
        }
    }
}