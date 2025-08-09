package liedge.limacore.blockentity;

import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.util.StringRepresentable;

public enum BlockContentsType implements StringRepresentable
{
    GENERAL("general"),
    AUXILIARY("aux"),
    INPUT("input"),
    OUTPUT("output");

    public static final LimaEnumCodec<BlockContentsType> CODEC = LimaEnumCodec.create(BlockContentsType.class);

    private final String name;

    BlockContentsType(String name)
    {
        this.name = name;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}