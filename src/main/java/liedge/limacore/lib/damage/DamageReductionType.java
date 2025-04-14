package liedge.limacore.lib.damage;

import liedge.limacore.LimaCore;
import liedge.limacore.data.LimaEnumCodec;
import liedge.limacore.lib.Translatable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

/**
 * One-to-one wrapper for {@link net.neoforged.neoforge.common.damagesource.DamageContainer.Reduction},
 * with added serialization compatibility.
 */
public enum DamageReductionType implements StringRepresentable, Translatable
{
    ARMOR("armor", DamageContainer.Reduction.ARMOR),
    ENCHANTMENTS("enchantments", DamageContainer.Reduction.ENCHANTMENTS),
    MOB_EFFECTS("mob_effects", DamageContainer.Reduction.MOB_EFFECTS),
    ABSORPTION("absorption", DamageContainer.Reduction.ABSORPTION);

    public static final LimaEnumCodec<DamageReductionType> CODEC = LimaEnumCodec.create(DamageReductionType.class);
    public static final StreamCodec<FriendlyByteBuf, DamageReductionType> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(DamageReductionType.class);

    private final String name;
    private final String langKey;
    private final DamageContainer.Reduction reduction;

    DamageReductionType(String name, DamageContainer.Reduction reduction)
    {
        this.name = name;
        this.langKey = LimaCore.RESOURCES.translationKey("reduction_type.{}", name);
        this.reduction = reduction;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }

    @Override
    public String descriptionId()
    {
        return langKey;
    }

    public DamageContainer.Reduction getReduction()
    {
        return reduction;
    }
}