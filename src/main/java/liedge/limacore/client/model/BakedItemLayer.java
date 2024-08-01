package liedge.limacore.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BakedItemLayer extends BakedModelWrapper<BakedModel>
{
    private final List<BakedQuad> quads;
    private final List<BakedModel> self;
    private final RenderType renderType;
    private final List<RenderType> renderTypeAsList;

    public BakedItemLayer(BakedModel parent, List<BakedQuad> quads, RenderType renderType)
    {
        super(parent);
        this.quads = quads;
        this.self = List.of(this);
        this.renderType = renderType;
        this.renderTypeAsList = List.of(renderType);
    }

    public void addToBufferDirect(PoseStack poseStack, MultiBufferSource bufferSource, int light)
    {
        VertexConsumer buffer = bufferSource.getBuffer(renderType);
        PoseStack.Pose pose = poseStack.last();
        for (BakedQuad quad : quads)
        {
            buffer.putBulkData(pose, quad, 1f, 1f, 1f, 1f, light, OverlayTexture.NO_OVERLAY);
        }
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand)
    {
        return side == null ? quads : List.of();
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType)
    {
        return side == null ? quads : List.of();
    }

    @Override
    public List<RenderType> getRenderTypes(ItemStack itemStack, boolean fabulous)
    {
        return renderTypeAsList;
    }

    @Override
    public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous)
    {
        return self;
    }

    @Override
    public ItemOverrides getOverrides()
    {
        return ItemOverrides.EMPTY;
    }

    @Override
    public boolean isCustomRenderer()
    {
        return false;
    }
}