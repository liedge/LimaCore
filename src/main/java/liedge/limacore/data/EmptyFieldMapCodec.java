package liedge.limacore.data;

import com.mojang.serialization.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class EmptyFieldMapCodec<A> extends MapCodec<A>
{
    public static <T> MapCodec<List<T>> emptyListField(String fieldKey)
    {
        return new EmptyFieldMapCodec<>(fieldKey, List.of());
    }

    private final String fieldKey;
    private final A unitValue;

    public EmptyFieldMapCodec(String fieldKey, A unitValue)
    {
        this.fieldKey = fieldKey;
        this.unitValue = unitValue;
    }

    @Override
    public <T> Stream<T> keys(DynamicOps<T> ops)
    {
        return Stream.empty();
    }

    @Override
    public <T> DataResult<A> decode(DynamicOps<T> ops, MapLike<T> input)
    {
        if (input.get(fieldKey) != null)
        {
            return DataResult.error(() -> String.format("Field '%s' must be absent during decoding.", fieldKey));
        }

        return DataResult.success(unitValue);
    }

    @Override
    public <T> RecordBuilder<T> encode(A input, DynamicOps<T> ops, RecordBuilder<T> prefix)
    {
        if (!Objects.equals(unitValue, input))
        {
            return prefix.withErrorsFrom(DataResult.error(() -> String.format("Cannot encode field '%s': it does not match the default unit value.", fieldKey)));
        }

        return prefix;
    }

    @Override
    public String toString()
    {
        return "EmptyFieldMapCodec[key=" + fieldKey + "]";
    }
}