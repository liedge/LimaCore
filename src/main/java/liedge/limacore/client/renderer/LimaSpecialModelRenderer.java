package liedge.limacore.client.renderer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import liedge.limacore.client.util.LimaModelsUtil;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

public interface LimaSpecialModelRenderer<T> extends SpecialModelRenderer<T>
{
    @Nullable T extractArgument(ItemStack item, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable ItemOwner owner);

    default int resolveTint(ItemTintSource tintSource, ItemStack item, @Nullable ClientLevel level, @Nullable ItemOwner owner)
    {
        return LimaModelsUtil.resolveTint(tintSource, item, level, owner);
    }

    @Deprecated
    default @Nullable T extractArgument(ItemStack stack)
    {
        return null;
    }

    interface LimaUnbaked<T> extends Unbaked<T>
    {
        Codec<LimaUnbaked<?>> CODEC = SpecialModelRenderers.CODEC.comapFlatMap(o -> {
            if (o instanceof LimaSpecialModelRenderer.LimaUnbaked<?> unbaked)
                return DataResult.success(unbaked);
            else
                return DataResult.error(() -> "Not a Lima unbaked special model renderer.");
        }, Function.identity());

        @Override
        @Nullable
        LimaSpecialModelRenderer<T> bake(BakingContext context);
    }
}