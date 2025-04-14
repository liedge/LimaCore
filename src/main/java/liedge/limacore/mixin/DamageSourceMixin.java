package liedge.limacore.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import liedge.limacore.lib.damage.LimaCoreDamageComponents;
import liedge.limacore.lib.damage.LimaDamageSourceExtension;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DamageSource.class)
public class DamageSourceMixin implements LimaDamageSourceExtension
{
    @Unique
    private PatchedDataComponentMap limaCore$components;

    @ModifyReturnValue(method = "is(Lnet/minecraft/tags/TagKey;)Z", at = @At(value = "RETURN"))
    private boolean checkDynamicTags(boolean original, @Local(argsOnly = true) TagKey<DamageType> key)
    {
        if (original) return true;
        return this.getSet(LimaCoreDamageComponents.DYNAMIC_TAGS).contains(key);
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public @NotNull PatchedDataComponentMap getModifiableComponents()
    {
        if (limaCore$components == null) limaCore$components = new PatchedDataComponentMap(DataComponentMap.EMPTY);
        return limaCore$components;
    }

    @Override
    public @NotNull DataComponentMap getComponents()
    {
        return limaCore$components == null ? DataComponentMap.EMPTY : limaCore$components;
    }
}