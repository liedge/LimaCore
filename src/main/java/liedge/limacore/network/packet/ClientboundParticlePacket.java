package liedge.limacore.network.packet;

import liedge.limacore.LimaCore;
import liedge.limacore.network.ClientboundPayload;
import liedge.limacore.network.LimaStreamCodecs;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * A simplified version of the MC {@link net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket}. Uses
 * doubles for the speed and removes some unnecessary data for typical remote particle spawning.
 * @param data The {@link ParticleOptions} particle data to be used.
 * @param pos The position of the particle.
 * @param speed The speed of the particle.
 */
public record ClientboundParticlePacket(ParticleOptions data, Vec3 pos, Vec3 speed) implements ClientboundPayload
{
    public static final Type<ClientboundParticlePacket> TYPE = LimaCore.RESOURCES.packetType("particle");
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundParticlePacket> STREAM_CODEC = StreamCodec.composite(
            ParticleTypes.STREAM_CODEC, ClientboundParticlePacket::data,
            LimaStreamCodecs.VEC3D, ClientboundParticlePacket::pos,
            LimaStreamCodecs.VEC3D, ClientboundParticlePacket::speed,
            ClientboundParticlePacket::new);

    public ClientboundParticlePacket(ParticleOptions data, Vec3 pos)
    {
        this(data, pos, Vec3.ZERO);
    }

    public ClientboundParticlePacket(ParticleOptions data, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
    {
        this(data, new Vec3(x, y, z), new Vec3(xSpeed, ySpeed, zSpeed));
    }

    public ClientboundParticlePacket(ParticleOptions data, double x, double y, double z)
    {
        this(data, new Vec3(x, y, z));
    }

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