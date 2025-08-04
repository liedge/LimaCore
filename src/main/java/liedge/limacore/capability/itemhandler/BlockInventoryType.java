package liedge.limacore.capability.itemhandler;

import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.util.StringRepresentable;

public enum BlockInventoryType implements StringRepresentable
{
    GENERAL("general"),
    AUXILIARY("aux"),
    INPUT("input"),
    OUTPUT("output");

    public static final LimaEnumCodec<BlockInventoryType> CODEC = LimaEnumCodec.create(BlockInventoryType.class);

    private final String name;

    BlockInventoryType(String name)
    {
        this.name = name;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}