package liedge.limacore.data;

import com.mojang.serialization.Codec;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * A simple implementation of {@link DataComponentType}, for use with persistent
 * and network-synchronized data types.
 * @param <T> The type of the data component value
 */
public class LimaDataComponentType<T> implements DataComponentType<T>
{
    private final Codec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public LimaDataComponentType(Codec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec)
    {
        this.codec = codec;
        this.streamCodec = streamCodec;
    }

    public LimaDataComponentType(Codec<T> codec)
    {
        this(codec, ByteBufCodecs.fromCodecWithRegistries(codec));
    }

    @Override
    public Codec<T> codec()
    {
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec()
    {
        return streamCodec;
    }

    @Override
    public String toString()
    {
        return LimaRegistryUtil.getNonNullRegistryId(this, BuiltInRegistries.DATA_COMPONENT_TYPE).toString();
    }
}