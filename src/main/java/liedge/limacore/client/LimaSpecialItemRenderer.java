package liedge.limacore.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public abstract class LimaSpecialItemRenderer<T extends Item> extends BlockEntityWithoutLevelRenderer
{
    public static final float FONT_SCALE = 0.015625f;

    protected LimaSpecialItemRenderer()
    {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public final void onResourceManagerReload(ResourceManager manager)
    {
        T renderableItem = getRenderableItem();
        onResourceManagerReload(manager, renderableItem);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void renderByItem(ItemStack itemStack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay)
    {
        T item = (T) itemStack.getItem();
        float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        renderCustomItem(itemStack, item, displayContext, poseStack, bufferSource, partialTick, light, overlay);
    }

    protected abstract T getRenderableItem();

    protected abstract void onResourceManagerReload(ResourceManager manager, T item);

    protected abstract void renderCustomItem(ItemStack itemStack, T item, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, int light, int overlay);

    protected EntityModelSet getEntityModels()
    {
        return Minecraft.getInstance().getEntityModels();
    }

    protected boolean isRightHanded(ItemDisplayContext displayContext)
    {
        return displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
    }

    protected boolean isLeftHanded(ItemDisplayContext displayContext)
    {
        return displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
    }

    protected boolean isFirstPersonMainHand(ItemDisplayContext displayContext)
    {
        if (displayContext.firstPerson())
        {
            if (Minecraft.getInstance().options.mainHand().get().getId() == 0)
            {
                return displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
            }
            else
            {
                return displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
            }
        }

        return false;
    }

    protected boolean isFirstPersonOffHand(ItemDisplayContext displayContext)
    {
        if (displayContext.firstPerson())
        {
            if (Minecraft.getInstance().options.mainHand().get().getOpposite().getId() == 0)
            {
                return displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
            }
            else
            {
                return displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
            }
        }

        return false;
    }
}