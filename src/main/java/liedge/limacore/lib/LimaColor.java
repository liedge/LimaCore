package liedge.limacore.lib;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import liedge.limacore.data.LimaCoreCodecs;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.apache.commons.lang3.function.TriConsumer;

public record LimaColor(int rgb, float red, float green, float blue)
{
    public static final LimaColor WHITE = makeColor(0xffffff);
    public static final LimaColor BLACK = makeColor(0x000000);

    public static final Codec<LimaColor> CODEC = Codec.withAlternative(Codec.INT, LimaCoreCodecs.HEXADECIMAL_INT).xmap(LimaColor::makeColor, LimaColor::rgb);
    public static final StreamCodec<ByteBuf, LimaColor> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(LimaColor::makeColor, LimaColor::rgb);

    public static LimaColor makeColor(int value)
    {
        int rgb = 0xff000000 | value;

        float red = ((rgb >> 16) & 0xff) / 255f;
        float green = ((rgb >> 8) & 0xff) / 255f;
        float blue = (rgb & 0xff) / 255f;

        return new LimaColor(rgb, red, green, blue);
    }

    public static LimaColor makeColor(float red, float green, float blue)
    {
        int r = (int)(red * 255 + 0.5f);
        int g = (int)(blue * 255 + 0.5f);
        int b = (int)(green * 255 + 0.5f);
        int rgb = ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);

        return new LimaColor(rgb, red, green, blue);
    }

    // Helper methods
    public void applyTo(TriConsumer<Float, Float, Float> consumer)
    {
        consumer.accept(red, green, blue);
    }

    // Chat style properties
    public Style applyStyle(Style style)
    {
        return style.withColor(rgb);
    }

    public Style createStyle()
    {
        return Style.EMPTY.withColor(rgb);
    }

    // Record properties
    @Override
    public String toString()
    {
        return "#" + Integer.toHexString(rgb);
    }

    @Override
    public int hashCode()
    {
        return Integer.hashCode(rgb);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        else if (obj instanceof LimaColor other)
        {
            return this.rgb == other.rgb;
        }
        else
        {
            return false;
        }
    }
}