package liedge.limacore.client.model;

import it.unimi.dsi.fastutil.ints.IntList;

public interface BlockBenchGroupData
{
    String name();

    IntList elements();

    default boolean emissive()
    {
        return false;
    }
}