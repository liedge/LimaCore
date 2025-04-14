package liedge.limacore.data.generation.builtin;

import liedge.limacore.LimaCore;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.RelativeHorizontalSide;
import liedge.limacore.client.LimaComponentUtil;
import liedge.limacore.data.generation.LimaLanguageProvider;
import liedge.limacore.lib.damage.DamageReductionType;
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

        // Damage reduction types
        for (DamageReductionType type : DamageReductionType.values())
        {
            add(type, localizeSimpleName(type));
        }

        // IO Access translations
        for (IOAccess access : IOAccess.values())
        {
            String value = access == IOAccess.INPUT_AND_OUTPUT ? "Input/Output" : localizeSimpleName(access);
            add(access, value);
        }

        // Direction translations
        for (Direction side : Direction.values())
        {
            add(LimaComponentUtil.localizeDirection(side), localizeSimpleName(side));
        }

        // Relative side translations
        for (RelativeHorizontalSide side : RelativeHorizontalSide.values())
        {
            add(side, localizeSimpleName(side));
        }
    }
}