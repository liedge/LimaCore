package liedge.limacore.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.function.*;

public final class LimaCoreUtil
{
    private LimaCoreUtil() {}

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
    public static @Nullable <T> T castOrNull(Class<T> type, @Nullable Object o)
    {
        return type.isInstance(o) ? type.cast(o) : null;
    }

    public static <T> T castOrThrow(Class<T> type, @Nullable Object o)
    {
        return castOrThrow(type, o, "Object is not an instance of " + type.getSimpleName());
    }

    public static <T> T castOrThrow(Class<T> type, @Nullable Object o, String errorMessage)
    {
        T val = castOrNull(type, o);
        if (val != null)
        {
            return val;
        }
        else
        {
            throw new ClassCastException(errorMessage);
        }
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

    public static <T> int toIntOrElse(Class<T> type, @Nullable Object o, ToIntFunction<? super T> mapper, int fallback)
    {
        return type.isInstance(o) ? mapper.applyAsInt(type.cast(o)) : fallback;
    }

    public static <T> double toDoubleOrElse(Class<T> type, @Nullable Object o, ToDoubleFunction<? super T> mapper, double fallback)
    {
        return type.isInstance(o) ? mapper.applyAsDouble(type.cast(o)) : fallback;
    }

    public static <T> boolean castAndTest(Class<T> type, @Nullable Object o, Predicate<? super T> predicate)
    {
        return type.isInstance(o) && predicate.test(type.cast(o));
    }
}