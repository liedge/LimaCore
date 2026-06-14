package liedge.limacore.network.packet;

import liedge.limacore.LimaCore;
import liedge.limacore.menu.LimaMenu;
import liedge.limacore.menu.slot.FluidMenuInput;
import liedge.limacore.network.ServerboundPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundFluidSlotInputPacket(int containerId, int slotIndex, FluidMenuInput input) implements ServerboundPayload
{
    public static final StreamCodec<FriendlyByteBuf, ServerboundFluidSlotInputPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ServerboundFluidSlotInputPacket::containerId,
            ByteBufCodecs.VAR_INT, ServerboundFluidSlotInputPacket::slotIndex,
            FluidMenuInput.STREAM_CODEC, ServerboundFluidSlotInputPacket::input,
            ServerboundFluidSlotInputPacket::new);
    public static final Type<ServerboundFluidSlotInputPacket> TYPE = LimaCore.RESOURCES.packetType("fluid_slot_input");

    @Override
    public void handleServer(ServerPlayer sender, IPayloadContext context)
    {
        if (sender.containerMenu instanceof LimaMenu<?> menu && menu.containerId == this.containerId)
        {
            menu.fluidClicked(sender, slotIndex, input);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}