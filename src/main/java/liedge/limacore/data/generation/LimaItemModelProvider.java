package liedge.limacore.data.generation;

import com.google.common.base.Preconditions;
import liedge.limacore.lib.ModResources;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Arrays;

import static liedge.limacore.util.LimaRegistryUtil.getItemName;

public abstract class LimaItemModelProvider extends ItemModelProvider implements ModelProviderHelper
{
    protected final ModelFile generatedModel = new ModelFile.UncheckedModelFile("item/generated");
    protected final ModelFile handheldModel = new ModelFile.UncheckedModelFile("item/handheld");
    protected final ModelFile builtInEntityModel = new ModelFile.UncheckedModelFile("builtin/entity");
    private final ModResources resources;

    protected LimaItemModelProvider(PackOutput output, ModResources resources, ExistingFileHelper existingFileHelper)
    {
        super(output, resources.modid(), existingFileHelper);
        this.resources = resources;
    }

    @Override
    public ModResources modResources()
    {
        return resources;
    }

    @Override
    public ExistingFileHelper fileHelper()
    {
        return this.existingFileHelper;
    }

    protected ItemModelBuilder layersBuilder(ItemModelBuilder builder, ResourceLocation... textures)
    {
        Preconditions.checkArgument(textures.length > 1, "Layers model must contain at least 2 layers");
        for (int i = 0; i < textures.length; i++)
        {
            builder.texture("layer" + i, textures[i]);
        }
        return builder;
    }

    protected void generated(ItemLike itemLike, ResourceLocation texture)
    {
        getBuilder(itemLike).parent(generatedModel).texture("layer0", texture);
    }

    protected void generated(ItemLike itemLike)
    {
        generated(itemLike, itemFolderLocation(itemLike));
    }

    protected void generated(ItemLike... items)
    {
        Arrays.stream(items).forEach(this::generated);
    }

    protected ItemModelBuilder generatedLayers(String path, ResourceLocation... textures)
    {
        return layersBuilder(getBuilder(path).parent(generatedModel), textures);
    }

    protected ItemModelBuilder generatedLayers(ItemLike itemLike, ResourceLocation... textures)
    {
        return layersBuilder(getBuilder(itemLike).parent(generatedModel), textures);
    }

    protected void handheld(ItemLike itemLike, ResourceLocation texture)
    {
        getBuilder(itemLike).parent(handheldModel).texture("layer0", texture);
    }

    protected void handheld(ItemLike itemLike)
    {
        handheld(itemLike, itemFolderLocation(itemLike));
    }

    protected void handheld(ItemLike... items)
    {
        Arrays.stream(items).forEach(this::handheld);
    }

    protected ItemModelBuilder handheldLayers(String path, ResourceLocation... textures)
    {
        return layersBuilder(getBuilder(path).parent(handheldModel), textures);
    }

    protected ItemModelBuilder handheldLayers(ItemLike itemLike, ResourceLocation... textures)
    {
        return layersBuilder(getBuilder(itemLike).parent(handheldModel), textures);
    }

    protected void builtInEntity(ItemLike itemLike, BlockModel.GuiLight light)
    {
        getBuilder(itemLike).parent(builtInEntityModel).guiLight(light);
    }

    protected ItemModelBuilder getBuilder(ItemLike itemLike)
    {
        return getBuilder(getItemName(itemLike.asItem()));
    }
}