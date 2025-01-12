package liedge.limacore.client;

import liedge.limacore.LimaCommonConstants;
import liedge.limacore.LimaCore;
import liedge.limacore.lib.Translatable;
import liedge.limacore.util.LimaCollectionsUtil;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collector;

public final class LimaComponentUtil
{
    private LimaComponentUtil() {}

    // Standard constant components
    public static final Component NEWLINE = Component.literal("\n");
    public static final Component COMMA_SEPARATOR = Component.literal(", ");
    public static final Component INFINITY_SYMBOL = Component.literal(LimaCommonConstants.INFINITY_SYMBOL);
    public static final Component BULLET_1_INDENT = Component.literal(" • ");
    public static final Component BULLET_2_INDENT = Component.literal("  • ");
    public static final Component MINUS_1_INDENT = Component.literal(" - ");
    public static final Component MINUS_2_INDENT = Component.literal("  - ");

    private static final Map<Direction, Translatable> DIRECTION_KEYS = LimaCollectionsUtil.fillAndCreateImmutableEnumMap(Direction.class, side -> LimaCore.RESOURCES.translationHolder("direction", "{}", side.getSerializedName()));

    public static Translatable localizeDirection(Direction side)
    {
        return DIRECTION_KEYS.get(side);
    }

    //#region Component helper functions
    public static MutableComponent bulletPointListWithHeader(Component header, Component bullet, Collection<? extends Component> elements)
    {
        return header.copy().append(NEWLINE).append(bulletPointList(bullet, elements));
    }

    public static MutableComponent bulletPointList(Component bullet, Collection<? extends Component> elements)
    {
        return ComponentUtils.formatList(elements, NEWLINE, component -> bullet.copy().append(component));
    }

    public static void accumulateWithDelimiter(MutableComponent master, Component delimiter, Component line)
    {
        if (!master.getSiblings().isEmpty()) master.append(delimiter);
        master.append(line);
    }

    public static MutableComponent joinWithDelimiter(MutableComponent component1, MutableComponent component2, Component delimiter)
    {
        if (!component1.getSiblings().isEmpty()) component1.append(delimiter);
        return component1.append(component2);
    }

    public static Collector<Component, ?, MutableComponent> joiningComponents(Component delimiter)
    {
        return Collector.of(Component::empty, (component, line) -> accumulateWithDelimiter(component, line, delimiter), (com1, com2) -> joinWithDelimiter(com1, com2, delimiter), Collector.Characteristics.IDENTITY_FINISH);
    }
    //#endregion
}