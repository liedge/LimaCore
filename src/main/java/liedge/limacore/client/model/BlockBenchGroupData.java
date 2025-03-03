package liedge.limacore.client.model;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.IntList;

public record BlockBenchGroupData(String name, IntList elements, int emissivity)
{
    public BlockBenchGroupData
    {
        Preconditions.checkArgument(emissivity >= 0 && emissivity < 16, "Emissivity must be between 0 and 15.");
    }

    public BlockBenchGroupData(String name, IntList elements)
    {
        this(name, elements, 0);
    }

    public boolean hasEmissivity()
    {
        return emissivity > 0;
    }
}

/*
public interface BlockBenchGroupData
{
    String name();

    IntList elements();

    int emissivity();

    default boolean hasEmissivity()
    {
        return emissivity() > 0;
    }

    record DefaultData(String name, IntList elements, int emissivity) implements BlockBenchGroupData
    {
        public DefaultData
        {
            if (emissivity < 0 || emissivity > 15) throw new IllegalArgumentException("Emissivity must be between 0 and 15.");
        }

        public DefaultData(String name, IntList elements)
        {
            this(name, elements, 0);
        }
    }
}
*/