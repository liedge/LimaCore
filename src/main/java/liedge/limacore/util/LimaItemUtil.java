package liedge.limacore.util;

import liedge.limacore.lib.ModResources;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiFunction;

public final class LimaItemUtil
{
    private LimaItemUtil() {}

    public static final Set<EquipmentSlot> HAND_EQUIPMENT_SLOTS = EnumSet.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
    public static final Set<EquipmentSlot> ARMOR_EQUIPMENT_SLOTS = EnumSet.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.BODY);

    public static boolean areItemsMergeCompatible(ItemStack existing, ItemStack other)
    {
        return existing.isEmpty() || ItemStack.isSameItemSameComponents(other, existing);
    }

    public static boolean canMergeItemStacks(ItemStack existing, ItemStack other, boolean allowPartialMerge)
    {
        if (areItemsMergeCompatible(existing, other))
        {
            int limit;
            if (existing.isEmpty())
            {
                limit = other.getMaxStackSize();
            }
            else
            {
                limit = existing.getMaxStackSize();
                limit -= existing.getCount();
            }

            if (limit <= 0) return false;

            return allowPartialMerge || other.getCount() <= limit;
        }

        return false;
    }

    public static boolean canMergeItemStacks(ItemStack existing, ItemStack other)
    {
        return canMergeItemStacks(existing, other, false);
    }

    public static <T> InteractionResultHolder<T> sidedFail(T stack, boolean isClientSide)
    {
        return isClientSide ? InteractionResultHolder.fail(stack) : InteractionResultHolder.consume(stack);
    }

    //#region Creative tab helpers
    public static CreativeModeTab.Builder tabBuilderWithTitle(ResourceLocation id)
    {
        return CreativeModeTab.builder()
                .title(Component.translatable(ModResources.prefixedIdLangKey("creative_tab", id)));
    }
    //#endregion

    //#region Capability check helpers
    public static boolean hasValidCapability(ItemCapability<?, Void> capability, ItemStack stack)
    {
        return stack.getCapability(capability) != null;
    }

    public static boolean hasEnergyCapability(ItemStack stack)
    {
        return hasValidCapability(Capabilities.EnergyStorage.ITEM, stack);
    }

    public static boolean hasItemHandlerCapability(ItemStack stack)
    {
        return hasValidCapability(Capabilities.ItemHandler.ITEM, stack);
    }

    public static boolean hasFluidHandlerCapability(ItemStack stack)
    {
        return hasValidCapability(Capabilities.FluidHandler.ITEM, stack);
    }
    //#endregion

    //#region Tool item helpers
    public static <T extends TieredItem> T createTieredTool(BiFunction<Tier, Item.Properties, T> constructor, @Nullable Item.Properties properties, Tier tier, float attackDamage, float attackSpeed)
    {
        if (properties == null) properties = new Item.Properties();
        return constructor.apply(tier, properties.attributes(DiggerItem.createAttributes(tier, attackDamage, attackSpeed)));
    }

    public static SwordItem createSword(@Nullable Item.Properties properties, Tier tier, float attackDamage, float attackSpeed)
    {
        return createTieredTool(SwordItem::new, properties, tier, attackDamage, attackSpeed);
    }

    public static ShovelItem createShovel(@Nullable Item.Properties properties, Tier tier, float attackDamage, float attackSpeed)
    {
        return createTieredTool(ShovelItem::new, properties, tier, attackDamage, attackSpeed);
    }

    public static PickaxeItem createPickaxe(@Nullable Item.Properties properties, Tier tier, float attackDamage, float attackSpeed)
    {
        return createTieredTool(PickaxeItem::new, properties, tier, attackDamage, attackSpeed);
    }

    public static AxeItem createAxe(@Nullable Item.Properties properties, Tier tier, float attackDamage, float attackSpeed)
    {
        return createTieredTool(AxeItem::new, properties, tier, attackDamage, attackSpeed);
    }

    public static HoeItem createHoe(@Nullable Item.Properties properties, Tier tier, float attackDamage, float attackSpeed)
    {
        return createTieredTool(HoeItem::new, properties, tier, attackDamage, attackSpeed);
    }

    public static ShearsItem createShears(@Nullable Item.Properties properties, int durability)
    {
        if (properties == null) properties = new Item.Properties();
        properties.durability(durability).component(DataComponents.TOOL, ShearsItem.createToolProperties());
        return new ShearsItem(properties);
    }
    //#endregion
}