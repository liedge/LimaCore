package liedge.limacore.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import liedge.limacore.lib.damage.LimaDamageSourceExtension;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Mixin(DamageSource.class)
public class DamageSourceMixin implements LimaDamageSourceExtension
{
    @Unique
    private Set<TagKey<DamageType>> limaCore$dynamicTags;

    @ModifyReturnValue(method = "is(Lnet/minecraft/tags/TagKey;)Z", at = @At(value = "RETURN"))
    private boolean checkDynamicTags(boolean original, @Local(argsOnly = true) TagKey<DamageType> key)
    {
        if (original) return true;
        else return (limaCore$dynamicTags != null && limaCore$dynamicTags.contains(key));
    }

    @Override
    public void limaCore$addDynamicTag(@NotNull TagKey<DamageType> tag)
    {
        if (limaCore$dynamicTags == null) limaCore$dynamicTags = new ObjectOpenHashSet<>();

        limaCore$dynamicTags.add(tag);
    }
}