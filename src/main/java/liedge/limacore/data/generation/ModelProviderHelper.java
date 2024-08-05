package liedge.limacore.data.generation;

import liedge.limacore.lib.ModResources;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import static liedge.limacore.util.LimaRegistryUtil.getBlockId;
import static liedge.limacore.util.LimaRegistryUtil.getItemId;

public interface ModelProviderHelper
{
    ModResources modResources();

    ExistingFileHelper fileHelper();

    //#region Model file helper factories
    default ModelFile existingModel(ResourceLocation location)
    {
        return new ModelFile.ExistingModelFile(location, fileHelper());
    }

    default ModelFile uncheckedBlockModel(String path)
    {
        return new ModelFile.UncheckedModelFile(blockFolderLocation(path));
    }

    default ModelFile uncheckedItemModel(String path)
    {
        return new ModelFile.UncheckedModelFile(itemFolderLocation(path));
    }
    //#endregion

    //#region Item and block model resource location factories
    default ResourceLocation blockFolderLocation(ModResources resources, String path)
    {
        return resources.location(blockFolderPath(path));
    }

    default ResourceLocation blockFolderLocation(String path)
    {
        return blockFolderLocation(modResources(), path);
    }

    default ResourceLocation blockFolderLocation(Block block)
    {
        ResourceLocation id = getBlockId(block);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), blockFolderPath(id.getPath()));
    }

    default ResourceLocation blockFolderLocation(Holder<Block> holder)
    {
        return blockFolderLocation(holder.value());
    }

    default String blockFolderPath(String path)
    {
        return ModelProvider.BLOCK_FOLDER + '/' + path;
    }

    default ResourceLocation itemFolderLocation(ModResources resources, String path)
    {
        return resources.location(itemFolderPath(path));
    }

    default ResourceLocation itemFolderLocation(String path)
    {
        return itemFolderLocation(modResources(), path);
    }

    default ResourceLocation itemFolderLocation(ItemLike item)
    {
        ResourceLocation id = getItemId(item.asItem());
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), itemFolderPath(id.getPath()));
    }

    default String itemFolderPath(String path)
    {
        return ModelProvider.ITEM_FOLDER + '/' + path;
    }
    //#endregion
}