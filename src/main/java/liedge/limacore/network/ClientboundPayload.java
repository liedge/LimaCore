package liedge.limacore.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface ClientboundPayload extends CustomPacketPayload
{
    void handleClient(IPayloadContext context);
}