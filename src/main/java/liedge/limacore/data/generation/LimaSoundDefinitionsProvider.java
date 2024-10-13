package liedge.limacore.data.generation;

import liedge.limacore.lib.ModResources;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

import static liedge.limacore.util.LimaRegistryUtil.getNonNullRegistryId;

public abstract class LimaSoundDefinitionsProvider extends SoundDefinitionsProvider
{
    public static String defaultSubtitleKey(ResourceLocation id)
    {
        return ModResources.prefixIdTranslationKey("subtitle", id);
    }

    public static String defaultSubtitleKey(Holder<SoundEvent> holder)
    {
        return defaultSubtitleKey(getNonNullRegistryId(holder));
    }

    private final ModResources resources;

    protected LimaSoundDefinitionsProvider(PackOutput output, ModResources resources, ExistingFileHelper helper)
    {
        super(output, resources.modid(), helper);
        this.resources = resources;
    }

    protected SoundDefinition.Sound beginSound(Holder<SoundEvent> holder, SoundDefinition.SoundType soundType)
    {
        return sound(getNonNullRegistryId(holder), soundType);
    }

    protected SoundDefinition.Sound beginSound(String soundFilePath, SoundDefinition.SoundType soundType)
    {
        return sound(resources.location(soundFilePath), soundType);
    }

    protected SoundDefinition beginDefinition(ResourceLocation soundEventId)
    {
        return SoundDefinition.definition().subtitle(defaultSubtitleKey(soundEventId));
    }

    protected SoundDefinition beginDefinition(String soundEventName)
    {
        return beginDefinition(resources.location(soundEventName));
    }

    protected SoundDefinition beginDefinition(Holder<SoundEvent> holder)
    {
        return beginDefinition(getNonNullRegistryId(holder));
    }

    protected void add(Holder<SoundEvent> holder, SoundDefinition definition)
    {
        add(holder.value(), definition);
    }

    protected void addSingleDirectSound(Holder<SoundEvent> holder)
    {
        add(holder.value(), beginDefinition(holder).with(beginSound(holder, SoundDefinition.SoundType.SOUND)));
    }

    protected void addSingleDirectSound(Holder<SoundEvent> holder, String soundFilePath)
    {
        add(holder.value(), beginDefinition(holder).with(beginSound(soundFilePath, SoundDefinition.SoundType.SOUND)));
    }

    protected void addSingleEventRedirectSound(Holder<SoundEvent> eventHolder, Holder<SoundEvent> destinationHolder)
    {
        add(eventHolder.value(), beginDefinition(eventHolder).with(beginSound(destinationHolder, SoundDefinition.SoundType.EVENT)));
    }
}