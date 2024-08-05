package liedge.limacore.mixin;

import liedge.limacore.client.ItemGuiRenderOverride;
import liedge.limacore.util.LimaCoreUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin
{
    @Inject(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V", at = @At(value = "HEAD"), cancellable = true)
    private void renderItemOverride(@Nullable LivingEntity entity, @Nullable Level level, ItemStack stack, int x, int y, int seed, int guiOffset, CallbackInfo ci)
    {
        ItemGuiRenderOverride extensions = LimaCoreUtil.castOrNull(ItemGuiRenderOverride.class, IClientItemExtensions.of(stack));
        if (extensions != null)
        {
            boolean renderResult = extensions.renderCustomGuiItem((GuiGraphics) (Object) this, stack, x, y);
            if (renderResult) ci.cancel();
        }
    }
}