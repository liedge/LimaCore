package liedge.limacore.mixin;

import liedge.limacore.client.renderer.LimaCoreRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.event.RegisterNamedRenderTypesEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(RegisterNamedRenderTypesEvent.class)
public abstract class RegisterNamedRenderTypesEventMixin
{
    @Shadow
    @Final
    private Map<ResourceLocation, RenderTypeGroup> renderTypes;

    @Inject(method = "register(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/renderer/RenderType;Lnet/minecraft/client/renderer/RenderType;)V", at = @At(value = "HEAD"), cancellable = true)
    private void forceRegisterRenderType(ResourceLocation key, RenderType blockRenderType, RenderType entityRenderType, CallbackInfo ci)
    {
        if (key.equals(LimaCoreRenderTypes.EMISSIVE_SOLID_ITEM_NAME))
        {
            renderTypes.put(key, new RenderTypeGroup(blockRenderType, entityRenderType, entityRenderType));
            ci.cancel();
        }
    }
}