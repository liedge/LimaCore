package liedge.limacore.client.model.baked;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import liedge.limacore.lib.LimaColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class BakedItemLayer extends BakedModelWrapper<BakedModel>
{
    public static BakedItemLayer.Builder builder(String name)
    {
        return new Builder(name);
    }

    private final List<BakedQuad> quads;
    private final List<BakedModel> self;
    private final RenderType renderType;
    private final RenderType fabulousRenderType;
    private final List<RenderType> renderTypes;
    private final List<RenderType> fabulousRenderTypes;

    public BakedItemLayer(BakedModel parent, List<BakedQuad> quads, @Nullable RenderType renderType, @Nullable RenderType fabulousRenderType)
    {
        super(parent);
        this.quads = quads;
        this.self = List.of(this);
        this.renderType = renderType != null ? renderType : Sheets.translucentItemSheet();
        this.fabulousRenderType = fabulousRenderType != null ? renderType : Sheets.translucentCullBlockSheet();
        this.renderTypes = List.of(this.renderType);
        this.fabulousRenderTypes = List.of(this.fabulousRenderType);
    }

    public BakedItemLayer(BakedModel parent, List<BakedQuad> quads, RenderTypeGroup renderTypeGroup)
    {
        this(parent, quads, renderTypeGroup.entity(), renderTypeGroup.entityFabulous());
    }

    public List<BakedQuad> getQuads()
    {
        return quads;
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

    public static final class Builder
    {
        private final String name;
        private final ObjectList<BakedQuad> quads;
        private RenderTypeGroup renderTypeGroup = RenderTypeGroup.EMPTY;

        private Builder(String name)
        {
            this.name = name;
            this.quads = new ObjectArrayList<>();
        }

        public String getName()
        {
            return name;
        }

        public void addQuad(BakedQuad quad)
        {
            quads.add(quad);
        }

        public void setRenderTypes(RenderTypeGroup renderTypes)
        {
            this.renderTypeGroup = renderTypes;
        }

        public BakedItemLayer build(ItemLayerBakedModel parent)
        {
            return new BakedItemLayer(parent, ObjectLists.unmodifiable(quads), renderTypeGroup);
        }
    }
}