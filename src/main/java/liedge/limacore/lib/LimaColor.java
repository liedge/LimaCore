package liedge.limacore.lib;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import liedge.limacore.data.LimaCoreCodecs;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.FastColor.ARGB32;
import org.jetbrains.annotations.ApiStatus;

public record LimaColor(int argb32, float red, float green, float blue, Style chatStyle)
{
    public static final LimaColor WHITE = createOpaque(0xffffff);
    public static final LimaColor BLACK = createOpaque(0x000000);

    public static final Codec<LimaColor> CODEC = Codec.withAlternative(Codec.INT, LimaCoreCodecs.HEXADECIMAL_INT).xmap(LimaColor::createOpaque, LimaColor::argb32);
    public static final StreamCodec<ByteBuf, LimaColor> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(LimaColor::createOpaque, LimaColor::argb32);

    public static LimaColor createOpaque(int rgb)
    {
        int argb32 = ARGB32.opaque(rgb);
        float red = ARGB32.red(argb32) / 255f;
        float green = ARGB32.green(argb32) / 255f;
        float blue = ARGB32.blue(argb32) / 255f;

        return new LimaColor(argb32, red, green, blue);
    }

    public static LimaColor createOpaque(float red, float green, float blue)
    {
        int argb32 = ARGB32.colorFromFloat(1f, red, green, blue);

        return new LimaColor(argb32, red, green, blue);
    }

    @ApiStatus.Internal
    public LimaColor {}

    private LimaColor(int argb32, float red, float green, float blue)
    {
        this(argb32, red, green, blue, Style.EMPTY.withColor(argb32));
    }

    public Style applyChatStyle(Style style)
    {
        return style.withColor(argb32);
    }

    @Override
    public String toString()
    {
        return "#" + argb32;
    }

    @Override
    public int hashCode()
    {
        return Integer.hashCode(argb32);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        else if (obj instanceof LimaColor color)
        {
            return this.argb32() == color.argb32();
        }
        else
        {
            return false;
        }
    }
}