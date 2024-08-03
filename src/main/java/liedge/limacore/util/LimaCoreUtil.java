package liedge.limacore.util;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public final class LimaCoreUtil
{
    private LimaCoreUtil() {}

    //#region Block entity access helpers
    @SuppressWarnings("deprecation")
    public static @Nullable BlockEntity getSafeBlockEntity(@Nullable LevelReader level, BlockPos blockPos)
    {
        if (level != null && level.hasChunkAt(blockPos))
        {
            return level.getBlockEntity(blockPos);
        }
        else
        {
            return null;
        }
    }

    public static <BE> @Nullable BE getSafeBlockEntity(@Nullable LevelReader level, BlockPos blockPos, Class<BE> beClass)
    {
        return castOrNull(beClass, getSafeBlockEntity(level, blockPos));
    }

    @SuppressWarnings("deprecation")
    public static @Nullable LevelChunk getSafeLevelChunk(@Nullable LevelReader level, int chunkX, int chunkZ)
    {
        if (level != null && level.hasChunk(chunkX, chunkZ))
        {
            return castOrNull(LevelChunk.class, level.getChunk(chunkX, chunkZ));
        }
        else
        {
            return null;
        }
    }

    public static @Nullable LevelChunk getSafeLevelChunk(@Nullable LevelReader level, ChunkPos chunkPos)
    {
        return getSafeLevelChunk(level, chunkPos.x, chunkPos.z);
    }
    //#endregion

    //#region Attachment helper functions
    public static <S extends Tag, T extends INBTSerializable<S>> void copySerializableAttachments(AttachmentType<T> attachmentType, IAttachmentHolder source, IAttachmentHolder destination, HolderLookup.Provider registries)
    {
        S tag = source.getData(attachmentType).serializeNBT(registries);
        destination.getData(attachmentType).deserializeNBT(registries, tag);
    }

    public static <S extends Tag, T extends INBTSerializable<S>> void copySerializableAttachments(Supplier<AttachmentType<T>> typeSupplier, IAttachmentHolder source, IAttachmentHolder destination, HolderLookup.Provider registries)
    {
        copySerializableAttachments(typeSupplier.get(), source, destination, registries);
    }
    //#endregion

    // Misc functions
    @SuppressWarnings("unchecked")
    public static <T extends Recipe<?>> @Nullable RecipeHolder<T> getRecipeByKey(Level level, ResourceLocation recipeId, RecipeType<T> recipeType)
    {
        Optional<RecipeHolder<?>> optional = level.getRecipeManager().byKey(recipeId).filter(holder -> holder.value().getType().equals(recipeType));
        return (RecipeHolder<T>) optional.orElse(null);
    }

    public static <T extends Recipe<?>> @Nullable RecipeHolder<T> getRecipeByKey(Level level, ResourceLocation recipeId, Supplier<? extends RecipeType<T>> typeSupplier)
    {
        return getRecipeByKey(level, recipeId, typeSupplier.get());
    }

    public static boolean isEntityHostile(Entity entity)
    {
        return entity instanceof Enemy || !entity.getType().getCategory().isFriendly();
    }

    public static void customKnockbackEntity(LivingEntity entity, boolean ignoreResist, double strength, double ratioX, double ratioZ)
    {
        LivingKnockBackEvent event = CommonHooks.onLivingKnockBack(entity, (float) strength, ratioX, ratioZ);
        if (event.isCanceled()) return;

        strength = event.getStrength();
        ratioX = event.getRatioX();
        ratioZ = event.getRatioZ();

        double resist = ignoreResist ? 0 : entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        strength *= (1 - resist);
        if (strength > 0)
        {
            entity.hasImpulse = true;
            Vec3 vec3 = entity.getDeltaMovement();

            while (ratioX * ratioX + ratioZ * ratioZ < 1.0e-5f)
            {
                ratioX = (Math.random() - Math.random()) * 0.01d;
                ratioZ = (Math.random() - Math.random()) * 0.01d;
            }

            Vec3 vec31 = new Vec3(ratioX, 0.0d, ratioZ).normalize().scale(strength);
            entity.setDeltaMovement(vec3.x / 2.0d - vec31.x, entity.onGround() ? Math.min(0.4d, vec3.y / 2.0d + strength) : vec3.y, vec3.z / 2.0d - vec31.z);
        }
    }

    public static <T, L extends T, R extends T> Either<L, R> eitherFromSubclasses(T object, Class<L> leftClass, Class<R> rightClass)
    {
        if (leftClass.isInstance(object))
        {
            return Either.left(leftClass.cast(object));
        }
        else if (rightClass.isInstance(object))
        {
            return Either.right(rightClass.cast(object));
        }
        else
        {
            throw new IllegalArgumentException("Object is not an instance of either " + leftClass.getSimpleName() + " or " + rightClass.getSimpleName());
        }
    }

    public static @Nullable <T> T castOrNull(Class<T> type, @Nullable Object o)
    {
        return type.isInstance(o) ? type.cast(o) : null;
    }

    public static <T> T castOrThrow(Class<T> type, @Nullable Object o)
    {
        return Objects.requireNonNull(castOrNull(type, o), "Object is not an instance of " + type.getSimpleName());
    }

    public static <T, X extends Throwable> T castOrThrow(Class<T> type, @Nullable Object o, Supplier<X> exceptionSupplier) throws X
    {
        T val = castOrNull(type, o);
        if (val != null)
        {
            return val;
        }
        else
        {
            throw exceptionSupplier.get();
        }
    }
}