package liedge.limacore.world.loot.position;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public interface ContextPosition extends LootContextUser
{
    Codec<ContextPosition> CODEC = Type.CODEC.dispatchWithInline(EntityPosition.class, EntityPosition.INLINE_CODEC, ContextPosition::getType, Type::getCodec);

    @Nullable Vec3 get(LootContext context);

    Type getType();

    enum Type implements StringRepresentable
    {
        CONTEXT_ENTITY("entity", EntityPosition.CODEC),
        CONTEXT_ORIGIN("origin", OriginPosition.CODEC),
        CONTEXT_BLOCK_ENTITY("block_entity", BlockEntityPosition.CODEC);

        public static final LimaEnumCodec<Type> CODEC = LimaEnumCodec.create(Type.class);

        private final String name;
        private final MapCodec<? extends ContextPosition> codec;

        Type(String name, MapCodec<? extends ContextPosition> codec)
        {
            this.name = name;
            this.codec = codec;
        }

        @Override
        public String getSerializedName()
        {
            return name;
        }

        public MapCodec<? extends ContextPosition> getCodec()
        {
            return codec;
        }
    }
}