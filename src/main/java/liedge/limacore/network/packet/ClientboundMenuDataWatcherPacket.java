package liedge.limacore.network.packet;

import liedge.limacore.LimaCore;
import liedge.limacore.network.ClientboundPayload;
import liedge.limacore.network.IndexedStreamData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record ClientboundMenuDataWatcherPacket(List<IndexedStreamData<?>> streamData, int containerId) implements ClientboundPayload
{
    public static final Type<ClientboundMenuDataWatcherPacket> TYPE = LimaCore.RESOURCES.packetType("menu_data");
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundMenuDataWatcherPacket> STREAM_CODEC = StreamCodec.composite(
            IndexedStreamData.LIST_STREAM_CODEC, ClientboundMenuDataWatcherPacket::streamData,
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