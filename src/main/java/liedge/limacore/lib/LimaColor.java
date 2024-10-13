package liedge.limacore.lib;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import liedge.limacore.data.LimaCoreCodecs;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.FastColor.ARGB32;
import org.jetbrains.annotations.ApiStatus;

public record LimaColor(int packedRGB, float red, float green, float blue, Style chatStyle)
{
    private static final Interner<LimaColor> INTERNER = Interners.newWeakInterner();

    public static final LimaColor WHITE = createOpaque(0xffffff);
    public static final LimaColor BLACK = createOpaque(0x000000);

    public static final Codec<LimaColor> CODEC = Codec.withAlternative(Codec.INT, LimaCoreCodecs.HEXADECIMAL_INT).xmap(LimaColor::createOpaque, LimaColor::packedRGB);
    public static final StreamCodec<ByteBuf, LimaColor> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(LimaColor::createOpaque, LimaColor::packedRGB);

    private static LimaColor intern(int packedRGB, float red, float green, float blue)
    {
        Style chatStyle = Style.EMPTY.withColor(packedRGB);
        return INTERNER.intern(new LimaColor(packedRGB, red, green, blue, chatStyle));
    }

    public static LimaColor createOpaque(int rgb32)
    {
        int packedRGB = ARGB32.opaque(rgb32);
        float red = ARGB32.red(packedRGB) / 255f;
        float green = ARGB32.green(packedRGB) / 255f;
        float blue = ARGB32.blue(packedRGB) / 255f;

        return intern(packedRGB, red, green, blue);
    }

    public static LimaColor createOpaque(float red, float green, float blue)
    {
        return createOpaque(ARGB32.colorFromFloat(1f, red, green, blue));
    }

    @ApiStatus.Internal
    public LimaColor {}

    public Style applyChatStyle(Style style)
    {
        return style.withColor(packedRGB);
    }

    @Override
    public String toString()
    {
        return "#" + packedRGB;
    }

    @Override
    public int hashCode()
    {
        return Integer.hashCode(packedRGB);
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
            return this.packedRGB() == color.packedRGB();
        }
        else
        {
            return false;
        }
    }
}