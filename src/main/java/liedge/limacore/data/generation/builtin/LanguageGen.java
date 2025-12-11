package liedge.limacore.data.generation.builtin;

import liedge.limacore.LimaCore;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.RelativeHorizontalSide;
import liedge.limacore.client.LimaComponentUtil;
import liedge.limacore.data.generation.LimaLanguageProvider;
import liedge.limacore.lib.damage.DamageReductionType;
import liedge.limacore.recipe.result.ResultPriority;
import liedge.limacore.registry.game.LimaCoreAttributes;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;

class LanguageGen extends LimaLanguageProvider
{
    LanguageGen(PackOutput output)
    {
        super(output, LimaCore.RESOURCES);
    }

    @Override
    protected void addTranslations()
    {
        // Attributes
        add(LimaCoreAttributes.DAMAGE_MULTIPLIER.get().getDescriptionId(), "Damage Multiplier");
        add(LimaCoreAttributes.KNOCKBACK_MULTIPLIER.get().getDescriptionId(), "Knockback Multiplier");

        // Direction translations
        for (Direction side : Direction.values())
        {
            add(LimaComponentUtil.localizeDirection(side), localizeSimpleName(side));
        }

        addEnum(DamageReductionType.class);
        addEnum(IOAccess.class, (e, v) -> e == IOAccess.INPUT_AND_OUTPUT ? "Input/Output" : v);
        addEnum(RelativeHorizontalSide.class);
        addEnum(ResultPriority.class, (e, v) -> v + " Output");
    }
}