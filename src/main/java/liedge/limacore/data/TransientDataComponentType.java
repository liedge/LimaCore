package liedge.limacore.data;

import com.mojang.serialization.Codec;
import liedge.limacore.lib.damage.LimaDamageSourceExtension;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * For use in transient {@link net.neoforged.neoforge.common.MutableDataComponentHolder} instances like
 * {@link LimaDamageSourceExtension}. Not safe for serialization, do not use in
 * persistent contexts.
 * @param id
 * @param <T>
 */
public record TransientDataComponentType<T>(ResourceLocation id) implements DataComponentType<T>
{
    @Nullable
    @Override
    public Codec<T> codec()
    {
        throw new UnsupportedOperationException("Transient data components do not support serialization.");
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec()
    {
        throw new UnsupportedOperationException("Transient data components do not support network serialization.");
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        else if (obj instanceof TransientDataComponentType<?> other) return this.id.equals(other.id);
        else return false;
    }

    @Override
    public String toString()
    {
        return "TransientDataComponent[" + id + "]";
    }
}