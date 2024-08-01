package liedge.limacore.client.model;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import liedge.limacore.LimaCore;
import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SimpleEmissiveGeometry extends EmissiveBlockBenchGeometry<SimpleEmissiveGeometry.GroupData>
{
    public static final ResourceLocation LOADER_ID = LimaCore.RESOURCES.location("simple_emissive");
    public static final LimaGeometryLoader<?> LOADER = new Loader();

    private final BakeType bakeType;

    private SimpleEmissiveGeometry(List<BlockElement> elements, List<GroupData> groups, BakeType bakeType)
    {
        super(elements, groups);
        this.bakeType = bakeType;
    }

    @Override
    protected Builder createBuilder(IGeometryBakingContext ctx, RenderTypeGroup renderTypeGroup, TextureAtlasSprite particleIcon, ItemOverrides overrides)
    {
        if (bakeType == BakeType.ITEM_ONLY)
        {
            return new ItemBuilder(ctx.useAmbientOcclusion(), ctx.isGui3d(), ctx.useBlockLight(), particleIcon, ctx.getTransforms(), overrides, renderTypeGroup);
        }
        else
        {
            boolean b = bakeType == BakeType.BLOCK_AND_ITEM;
            return new BlockBuilder(ctx.useAmbientOcclusion(), ctx.isGui3d(), ctx.useBlockLight(), particleIcon, ctx.getTransforms(), overrides, renderTypeGroup, b);
        }
    }

    private static class BlockBuilder extends Builder
    {
        private final Map<Direction, List<BakedQuad>> culledFaces;
        private final ObjectList<BakedQuad> unculledFaces = new ObjectArrayList<>();
        private final boolean createItemLayers;

        private ObjectList<BakedQuad> nonEmissiveQuads;
        private ObjectList<BakedQuad> emissiveQuads;

        protected BlockBuilder(boolean ambientOcclusion, boolean gui3d, boolean useBlockLight, TextureAtlasSprite particleIcon, ItemTransforms transforms, ItemOverrides overrides, RenderTypeGroup renderTypeGroup, boolean createItemLayers)
        {
            super(ambientOcclusion, gui3d, useBlockLight, particleIcon, transforms, overrides, false, renderTypeGroup);

            this.culledFaces = new EnumMap<>(Direction.class);

            for (Direction side : Direction.values())
            {
                culledFaces.put(side, new ObjectArrayList<>());
            }

            this.createItemLayers = createItemLayers;

            if (createItemLayers)
            {
                nonEmissiveQuads = new ObjectArrayList<>();
                emissiveQuads = new ObjectArrayList<>();
            }
        }

        private void addItemQuads(BakedQuad quad, boolean emissive)
        {
            if (createItemLayers)
            {
                if (emissive)
                {
                    emissiveQuads.add(quad);
                }
                else
                {
                    nonEmissiveQuads.add(quad);
                }
            }
        }

        @Override
        public Builder addCulledFace(Direction facing, BakedQuad quad, boolean emissive)
        {
            culledFaces.get(facing).add(quad);
            addItemQuads(quad, emissive);
            return this;
        }

        @Override
        public Builder addUnculledFace(BakedQuad quad, boolean emissive)
        {
            unculledFaces.add(quad);
            addItemQuads(quad, emissive);
            return this;
        }

        @Override
        public BakedModel build()
        {
            List<BakedQuad> nonEmissiveItemQuads = nonEmissiveQuads != null ? ObjectLists.unmodifiable(nonEmissiveQuads) : null;
            List<BakedQuad> emissiveItemQuads = emissiveQuads != null ? ObjectLists.unmodifiable(emissiveQuads) : null;

            return new BakedBlock(ambientOcclusion, gui3d, useBlockLight, particleIcon, transforms, overrides, false, renderTypeGroup,
                    ObjectLists.unmodifiable(unculledFaces), culledFaces, nonEmissiveItemQuads, emissiveItemQuads);
        }
    }

    private static class ItemBuilder extends Builder
    {
        private final ImmutableList.Builder<BakedQuad> nonEmissiveQuads = new ImmutableList.Builder<>();
        private final ImmutableList.Builder<BakedQuad> emissiveQuads = new ImmutableList.Builder<>();

        protected ItemBuilder(boolean ambientOcclusion, boolean gui3d, boolean useBlockLight, TextureAtlasSprite particleIcon, ItemTransforms transforms, ItemOverrides overrides, RenderTypeGroup renderTypeGroup)
        {
            super(ambientOcclusion, gui3d, useBlockLight, particleIcon, transforms, overrides, false, renderTypeGroup);
        }

        @Override
        public Builder addCulledFace(Direction facing, BakedQuad quad, boolean emissive)
        {
            return addUnculledFace(quad, emissive);
        }

        @Override
        public Builder addUnculledFace(BakedQuad quad, boolean emissive)
        {
            if (emissive)
            {
                emissiveQuads.add(quad);
            }
            else
            {
                nonEmissiveQuads.add(quad);
            }
            return this;
        }

        @Override
        public BakedModel build()
        {
            return new BakedItem(ambientOcclusion, gui3d, useBlockLight, particleIcon, transforms, overrides, false, renderTypeGroup, nonEmissiveQuads.build(), emissiveQuads.build());
        }
    }

    private static class BakedBlock extends LimaBasicBakedModel
    {
        private final Map<Direction, List<BakedQuad>> culledQuads;
        private final List<BakedQuad> unculledQuads;
        private final List<BakedModel> renderPasses;

        private BakedBlock(boolean ambientOcclusion,
                           boolean gui3d,
                           boolean useBlockLight,
                           TextureAtlasSprite particleIcon,
                           ItemTransforms transforms,
                           ItemOverrides overrides,
                           boolean useCustomRenderer,
                           RenderTypeGroup renderTypeGroup,
                           List<BakedQuad> unculledQuads,
                           Map<Direction, List<BakedQuad>> culledQuads,
                           @Nullable List<BakedQuad> nonEmissiveItemQuads,
                           @Nullable List<BakedQuad> emissiveItemQuads)
        {
            super(ambientOcclusion, gui3d, useBlockLight, particleIcon, transforms, overrides, useCustomRenderer, renderTypeGroup);

            this.culledQuads = culledQuads;
            this.unculledQuads = unculledQuads;

            if (nonEmissiveItemQuads != null && emissiveItemQuads != null)
            {
                BakedItemLayer emissiveLayer = new BakedItemLayer(this, emissiveItemQuads, NeoForgeRenderTypes.ITEM_UNSORTED_UNLIT_TRANSLUCENT.get());

                if (!nonEmissiveItemQuads.isEmpty())
                {
                    BakedItemLayer nonEmissiveLayer = new BakedItemLayer(this, nonEmissiveItemQuads, LimaCoreClientUtil.getItemRenderTypeOrDefault(renderTypeGroup));
                    renderPasses = List.of(nonEmissiveLayer, emissiveLayer);
                }
                else
                {
                    renderPasses = List.of(emissiveLayer);
                }
            }
            else
            {
                renderPasses = List.of(this);
            }
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side)
        {
            return (side == null) ? unculledQuads : culledQuads.get(side);
        }

        @Override
        public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous)
        {
            return renderPasses;
        }
    }

    private static class BakedItem extends LimaBasicBakedModel
    {
        private final List<BakedModel> renderPasses;

        private BakedItem(boolean ambientOcclusion,
                          boolean gui3d,
                          boolean useBlockLight,
                          TextureAtlasSprite particleIcon,
                          ItemTransforms transforms,
                          ItemOverrides overrides,
                          boolean useCustomRenderer,
                          RenderTypeGroup renderTypeGroup,
                          List<BakedQuad> nonEmissiveQuads,
                          List<BakedQuad> emissiveQuads)
        {
            super(ambientOcclusion, gui3d, useBlockLight, particleIcon, transforms, overrides, useCustomRenderer, renderTypeGroup);

            BakedItemLayer emissiveLayer = new BakedItemLayer(this, emissiveQuads, NeoForgeRenderTypes.ITEM_UNSORTED_UNLIT_TRANSLUCENT.get());
            if (!nonEmissiveQuads.isEmpty())
            {
                BakedItemLayer nonEmissiveLayer = new BakedItemLayer(this, nonEmissiveQuads, LimaCoreClientUtil.getItemRenderTypeOrDefault(renderTypeGroup));
                this.renderPasses = List.of(nonEmissiveLayer, emissiveLayer);
            }
            else
            {
                this.renderPasses = List.of(emissiveLayer);
            }
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

    private static class Loader extends GeometryLoader<GroupData>
    {
        private Loader() {}

        @Override
        protected List<BlockElement> deserializeElements(List<GroupData> groups, JsonArray elementsArray, JsonObject modelJson, JsonDeserializationContext ctx)
        {
            if (GsonHelper.getAsBoolean(modelJson, "auto_disable_shade", true))
            {
                groups.stream().filter(GroupData::emissive).flatMapToInt(o -> o.elements.intStream()).forEach(e -> elementsArray.get(e).getAsJsonObject().addProperty("shade", false));
            }

            return super.deserializeElements(groups, elementsArray, modelJson, ctx);
        }

        @Override
        protected GroupData deserializeGroup(JsonObject json, IntList groupElements)
        {
            boolean emissive = GsonHelper.getAsBoolean(json, "emissive", false);
            return new GroupData(groupElements, emissive);
        }

        @Override
        protected BlockBenchGroupGeometry<GroupData> createGeometry(JsonObject modelJson, List<BlockElement> elements, List<GroupData> groups)
        {
            BakeType bakeType = BakeType.CODEC.byName(GsonHelper.getAsString(modelJson, "model_type", ""));
            return new SimpleEmissiveGeometry(elements, groups, bakeType);
        }

        @Override
        public ResourceLocation getLoaderId()
        {
            return LOADER_ID;
        }
    }

    private enum BakeType implements StringRepresentable
    {
        BLOCK_ONLY("block_only"),
        ITEM_ONLY("item_only"),
        BLOCK_AND_ITEM("block_and_item");

        private static final LimaEnumCodec<BakeType> CODEC = LimaEnumCodec.createDefaulted(BakeType.class, BLOCK_AND_ITEM);

        private final String name;

        BakeType(String name)
        {
            this.name = name;
        }

        @Override
        public String getSerializedName()
        {
            return name;
        }
    }

    public record GroupData(IntList elements, boolean emissive) implements EmissiveGroupData
    {}
}