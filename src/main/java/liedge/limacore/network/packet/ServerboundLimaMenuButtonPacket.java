package liedge.limacore.network.packet;

import it.unimi.dsi.fastutil.ints.IntList;
import liedge.limacore.LimaCore;
import liedge.limacore.inventory.menu.LimaMenu;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record ServerboundLimaMenuButtonPacket(int containerId, int buttonId, int value) implements LimaPlayPacket.ServerboundOnly
{
    static final PacketSpec<ServerboundLimaMenuButtonPacket> PACKET_SPEC = LimaCore.RESOURCES.packetSpec("menu_button", ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list(3)).map(ServerboundLimaMenuButtonPacket::new, ServerboundLimaMenuButtonPacket::toList));

    private ServerboundLimaMenuButtonPacket(List<Integer> list)
    {
        this(list.get(0), list.get(1), list.get(2));
    }

    private List<Integer> toList()
    {
        return IntList.of(containerId, buttonId, value);
    }

    @Override
    public void onReceivedByServer(IPayloadContext context, ServerPlayer sender)
    {
        if (sender.containerMenu instanceof LimaMenu<?> menu && menu.containerId == containerId)
        {
            menu.handleCustomButton(sender, buttonId, value);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return PACKET_SPEC.type();
    }
}