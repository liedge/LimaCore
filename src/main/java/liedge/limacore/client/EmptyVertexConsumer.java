package liedge.limacore.client;

import com.mojang.blaze3d.vertex.VertexConsumer;

public class EmptyVertexConsumer implements VertexConsumer
{
    public static final VertexConsumer EMPTY_VERTEX_CONSUMER = new EmptyVertexConsumer();

    private EmptyVertexConsumer() {}

    @Override
    public VertexConsumer addVertex(float x, float y, float z)
    {
        return this;
    }

    @Override
    public VertexConsumer setColor(int red, int green, int blue, int alpha)
    {
        return this;
    }

    @Override
    public VertexConsumer setUv(float u, float v)
    {
        return this;
    }

    @Override
    public VertexConsumer setUv1(int u, int v)
    {
        return this;
    }

    @Override
    public VertexConsumer setUv2(int u, int v)
    {
        return this;
    }

    @Override
    public VertexConsumer setNormal(float normalX, float normalY, float normalZ)
    {
        return this;
    }
}