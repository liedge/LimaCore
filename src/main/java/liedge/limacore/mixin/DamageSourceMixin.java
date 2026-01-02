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
    private Set<TagKey<DamageType>> limaCore$extraTags;

    @ModifyReturnValue(method = "is(Lnet/minecraft/tags/TagKey;)Z", at = @At(value = "RETURN"))
    private boolean checkExtraTags(boolean original, @Local(argsOnly = true) TagKey<DamageType> key)
    {
        if (original) return true;
        else return (limaCore$extraTags != null && limaCore$extraTags.contains(key));
    }

    @Override
    public @NotNull Set<TagKey<DamageType>> limaCore$getExtraTags()
    {
        if (limaCore$extraTags == null) limaCore$extraTags = new ObjectOpenHashSet<>();

        return limaCore$extraTags;
    }
}