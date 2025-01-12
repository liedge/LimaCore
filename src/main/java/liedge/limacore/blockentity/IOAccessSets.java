package liedge.limacore.blockentity;

import java.util.EnumSet;
import java.util.Set;

public final class IOAccessSets
{
    private IOAccessSets() {}

    public static final Set<IOAccess> ALL_ALLOWED = EnumSet.allOf(IOAccess.class);
    public static final Set<IOAccess> INPUT_ONLY_OR_DISABLED = EnumSet.of(IOAccess.INPUT_ONLY, IOAccess.DISABLED);
    public static final Set<IOAccess> OUTPUT_ONLY_OR_DISABLED = EnumSet.of(IOAccess.OUTPUT_ONLY, IOAccess.DISABLED);
    public static final Set<IOAccess> INPUT_AND_OUTPUT_OR_DISABLED = EnumSet.of(IOAccess.INPUT_AND_OUTPUT, IOAccess.DISABLED);
    public static final Set<IOAccess> INPUT_XOR_OUTPUT_OR_DISABLED = EnumSet.of(IOAccess.INPUT_ONLY, IOAccess.OUTPUT_ONLY, IOAccess.DISABLED);
}