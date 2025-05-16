package liedge.limacore.client.model.geometry;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.LimaCore;
import liedge.limacore.client.model.baked.BakedItemLayer;
import liedge.limacore.client.model.baked.BakedQuadCollection;
import liedge.limacore.client.model.baked.LimaBasicBakedModel;
import liedge.limacore.util.LimaJsonUtil;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.ExtraFaceData;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class SimpleEmissiveGeometry extends ElementGroupGeometry
{
    public static final ResourceLocation LOADER_ID = LimaCore.RESOURCES.location("simple_emissive");
    public static final LimaGeometryLoader<?> LOADER = new Loader();

    private final int emissivity;
    private final IntSet emissiveElements;

    private SimpleEmissiveGeometry(List<BlockElement> elements, Map<String, IntList> elementGroups, int emissivity, IntSet emissiveElements)
    {
        super(elements, elementGroups);
        this.emissivity = emissivity;
        this.emissiveElements = emissiveElements;
    }

    @Override
    protected BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, TextureAtlasSprite particleIcon, RenderTypeGroup modelRenderTypes, List<BlockElement> elements, Map<String, IntList> elementGroups)
    {
        BakedQuadCollection.Builder blockBuilder = BakedQuadCollection.builder();
        List<BakedQuad> baseQuads = new ObjectArrayList<>();
        List<BakedQuad> emissiveQuads = new ObjectArrayList<>();

        for (int i = 0; i < elements.size(); i++)
        {
            BlockElement element = elements.get(i);
            for (Direction side : element.faces.keySet())
            {
                BlockElementFace face = element.faces.get(side);
                TextureAtlasSprite sprite = spriteGetter.apply(context.getMaterial(face.texture()));
                BakedQuad quad = BlockModel.bakeFace(element, face, sprite, side, modelState);

                blockBuilder.addFace(face.cullForDirection(), quad);

                if (emissiveElements.contains(i))
                {
                    QuadTransformers.settingEmissivity(emissivity).processInPlace(quad);
                    emissiveQuads.add(quad);
                }
                else
                {
                    baseQuads.add(quad);
                }
            }
        }

        return new Baked(context.useAmbientOcclusion(), context.isGui3d(), context.useBlockLight(), particleIcon, context.getTransforms(), overrides, modelRenderTypes, blockBuilder.build(), baseQuads, emissiveQuads);
    }

    private static class Baked extends LimaBasicBakedModel
    {
        private final Map<Direction, List<BakedQuad>> culledFaces;
        private final List<BakedQuad> unculledFaces;
        private final List<BakedModel> renderPasses;

        private Baked(boolean ambientOcclusion,
                      boolean gui3d,
                      boolean useBlockLight,
                      TextureAtlasSprite particleIcon,
                      ItemTransforms transforms,
                      ItemOverrides overrides,
                      RenderTypeGroup modelRenderTypes,
                      BakedQuadCollection blockQuads,
                      List<BakedQuad> baseQuads,
                      List<BakedQuad> emissiveQuads)
        {
            super(ambientOcclusion, gui3d, useBlockLight, particleIcon, transforms, overrides, false, modelRenderTypes);

            this.culledFaces = blockQuads.getCulledFaces();
            this.unculledFaces = blockQuads.getUnculledFaces();

            BakedItemLayer baseLayer = new BakedItemLayer(this, baseQuads, modelRenderTypes);
            BakedItemLayer emissiveLayer = new BakedItemLayer(this, emissiveQuads, ElementGroupGeometry.customEmissiveRenderTypes());

            this.renderPasses = List.of(baseLayer, emissiveLayer);
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side)
        {
            return side == null ? unculledFaces : culledFaces.get(side);
        }

        @Override
        public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous)
        {
            return renderPasses;
        }
    }

    private static class Loader extends GeometryLoader
    {
        private Loader() {}

        @Override
        public ResourceLocation getLoaderId()
        {
            return LOADER_ID;
        }

        @Override
        protected ElementGroupGeometry createGeometry(JsonObject rootJson, List<BlockElement> elements, Map<String, IntList> elementGroups) throws JsonParseException
        {
            IntSet emissiveElements = LimaJsonUtil.stringsArrayStream(GsonHelper.getAsJsonArray(rootJson, "emissive_groups"))
                    .filter(elementGroups::containsKey)
                    .flatMapToInt(o -> elementGroups.get(o).intStream())
                    .collect(IntOpenHashSet::new, IntSet::add, IntSet::addAll);

            for (int elementIndex : emissiveElements)
            {
                BlockElement element = elements.get(elementIndex);
                @SuppressWarnings("DataFlowIssue") ExtraFaceData faceData = Objects.requireNonNullElse(element.getFaceData(), ExtraFaceData.DEFAULT); // Not actually non-null, explicitly use NeoForge's fallback
                elements.set(elementIndex, new BlockElement(element.from, element.to, element.faces, element.rotation, false, faceData));
            }

            int emissivity = GsonHelper.getAsInt(rootJson, "emissivity", 15);
            return new SimpleEmissiveGeometry(elements, elementGroups, emissivity, emissiveElements);
        }
    }
}