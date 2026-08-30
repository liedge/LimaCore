package liedge.limacore.client.model;

import com.mojang.math.Transformation;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.client.renderer.LimaCoreRenderTypes;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4fc;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public record TranslucentLastModel(Identifier model, Optional<Transformation> transformation, List<ItemTintSource> tints) implements ItemModel.Unbaked
{
    public static final MapCodec<TranslucentLastModel> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Identifier.CODEC.fieldOf("model").forGetter(TranslucentLastModel::model),
            Transformation.EXTENDED_CODEC.optionalFieldOf("transformation").forGetter(TranslucentLastModel::transformation),
            ItemTintSources.CODEC.listOf().optionalFieldOf("tints", List.of()).forGetter(TranslucentLastModel::tints))
            .apply(i, TranslucentLastModel::new));

    @Override
    public ItemModel bake(ItemModel.BakingContext context, Matrix4fc transformation)
    {
        ModelBaker baker = context.blockModelBaker();
        ResolvedModel resolvedModel = baker.getModel(model);
        TextureSlots textureSlots = resolvedModel.getTopTextureSlots();
        ModelRenderProperties properties = ModelRenderProperties.fromResolvedModel(baker, resolvedModel, textureSlots);
        Matrix4fc modelTransform = Transformation.compose(transformation, this.transformation);

        QuadCollection.Builder builder = new QuadCollection.Builder();
        resolvedModel.bakeTopGeometry(textureSlots, baker, BlockModelRotation.IDENTITY).getAll().stream()
                .sorted(Comparator.comparing(quad -> quad.materialInfo().itemRenderType(), LimaCoreRenderTypes.BLENDS_LAST))
                .forEach(builder::addUnculledFace);

        return new CuboidItemModelWrapper(tints, builder.build(), properties, modelTransform);
    }

    @Override
    public void resolveDependencies(Resolver resolver)
    {
        resolver.markDependency(model);
    }

    @Override
    public MapCodec<? extends ItemModel.Unbaked> type()
    {
        return CODEC;
    }
}