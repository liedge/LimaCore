package liedge.limacore.network.packet;

import liedge.limacore.LimaCore;
import liedge.limacore.menu.slot.LimaFluidSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ServerboundFluidSlotClickPacket(int containerId, int slotIndex, LimaFluidSlot.ClickAction action) implements CustomPacketPayload
{
    static final StreamCodec<FriendlyByteBuf, ServerboundFluidSlotClickPacket> STREAM_CODEC = StreamCodec.of((net, msg) -> msg.encode(net), ServerboundFluidSlotClickPacket::decode);
    static final Type<ServerboundFluidSlotClickPacket> TYPE = LimaCore.RESOURCES.packetType("fluid_slot_click");

    private static ServerboundFluidSlotClickPacket decode(FriendlyByteBuf net)
    {
        return new ServerboundFluidSlotClickPacket(net.readVarInt(), net.readVarInt(), net.readEnum(LimaFluidSlot.ClickAction.class));
    }

    private void encode(FriendlyByteBuf net)
    {
        net.writeVarInt(containerId);
        net.writeVarInt(slotIndex);
        net.writeEnum(action);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}