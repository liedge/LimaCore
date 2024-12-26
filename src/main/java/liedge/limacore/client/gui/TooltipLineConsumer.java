package liedge.limacore.client.gui;

import com.mojang.datafixers.util.Either;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.function.Consumer;

@FunctionalInterface
public interface TooltipLineConsumer extends Consumer<Either<FormattedText, TooltipComponent>>
{
    default void accept(FormattedText text)
    {
        accept(Either.left(text));
    }

    default void accept(TooltipComponent component)
    {
        accept(Either.right(component));
    }
}