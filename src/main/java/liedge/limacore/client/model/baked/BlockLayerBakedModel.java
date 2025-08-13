package liedge.limacore.client.model.baked;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import liedge.limacore.client.model.geometry.ElementGroupGeometry;
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
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public final class BlockLayerBakedModel extends LimaAbstractBakedModel
{
    private final BlockQuadsGroup masterQuadsGroup;
    private final Map<RenderType, BlockQuadsGroup> byRenderType;
    private final ChunkRenderTypeSet blockRenderTypes;
    private final List<BakedModel> itemRenderPasses;

    public BlockLayerBakedModel(boolean ambientOcclusion,
                                boolean gui3d,
                                boolean useBlockLight,
                                TextureAtlasSprite particleIcon,
                                ItemTransforms transforms,
                                ItemOverrides overrides,
                                List<BakedQuad> normalItemQuads,
                                List<BakedQuad> emissiveItemQuads,
                                BlockQuadsGroup.Builder masterBuilder,
                                Map<RenderType, BlockQuadsGroup.Builder> blockBuilders)
    {
        super(ambientOcclusion, gui3d, useBlockLight, particleIcon, transforms, overrides, false);

        this.blockRenderTypes = ChunkRenderTypeSet.of(blockBuilders.keySet());

        Object2ObjectMap<RenderType, BlockQuadsGroup> map = new Object2ObjectOpenHashMap<>();
        blockBuilders.forEach((renderType, builder) -> map.put(renderType, builder.build()));
        this.byRenderType = Object2ObjectMaps.unmodifiable(map);
        this.masterQuadsGroup = masterBuilder.build();

        BakedItemLayer normalLayer = new BakedItemLayer(this, normalItemQuads, RenderTypeGroup.EMPTY);
        BakedItemLayer emissiveLayer = new BakedItemLayer(this, emissiveItemQuads, ElementGroupGeometry.customEmissiveRenderTypes());
        this.itemRenderPasses = List.of(normalLayer, emissiveLayer);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType)
    {
        return renderType == null ? masterQuadsGroup.getQuads(side) : byRenderType.get(renderType).getQuads(side);
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data)
    {
        return blockRenderTypes;
    }

    @Override
    public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous)
    {
        return itemRenderPasses;
    }
}