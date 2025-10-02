package liedge.limacore.client;

import liedge.limacore.LimaCommonConstants;
import liedge.limacore.LimaCore;
import liedge.limacore.lib.Translatable;
import liedge.limacore.util.LimaCollectionsUtil;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;

import java.util.Collection;
import java.util.Map;

public final class LimaComponentUtil
{
    private LimaComponentUtil() {}

    // Standard constant components
    public static final Component COMMA_SEPARATOR = ComponentUtils.DEFAULT_NO_STYLE_SEPARATOR;
    public static final Component INFINITY_SYMBOL = Component.literal(LimaCommonConstants.INFINITY_SYMBOL);
    public static final Component BULLET_1_INDENT = Component.literal(" • ");
    public static final Component BULLET_2_INDENT = Component.literal("  • ");
    public static final Component MINUS_1_INDENT = Component.literal(" - ");
    public static final Component MINUS_2_INDENT = Component.literal("  - ");
    public static final Component COLON_SPACE_SEPARATOR = Component.literal(": ");

    private static final Map<Direction, Translatable> DIRECTION_KEYS = LimaCollectionsUtil.fillAndCreateImmutableEnumMap(Direction.class, side -> LimaCore.RESOURCES.translationHolder("direction", "{}", side.getSerializedName()));

    public static Translatable localizeDirection(Direction side)
    {
        return DIRECTION_KEYS.get(side);
    }

    //#region Component helper functions
    public static MutableComponent colonSpaced(MutableComponent first, MutableComponent second)
    {
        return first.append(COLON_SPACE_SEPARATOR).append(second);
    }

    public static MutableComponent bulletPointListWithHeader(Component header, Component bullet, Collection<? extends Component> elements)
    {
        return header.copy().append(CommonComponents.NEW_LINE).append(bulletPointList(bullet, elements));
    }

    public static MutableComponent bulletPointList(Component bullet, Collection<? extends Component> elements)
    {
        return ComponentUtils.formatList(elements, CommonComponents.NEW_LINE, component -> bullet.copy().append(component));
    }
    //#endregion
}