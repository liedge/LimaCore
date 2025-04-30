package liedge.limacore.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.client.model.BakedItemLayer;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin
{
    @Shadow @Final private ItemColors itemColors;

    @WrapWithCondition(method = "renderModelLists", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderQuadList(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Ljava/util/List;Lnet/minecraft/world/item/ItemStack;II)V", ordinal = 1))
    private boolean renderEmissiveItemLayer(ItemRenderer instance, PoseStack poseStack, VertexConsumer consumer, List<BakedQuad> quads, ItemStack stack, int light, int overlay, @Local(argsOnly = true) BakedModel model)
    {
        if (!stack.isEmpty() && model instanceof BakedItemLayer layer)
        {
            return !layer.addQuadsToBuffer(poseStack.last(), consumer, stack, itemColors, quads, light, overlay);
        }

        return true;
    }
}