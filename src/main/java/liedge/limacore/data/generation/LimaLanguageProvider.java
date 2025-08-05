package liedge.limacore.data.generation;

import liedge.limacore.advancement.LimaAdvancementUtil;
import liedge.limacore.lib.ModResources;
import liedge.limacore.lib.Translatable;
import liedge.limacore.menu.LimaMenuType;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.fluids.FluidType;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static liedge.limacore.lib.ModResources.prefixedIdLangKey;

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

    /**
     * Takes an in-code object name, replaces underscores with a space, and capitalizes each word.
     * For use in english localization when localized name is just the formatted version of the in-code name.
     * @param path The name/path component of a resource location, resource key, or any other nameable game object.
     * @return The formatted name.
     */
    public static String localizeSimpleName(String path)
    {
        return Arrays.stream(path.split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
    }

    public static String localizeSimpleName(StringRepresentable representable)
    {
        return localizeSimpleName(representable.getSerializedName());
    }

    protected void soundEvent(Holder<SoundEvent> soundEvent, String translation)
    {
        add(LimaSoundDefinitionsProvider.defaultSubtitleKey(soundEvent), translation);
    }

    protected void advancement(ResourceLocation id, String titleTranslation, String descriptionTranslation)
    {
        add(LimaAdvancementUtil.defaultAdvancementTitleKey(id), titleTranslation);
        add(LimaAdvancementUtil.defaultAdvancementDescriptionKey(id), descriptionTranslation);
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

    protected void menuTitle(Supplier<? extends LimaMenuType<?, ?>> supplier, String value)
    {
        add(Objects.requireNonNull(supplier.get().getDefaultTitle(), "Menu does not have a default title"), value);
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
        String baseKey = prefixedIdLangKey("death.attack", damageTypeKey.location());
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