package liedge.limacore.network.packet;

import liedge.limacore.LimaCore;
import liedge.limacore.network.sync.DataWatcherHolder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record ClientboundMenuDataWatcherPacket(List<DataWatcherHolder.DataEntry<?>> entries, int containerId) implements ClientboundDataWatcherPacket
{
    public static final Type<ClientboundMenuDataWatcherPacket> TYPE = LimaCore.RESOURCES.packetType("menu_data");
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundMenuDataWatcherPacket> STREAM_CODEC = StreamCodec.composite(
            DataWatcherHolder.DataEntry.STREAM_CODEC, ClientboundMenuDataWatcherPacket::entries,
            ByteBufCodecs.VAR_INT, ClientboundMenuDataWatcherPacket::containerId,
            ClientboundMenuDataWatcherPacket::new);

    @Override
    public void handleClient(IPayloadContext context)
    {
        LimaCoreClientPacketHandler.handleMenuDataWatcherPacket(this);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}