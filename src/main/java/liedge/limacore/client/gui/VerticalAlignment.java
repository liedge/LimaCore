package liedge.limacore.client.gui;

import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;

public enum VerticalAlignment implements StringRepresentable
{
    TOP("top"),
    CENTER("center"),
    BOTTOM("bottom");

    public static final LimaEnumCodec<VerticalAlignment> CODEC = LimaEnumCodec.create(VerticalAlignment.class);

    private final String name;

    VerticalAlignment(String name)
    {
        this.name = name;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }

    public int getY(int elementHeight, int screenHeight)
    {
        return switch (this)
        {
            case TOP -> 0;
            case CENTER -> (screenHeight - elementHeight) / 2;
            case BOTTOM -> screenHeight - elementHeight;
        };
    }

    public int getAbsoluteY(int elementHeight, int screenHeight, int offset)
    {
        int y = getY(elementHeight, screenHeight);
        return this == BOTTOM ? y - offset : y + offset;
    }

    public int getRelativeY(int elementHeight, int screenHeight, float position)
    {
        float y = switch (this)
        {
            case TOP -> screenHeight * position;
            case CENTER -> screenHeight * position - elementHeight / 2f;
            case BOTTOM -> screenHeight * (1f - position) - elementHeight;
        };
        return Mth.floor(y);
    }
}