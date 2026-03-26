package liedge.limacore.mixin;

import liedge.limacore.client.ItemGuiRenderOverride;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsMixin
{
    @Inject(method = "item(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V", at = @At(value = "HEAD"), cancellable = true)
    private void renderItemOverride(@Nullable LivingEntity owner, @Nullable Level level, ItemStack stack, int x, int y, int seed, CallbackInfo ci)
    {
        if (IClientItemExtensions.of(stack) instanceof ItemGuiRenderOverride extensions)
        {
            boolean renderResult = extensions.renderCustomGuiItem((GuiGraphicsExtractor) (Object) this, owner, level, stack, x, y);
            if (renderResult) ci.cancel();
        }
    }
}