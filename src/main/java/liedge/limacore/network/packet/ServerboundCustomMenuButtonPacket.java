package liedge.limacore.network.packet;

import liedge.limacore.LimaCore;
import liedge.limacore.network.IndexedStreamData;
import liedge.limacore.network.ServerboundPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static net.minecraft.network.codec.StreamCodec.composite;

public record ServerboundCustomMenuButtonPacket(int containerId, IndexedStreamData<?> streamData) implements ServerboundPayload
{
    public static final Type<ServerboundCustomMenuButtonPacket> TYPE = LimaCore.RESOURCES.packetType("custom_menu_button");
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundCustomMenuButtonPacket> STREAM_CODEC = composite(
            ByteBufCodecs.VAR_INT, ServerboundCustomMenuButtonPacket::containerId,
            IndexedStreamData.STREAM_CODEC, ServerboundCustomMenuButtonPacket::streamData,
            ServerboundCustomMenuButtonPacket::new);

    @Override
    public void handleServer(ServerPlayer sender, IPayloadContext context)
    {
        LimaCoreServerPacketHandler.handleCustomMenuButtonPacket(this, sender);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}