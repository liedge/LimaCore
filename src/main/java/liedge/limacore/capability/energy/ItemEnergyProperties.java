package liedge.limacore.capability.energy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import liedge.limacore.network.LimaStreamCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public record ItemEnergyProperties(int capacity, int transferRate, int energyUsage)
{
    public static final Codec<ItemEnergyProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("capacity").forGetter(ItemEnergyProperties::capacity),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("transfer_rate").forGetter(ItemEnergyProperties::transferRate),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("energy_usage").forGetter(ItemEnergyProperties::energyUsage))
            .apply(instance, ItemEnergyProperties::new));

    public static final StreamCodec<ByteBuf, ItemEnergyProperties> STREAM_CODEC = StreamCodec.composite(
            LimaStreamCodecs.NON_NEGATIVE_VAR_INT, ItemEnergyProperties::capacity,
            LimaStreamCodecs.NON_NEGATIVE_VAR_INT, ItemEnergyProperties::transferRate,
            LimaStreamCodecs.NON_NEGATIVE_VAR_INT, ItemEnergyProperties::energyUsage,
            ItemEnergyProperties::new);

    public static final ItemEnergyProperties EMPTY = new ItemEnergyProperties(0, 0, 0);
    public static final ItemEnergyProperties INFINITE = new ItemEnergyProperties(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);

    public ItemEnergyProperties withCapacity(int capacity)
    {
        return new ItemEnergyProperties(capacity, this.transferRate, this.energyUsage);
    }

    public ItemEnergyProperties withTransferRate(int transferRate)
    {
        return new ItemEnergyProperties(this.capacity, transferRate, this.energyUsage);
    }

    public ItemEnergyProperties withEnergyUsage(int energyUsage)
    {
        return new ItemEnergyProperties(this.capacity, this.transferRate, energyUsage);
    }
}