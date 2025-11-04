package liedge.limacore.client.gui;

import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;

public enum HorizontalAlignment implements StringRepresentable
{
    LEFT("left"),
    CENTER("center"),
    RIGHT("right");

    public static final LimaEnumCodec<HorizontalAlignment> CODEC = LimaEnumCodec.create(HorizontalAlignment.class);

    private final String name;

    HorizontalAlignment(String name)
    {
        this.name = name;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }

    public int getX(int elementWidth, int screenWidth)
    {
        return switch (this)
        {
            case LEFT -> 0;
            case CENTER -> (screenWidth - elementWidth) / 2;
            case RIGHT -> screenWidth - elementWidth;
        };
    }

    public int getAbsoluteX(int elementWidth, int screenWidth, int offset)
    {
        int x = getX(elementWidth, screenWidth);
        return this == RIGHT ? x - offset : x + offset;
    }

    public int getRelativeX(int elementWidth, int screenWidth, float position)
    {
        float x = switch (this)
        {
            case LEFT -> screenWidth * position;
            case CENTER -> screenWidth * position - elementWidth / 2f;
            case RIGHT -> screenWidth * (1f - position) - elementWidth;
        };
        return Mth.floor(x);
    }
}