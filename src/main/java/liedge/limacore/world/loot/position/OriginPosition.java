package liedge.limacore.world.loot.position;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public final class OriginPosition implements ContextPosition
{
    private static final OriginPosition INSTANCE = new OriginPosition();

    static final MapCodec<OriginPosition> CODEC = MapCodec.unit(INSTANCE);

    public static OriginPosition contextOrigin()
    {
        return INSTANCE;
    }

    private OriginPosition() {}

    @Override
    public @Nullable Vec3 get(LootContext context)
    {
        return context.getOptionalParameter(LootContextParams.ORIGIN);
    }

    @Override
    public Type getType()
    {
        return Type.CONTEXT_ORIGIN;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams()
    {
        return Set.of(LootContextParams.ORIGIN);
    }
}