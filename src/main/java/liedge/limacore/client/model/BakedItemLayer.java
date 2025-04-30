package liedge.limacore.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BakedItemLayer extends BakedModelWrapper<BakedModel>
{
    private final List<BakedQuad> quads;
    private final List<BakedModel> self;
    private final @Nullable List<RenderType> renderTypes;
    private final @Nullable List<RenderType> fabulousRenderTypes;
    private final boolean skipNormalBufferAdd;

    public BakedItemLayer(BakedModel parent, List<BakedQuad> quads, @Nullable RenderType renderType, @Nullable RenderType fabulousRenderType, boolean skipNormalBufferAdd)
    {
        super(parent);
        this.quads = quads;
        this.self = List.of(this);
        this.renderTypes = renderType != null ? List.of(renderType) : null;
        this.fabulousRenderTypes = fabulousRenderType != null ? List.of(fabulousRenderType) : null;
        this.skipNormalBufferAdd = skipNormalBufferAdd;
    }

    public BakedItemLayer(BakedModel parent, List<BakedQuad> quads, RenderTypeGroup renderTypeGroup, boolean skipNormalBufferAdd)
    {
        this(parent, quads, renderTypeGroup.entity(), renderTypeGroup.entityFabulous(), skipNormalBufferAdd);
    }

    public List<BakedQuad> getQuads()
    {
        return quads;
    }

    public boolean addQuadsToBuffer(PoseStack.Pose pose, VertexConsumer buffer, ItemStack stack, ItemColors colors, List<BakedQuad> quads, int light, int overlay)
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
        if (fabulous && fabulousRenderTypes != null) return fabulousRenderTypes;
        else if (!fabulous && renderTypes != null) return renderTypes;
        else return List.of(RenderTypeHelper.getFallbackItemRenderType(itemStack, this, fabulous));
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