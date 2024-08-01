package liedge.limacore.network.packet;

import liedge.limacore.util.LimaCoreUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.Nullable;

public interface LimaPlayPacket extends CustomPacketPayload
{
    void onReceivedByClient(IPayloadContext context, Player localPlayer);

    void onReceivedByServer(IPayloadContext context, ServerPlayer sender);

    interface ClientboundOnly extends LimaPlayPacket
    {
        @Override
        default void onReceivedByServer(IPayloadContext context, ServerPlayer sender)
        {
            throw new IllegalStateException("Clientbound only packet cannot be sent to server.");
        }
    }

    interface ServerboundOnly extends LimaPlayPacket
    {
        @Override
        default void onReceivedByClient(IPayloadContext context, Player localPlayer)
        {
            throw new IllegalStateException("Serverbound only packet cannot be sent to client.");
        }
    }

    record PacketSpec<T extends LimaPlayPacket>(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec)
    {
        public void registerPacket(PayloadRegistrar registrar, @Nullable PacketFlow packetFlow)
        {
            switch (packetFlow)
            {
                case CLIENTBOUND -> registrar.playToClient(type, codec, this::handleClientbound);
                case SERVERBOUND -> registrar.playToServer(type, codec, this::handleServerbound);
                case null -> registrar.playBidirectional(type, codec, this::handleBiDirectional);
            }
        }

        private void handleClientbound(T packet, IPayloadContext ctx)
        {
            packet.onReceivedByClient(ctx, ctx.player());
        }

        private void handleServerbound(T packet, IPayloadContext ctx)
        {
            ServerPlayer sender = LimaCoreUtil.castOrThrow(ServerPlayer.class, ctx.player(), () -> new IllegalStateException("Tried to process serverbound packet on the client."));
            packet.onReceivedByServer(ctx, sender);
        }

        private void handleBiDirectional(T packet, IPayloadContext ctx)
        {
            if (ctx.flow().isClientbound())
            {
                handleClientbound(packet, ctx);
            }
            else
            {
                handleServerbound(packet, ctx);
            }
        }
    }
}