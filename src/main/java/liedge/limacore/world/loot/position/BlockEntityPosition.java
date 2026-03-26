package liedge.limacore.world.loot.position;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public record BlockEntityPosition(boolean center) implements ContextPosition
{
    static final MapCodec<BlockEntityPosition> CODEC = Codec.BOOL.optionalFieldOf("center", true).xmap(BlockEntityPosition::new, BlockEntityPosition::center);

    @Override
    public @Nullable Vec3 get(LootContext context)
    {
        BlockEntity blockEntity = context.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity == null) return null;

        BlockPos pos = blockEntity.getBlockPos();

        return center ? Vec3.atCenterOf(pos) : new Vec3(pos);
    }

    @Override
    public Type getType()
    {
        return Type.CONTEXT_BLOCK_ENTITY;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams()
    {
        return Set.of(LootContextParams.BLOCK_ENTITY);
    }
}