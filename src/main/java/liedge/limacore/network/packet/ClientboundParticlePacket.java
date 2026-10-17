package liedge.limacore.network.packet;

import liedge.limacore.LimaCore;
import liedge.limacore.network.ClientboundPayload;
import liedge.limacore.network.LimaStreamCodecs;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundParticlePacket(ParticleOptions data, Vec3 pos, Vec3 aux, boolean overrideLimiter, boolean alwaysShow) implements ClientboundPayload
{
    public static final Type<ClientboundParticlePacket> TYPE = LimaCore.RESOURCES.packetType("particle");
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundParticlePacket> STREAM_CODEC = StreamCodec.composite(
            ParticleTypes.STREAM_CODEC, ClientboundParticlePacket::data,
            LimaStreamCodecs.VEC3D, ClientboundParticlePacket::pos,
            LimaStreamCodecs.VEC3D, ClientboundParticlePacket::aux,
            ByteBufCodecs.BOOL, ClientboundParticlePacket::overrideLimiter,
            ByteBufCodecs.BOOL, ClientboundParticlePacket::alwaysShow,
            ClientboundParticlePacket::new);

    @Override
    public void handleClient(IPayloadContext context)
    {
        LimaCoreClientPacketHandler.handleParticlePacket(this);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}