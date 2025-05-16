package liedge.limacore.client.model.baked;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.client.renderer.LimaCoreRenderTypes;
import liedge.limacore.lib.LimaColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BakedItemLayer extends BakedModelWrapper<BakedModel>
{
    private final List<BakedQuad> quads;
    private final List<BakedModel> self;
    private final RenderType renderType;
    private final RenderType fabulousRenderType;
    private final List<RenderType> renderTypes;
    private final List<RenderType> fabulousRenderTypes;
    private final boolean skipNormalBufferAdd;

    public BakedItemLayer(BakedModel parent, List<BakedQuad> quads, @Nullable RenderType renderType, @Nullable RenderType fabulousRenderType, boolean skipNormalBufferAdd)
    {
        super(parent);
        this.quads = quads;
        this.self = List.of(this);
        this.renderType = renderType != null ? renderType : Sheets.translucentItemSheet();
        this.fabulousRenderType = fabulousRenderType != null ? renderType : Sheets.translucentCullBlockSheet();
        this.renderTypes = List.of(this.renderType);
        this.fabulousRenderTypes = List.of(this.fabulousRenderType);
        this.skipNormalBufferAdd = skipNormalBufferAdd;
    }

    public BakedItemLayer(BakedModel parent, List<BakedQuad> quads, RenderTypeGroup renderTypeGroup)
    {
        this(parent, quads, renderTypeGroup.entity(), renderTypeGroup.entityFabulous(), renderTypeGroup.entity() == LimaCoreRenderTypes.ITEM_POS_TEX_COLOR_SOLID);
    }

    public List<BakedQuad> getQuads()
    {
        return quads;
    }

    /**
     * For use in {@link liedge.limacore.mixin.ItemRendererMixin}. No reason to call this otherwise.
     */
    @ApiStatus.Internal
    public boolean customBufferQuadAddition(PoseStack.Pose pose, VertexConsumer buffer, ItemStack stack, ItemColors colors, List<BakedQuad> quads, int light, int overlay)
    {
        if (!skipNormalBufferAdd) return false;

        for (BakedQuad quad : quads)
        {
            int i = -1;
            if (quad.isTinted()) i = colors.getColor(stack, quad.getTintIndex());

            float red = FastColor.ARGB32.red(i) / 255f;
            float green = FastColor.ARGB32.green(i) / 255f;
            float blue = FastColor.ARGB32.blue(i) / 255f;

            buffer.putBulkData(pose, quad, red, green, blue, 1f, light, overlay);
        }

        return true;
    }

    public void putQuadsInBuffer(PoseStack poseStack, MultiBufferSource bufferSource, float red, float green, float blue, int packedLight)
    {
        RenderType renderType = Minecraft.useShaderTransparency() ? this.renderType : this.fabulousRenderType;
        VertexConsumer buffer = bufferSource.getBuffer(renderType);
        PoseStack.Pose pose = poseStack.last();

        for (BakedQuad quad : quads)
        {
            buffer.putBulkData(pose, quad, red, green, blue, 1f, packedLight, OverlayTexture.NO_OVERLAY);
        }
    }

    public void putQuadsInBuffer(PoseStack poseStack, MultiBufferSource bufferSource, LimaColor tint, int packedLight)
    {
        putQuadsInBuffer(poseStack, bufferSource, tint.red(), tint.green(), tint.blue(), packedLight);
    }

    public void putQuadsInBuffer(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight)
    {
        putQuadsInBuffer(poseStack, bufferSource, 1f, 1f, 1f, packedLight);
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
        return fabulous ? fabulousRenderTypes : renderTypes;
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