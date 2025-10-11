package liedge.limacore.world.loot.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface LootPositionSource extends LootContextUser
{
    Codec<LootPositionSource> CODEC = Type.CODEC.dispatch(LootPositionSource::getType, Type::getCodec);

    @Nullable Vec3 get(LootContext context);

    Type getType();

    static LootPositionSource constant(Vec3 pos)
    {
        return new Constant(pos);
    }

    static LootPositionSource entityPos(LootContext.EntityTarget target)
    {
        return new EntityPos(target);
    }

    record Constant(Vec3 pos) implements LootPositionSource
    {
        public static final MapCodec<Constant> CODEC = Vec3.CODEC.fieldOf("pos").xmap(Constant::new, Constant::pos);

        @Override
        public @Nullable Vec3 get(LootContext context)
        {
            return pos;
        }

        @Override
        public Type getType()
        {
            return Type.CONSTANT;
        }
    }

    record EntityPos(LootContext.EntityTarget target) implements LootPositionSource
    {
        public static final MapCodec<EntityPos> CODEC = LootContext.EntityTarget.CODEC.fieldOf("target").xmap(EntityPos::new, EntityPos::target);

        @Override
        public @Nullable Vec3 get(LootContext context)
        {
            Entity entity = context.getParamOrNull(target.getParam());
            return entity != null ? entity.position() : null;
        }

        @Override
        public Type getType()
        {
            return Type.ENTITY_POS;
        }

        @Override
        public Set<LootContextParam<?>> getReferencedContextParams()
        {
            return Set.of(target.getParam());
        }
    }

    enum Type implements StringRepresentable
    {
        CONSTANT("constant", Constant.CODEC),
        ENTITY_POS("entity", EntityPos.CODEC);

        public static final LimaEnumCodec<Type> CODEC = LimaEnumCodec.create(Type.class);

        private final String name;
        private final MapCodec<? extends LootPositionSource> codec;

        Type(String name, MapCodec<? extends LootPositionSource> codec)
        {
            this.name = name;
            this.codec = codec;
        }

        public MapCodec<? extends LootPositionSource> getCodec()
        {
            return codec;
        }

        @Override
        public String getSerializedName()
        {
            return name;
        }
    }
}