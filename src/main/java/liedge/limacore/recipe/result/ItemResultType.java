package liedge.limacore.recipe.result;

import com.mojang.serialization.MapCodec;
import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public enum ItemResultType implements StringRepresentable
{
    CONSTANT_RESULT("constant", ConstantItemResult.CODEC, ConstantItemResult.STREAM_CODEC),
    RANDOM_CHANCE("random_chance", RandomChanceItemResult.CODEC, RandomChanceItemResult.STREAM_CODEC),
    VARIABLE_COUNT("variable_count", VariableCountItemResult.CODEC, VariableCountItemResult.STREAM_CODEC);

    public static final LimaEnumCodec<ItemResultType> CODEC = LimaEnumCodec.create(ItemResultType.class);
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemResultType> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(ItemResultType.class);

    private final String name;
    private final MapCodec<? extends ItemResult> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, ? extends ItemResult> streamCodec;

    ItemResultType(String name, MapCodec<? extends ItemResult> codec, StreamCodec<RegistryFriendlyByteBuf, ? extends ItemResult> streamCodec)
    {
        this.name = name;
        this.codec = codec;
        this.streamCodec = streamCodec;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }

    public MapCodec<? extends ItemResult> getCodec()
    {
        return codec;
    }

    public StreamCodec<RegistryFriendlyByteBuf, ? extends ItemResult> getStreamCodec()
    {
        return streamCodec;
    }
}