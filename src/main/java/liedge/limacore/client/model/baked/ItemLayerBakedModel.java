package liedge.limacore.client.model.baked;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public final class ItemLayerBakedModel extends LimaAbstractBakedModel
{
    private final Map<String, BakedItemLayer> layerMap;
    private final List<BakedModel> renderPasses;

    public ItemLayerBakedModel(boolean ambientOcclusion,
                               boolean gui3d,
                               boolean useBlockLight,
                               TextureAtlasSprite particleIcon,
                               ItemTransforms transforms,
                               ItemOverrides overrides,
                               boolean useCustomRenderer,
                               List<BakedItemLayer.Builder> layerBuilders)
    {
        super(ambientOcclusion, gui3d, useBlockLight, particleIcon, transforms, overrides, useCustomRenderer);

        this.layerMap = new Object2ObjectOpenHashMap<>(layerBuilders.size());
        ObjectList<BakedItemLayer> layers = new ObjectArrayList<>(layerBuilders.size());

        for (BakedItemLayer.Builder builder : layerBuilders)
        {
            BakedItemLayer layer = builder.build(this);
            layerMap.put(builder.getName(), layer);
            layers.add(layer);
        }

        this.renderPasses = useCustomRenderer ? List.of() : ObjectLists.unmodifiable(layers);
    }

    public BakedItemLayer getLayer(String name)
    {
        BakedItemLayer layer = layerMap.get(name);
        if (layer == null) throw new IllegalArgumentException("Layer '" + name + "' not found in model. Available: " + layerMap.keySet());
        return layer;
    }

    @Override
    public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous)
    {
        return renderPasses;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType)
    {
        return List.of();
    }
}