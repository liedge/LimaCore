package liedge.limacore.data.generation;

import liedge.limacore.lib.ModResources;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Arrays;
import java.util.function.Supplier;

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

    protected void generated(Supplier<? extends Item> supplier, ResourceLocation texture)
    {
        getBuilder(supplier).parent(generatedModel).texture("layer0", texture);
    }

    protected void generated(Supplier<? extends Item> supplier)
    {
        generated(supplier, itemFolderLocation(supplier));
    }

    @SafeVarargs
    protected final void generated(Supplier<? extends Item>... suppliers)
    {
        Arrays.stream(suppliers).forEach(this::generated);
    }

    protected void handheld(Supplier<? extends Item> supplier, ResourceLocation texture)
    {
        getBuilder(supplier).parent(handheldModel).texture("layer0", texture);
    }

    protected void handheld(Supplier<? extends Item> supplier)
    {
        handheld(supplier, itemFolderLocation(supplier));
    }

    @SafeVarargs
    protected final void handheld(Supplier<? extends Item>... supplier)
    {
        Arrays.stream(supplier).forEach(this::handheld);
    }

    protected void builtInEntity(Supplier<? extends Item> supplier, BlockModel.GuiLight light)
    {
        getBuilder(supplier).parent(builtInEntityModel).guiLight(light);
    }

    protected ItemModelBuilder getBuilder(Supplier<? extends Item> supplier)
    {
        return getBuilder(getItemName(supplier.get()));
    }
}