package liedge.limacore.client.model;

import com.mojang.datafixers.util.Either;
import liedge.limacore.LimaCore;
import liedge.limacore.client.renderer.LimaCoreRenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.geometry.UnbakedGeometry;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.StandardModelParameters;
import org.jspecify.annotations.Nullable;

public final class EmissiveEntityCuboidModel extends RenderRemapModel
{
    public static final Identifier LOADER_ID = LimaCore.RESOURCES.id("emissive_entity");
    public static final Loader<EmissiveEntityCuboidModel> LOADER = new Loader<>(RenderRemapGeometry.NO_LIGHT_REMAP, EmissiveEntityCuboidModel::new);

    private EmissiveEntityCuboidModel(StandardModelParameters parameters, @Nullable UnbakedGeometry elements, int emissionTarget)
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
        public RenderType cutout(Identifier texture)
        {
            return RenderTypes.entityCutoutCull(texture);
        }

        @Override
        public RenderType translucent(Identifier texture)
        {
            return RenderTypes.entityTranslucentCullItemTarget(texture);
        }

        @Override
        public RenderType cutoutEmissive(Identifier texture)
        {
            return LimaCoreRenderTypes.entityCutoutCullEmissive(texture);
        }

        @Override
        public RenderType translucentEmissive(Identifier texture)
        {
            return LimaCoreRenderTypes.entityTranslucentEmissive(texture);
        }
    }
}