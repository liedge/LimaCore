package liedge.limacore.network.packet;

import liedge.limacore.LimaCore;
import liedge.limacore.menu.slot.LimaFluidSlot;
import liedge.limacore.network.ServerboundPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundFluidSlotClickPacket(int containerId, int slotIndex, LimaFluidSlot.ClickAction action) implements ServerboundPayload
{
    public static final StreamCodec<FriendlyByteBuf, ServerboundFluidSlotClickPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ServerboundFluidSlotClickPacket::containerId,
            ByteBufCodecs.VAR_INT,
            ServerboundFluidSlotClickPacket::slotIndex,
            LimaFluidSlot.ClickAction.STREAM_CODEC,
            ServerboundFluidSlotClickPacket::action,
            ServerboundFluidSlotClickPacket::new);
    public static final Type<ServerboundFluidSlotClickPacket> TYPE = LimaCore.RESOURCES.packetType("fluid_slot_click");

    @Override
    public void handleServer(ServerPlayer sender, IPayloadContext context)
    {
        LimaCoreServerPacketHandler.handleFluidSlotClick(this, sender);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}