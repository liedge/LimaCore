package liedge.limacore.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

public interface LimaCreativeTabStacksProvider
{
    void generateCreativeTabStacks(ResourceLocation tabId, CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output);

    default boolean addDefaultItemStack()
    {
        return true;
    }
}