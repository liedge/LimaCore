package liedge.limacore.client.model;

import com.google.common.base.Suppliers;
import com.mojang.math.Transformation;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import liedge.limacore.client.renderer.LimaSpecialModelRenderer;
import liedge.limacore.client.util.LimaModelsUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public final class LimaSpecialModelWrapper<T> implements ItemModel
{
    private final LimaSpecialModelRenderer<T> specialRenderer;
    private final ModelRenderProperties properties;
    private final Supplier<Vector3fc[]> extents;
    private final Matrix4fc transformation;

    public LimaSpecialModelWrapper(LimaSpecialModelRenderer<T> specialRenderer, ModelRenderProperties properties, Matrix4fc transformation)
    {
        this.specialRenderer = specialRenderer;
        this.properties = properties;
        this.extents = Suppliers.memoize(() -> {
            Set<Vector3fc> results = new ObjectOpenHashSet<>();
            specialRenderer.getExtents(results::add);
            return results.toArray(Vector3fc[]::new);
        });
        this.transformation = transformation;
    }

    @Override
    public void update(ItemStackRenderState output, ItemStack item, ItemModelResolver resolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed)
    {
        output.appendModelIdentityElement(this);
        ItemStackRenderState.LayerRenderState layer = output.newLayer();

        if (item.hasFoil())
        {
            ItemStackRenderState.FoilType foilType = ItemStackRenderState.FoilType.STANDARD;
            layer.setFoilType(foilType);
            output.setAnimated();
            output.appendModelIdentityElement(foilType);
        }

        T argument = specialRenderer.extractArgument(item, displayContext, level, owner);
        layer.setExtents(extents);
        layer.setLocalTransform(transformation);
        layer.setupSpecialModel(specialRenderer, argument);
        if (argument != null) output.appendModelIdentityElement(argument);

        properties.applyToLayer(layer, displayContext);
    }

    public record Unbaked(Identifier base, Optional<Transformation> transformation, LimaSpecialModelRenderer.LimaUnbaked<?> specialModel) implements ItemModel.Unbaked
    {
        public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Identifier.CODEC.fieldOf("base").forGetter(Unbaked::base),
                Transformation.EXTENDED_CODEC.optionalFieldOf("transformation").forGetter(Unbaked::transformation),
                LimaSpecialModelRenderer.LimaUnbaked.CODEC.fieldOf("renderer").forGetter(Unbaked::specialModel))
                .apply(i, Unbaked::new));

        @Override
        public ItemModel bake(BakingContext context, Matrix4fc transformation)
        {
            Matrix4fc modelTransform = Transformation.compose(transformation, this.transformation);

            LimaSpecialModelRenderer<?> renderer = specialModel.bake(context);
            if (renderer != null)
            {
                ModelRenderProperties properties = LimaModelsUtil.resolveProperties(context.blockModelBaker(), base);
                return new LimaSpecialModelWrapper<>(renderer, properties, modelTransform);
            }
            else
            {
                return context.missingItemModel(modelTransform);
            }
        }

        @Override
        public void resolveDependencies(Resolver resolver)
        {
            resolver.markDependency(base);
        }

        @Override
        public MapCodec<? extends ItemModel.Unbaked> type()
        {
            return CODEC;
        }
    }
}