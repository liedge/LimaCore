package liedge.limacore.data.generation;

import liedge.limacore.advancement.LimaAdvancementUtil;
import liedge.limacore.lib.ModResources;
import liedge.limacore.lib.Translatable;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.commons.lang3.StringUtils;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static liedge.limacore.lib.ModResources.prefixIdTranslationKey;

public abstract class LimaLanguageProvider extends LanguageProvider
{
    protected final ModResources modResources;

    public LimaLanguageProvider(PackOutput output, ModResources modResources, String locale)
    {
        super(output, modResources.modid(), locale);
        this.modResources = modResources;
    }

    public LimaLanguageProvider(PackOutput output, ModResources modResources)
    {
        this(output, modResources, "en_us");
    }

    public static String soundSubtitleKey(DeferredHolder<SoundEvent, SoundEvent> soundHolder)
    {
        return prefixIdTranslationKey("subtitle", soundHolder.getId());
    }

    protected void soundEvent(DeferredHolder<SoundEvent, SoundEvent> soundHolder, String value)
    {
        add(soundSubtitleKey(soundHolder), value);
    }

    protected void advancement(ResourceLocation id, String titleTranslation, String descriptionTranslation)
    {
        add(LimaAdvancementUtil.defaultAdvancementTitle(id), titleTranslation);
        add(LimaAdvancementUtil.defaultAdvancementDescription(id), descriptionTranslation);
    }

    protected void enchantment(ResourceKey<Enchantment> enchantmentKey, String translation)
    {
        add("enchantment", enchantmentKey.location().getPath(), translation);
    }

    protected void creativeTab(Supplier<? extends CreativeModeTab> tabSupplier, String value)
    {
        Component tabTitle = tabSupplier.get().getDisplayName();
        if (tabTitle.getContents() instanceof TranslatableContents translatable)
        {
            add(translatable.getKey(), value);
        }
        else
        {
            throw new IllegalArgumentException("Creative mode tab display name is not translatable");
        }
    }

    protected void fluidType(Supplier<? extends FluidType> supplier, String fluidValue)
    {
        add(supplier.get().getDescriptionId(), fluidValue);
    }

    protected void potion(Supplier<? extends PotionItem> item, String potionValue)
    {
        String name = LimaRegistryUtil.getItemName(item.get());
        add("item.minecraft.potion.effect." + name, "Potion of " + potionValue);
        add("item.minecraft.splash_potion.effect." + name, "Splash Potion of " + potionValue);
        add("item.minecraft.lingering_potion.effect." + name, "Lingering Potion of " + potionValue);
    }

    protected void damageTypeVariants(ResourceKey<DamageType> damageTypeKey, Consumer<BiConsumer<String, String>> collector)
    {
        String baseKey = prefixIdTranslationKey("death.attack", damageTypeKey.location());
        collector.accept((variant, value) -> {
            String key = StringUtils.isNotBlank(variant) ? baseKey + '.' + variant : baseKey;
            add(key, value);
        });
    }

    protected void damageTypeAndVariants(ResourceKey<DamageType> damageTypeKey, String defaultValue, Consumer<BiConsumer<String, String>> collector)
    {
        damageTypeVariants(damageTypeKey, c -> {
            c.accept("", defaultValue);
            collector.accept(c);
        });
    }

    protected void damageType(ResourceKey<DamageType> damageTypeKey, String value)
    {
        damageTypeAndVariants(damageTypeKey, value, collector -> {});
    }

    protected void entityAttackDamageType(ResourceKey<DamageType> damageTypeKey, String value, String itemValue)
    {
        damageTypeAndVariants(damageTypeKey, value, collector -> collector.accept("item", itemValue));
    }

    protected void ambientDamageType(ResourceKey<DamageType> damageTypeKey, String value, String playerValue)
    {
        damageTypeAndVariants(damageTypeKey, value, collector -> collector.accept("player", playerValue));
    }

    protected void add(Supplier<? extends Translatable> translatableSupplier, String value)
    {
        add(translatableSupplier.get(), value);
    }

    protected void add(Translatable translatable, String value)
    {
        add(translatable.descriptionId(), value);
    }

    protected void add(String prefix, String suffix, String value)
    {
        String key = prefix + "." + modResources.modid() + "." + suffix;
        add(key, value);
    }
}