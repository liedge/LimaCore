package liedge.limacore.capability.energy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import liedge.limacore.network.LimaStreamCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public record EnergyContainerSpec(int capacity, int transferRate)
{
    public static final Codec<EnergyContainerSpec> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("capacity").forGetter(EnergyContainerSpec::capacity),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("transfer_rate").forGetter(EnergyContainerSpec::transferRate))
            .apply(instance, EnergyContainerSpec::new));

    public static final StreamCodec<ByteBuf, EnergyContainerSpec> STREAM_CODEC = StreamCodec.composite(
            LimaStreamCodecs.NON_NEGATIVE_VAR_INT, EnergyContainerSpec::capacity,
            LimaStreamCodecs.NON_NEGATIVE_VAR_INT, EnergyContainerSpec::transferRate,
            EnergyContainerSpec::new);

    public static final EnergyContainerSpec EMPTY = new EnergyContainerSpec(0, 0);
    public static final EnergyContainerSpec INFINITE = new EnergyContainerSpec(Integer.MAX_VALUE, Integer.MAX_VALUE);

    public static EnergyContainerSpec of(LimaEnergyStorage energyStorage)
    {
        return new EnergyContainerSpec(energyStorage.getMaxEnergyStored(), energyStorage.getTransferRate());
    }

    public EnergyContainerSpec withNewCapacity(int newCapacity)
    {
        return new EnergyContainerSpec(newCapacity, this.transferRate);
    }

    public EnergyContainerSpec withNewTransferRate(int newTransferRate)
    {
        return new EnergyContainerSpec(this.capacity, newTransferRate);
    }
}