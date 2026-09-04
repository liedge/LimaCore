package liedge.limacore.client.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;

public interface BaseColorParticleOptions extends ParticleOptions
{
    MapCodec<Integer> COLOR_FIELD_CODEC = ExtraCodecs.RGB_COLOR_CODEC.fieldOf("color");

    int color();

    default float red()
    {
        return ARGB.redFloat(color());
    }

    default float green()
    {
        return ARGB.greenFloat(color());
    }

    default float blue()
    {
        return ARGB.blueFloat(color());
    }
}