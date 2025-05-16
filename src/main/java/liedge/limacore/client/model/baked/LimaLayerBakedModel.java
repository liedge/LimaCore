package liedge.limacore.client.model.baked;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeGroup;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;

import java.util.List;
import java.util.Map;

public class LimaLayerBakedModel extends LimaBasicBakedModel
{
    private final Map<String, BakedItemLayer> layers;
    private final List<BakedModel> renderPasses;

    public LimaLayerBakedModel(boolean ambientOcclusion,
                               boolean gui3d,
                               boolean useBlockLight,
                               TextureAtlasSprite particleIcon,
                               ItemTransforms transforms,
                               ItemOverrides overrides,
                               boolean useCustomRenderer,
                               RenderTypeGroup modelRenderTypes,
                               Map<String, Pair<List<BakedQuad>, RenderTypeGroup>> bakedLayers)
    {
        super(ambientOcclusion, gui3d, useBlockLight, particleIcon, transforms, overrides, useCustomRenderer, modelRenderTypes);

        Object2ObjectMap<String, BakedItemLayer> layers = new Object2ObjectOpenHashMap<>();
        bakedLayers.forEach((name, preBaked) -> layers.put(name, new BakedItemLayer(this, preBaked.getA(), preBaked.getB())));

        this.layers = Object2ObjectMaps.unmodifiable(layers);
        this.renderPasses = useCustomRenderer ? List.of() : List.copyOf(this.layers.values());
    }

    public BakedItemLayer getLayer(String name)
    {
        BakedItemLayer layer = layers.get(name);
        if (layer == null) throw new IllegalArgumentException("Layer '" + name + "' not found in model. Available: " + layers.keySet());
        return layer;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side)
    {
        return List.of();
    }

    @Override
    public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous)
    {
        return renderPasses;
    }
}