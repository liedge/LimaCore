package liedge.limacore.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public final class LimaItemUtil
{
    private LimaItemUtil() {}

    public static final Set<EquipmentSlot> HAND_EQUIPMENT_SLOTS = EnumSet.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
    public static final Set<EquipmentSlot> ARMOR_EQUIPMENT_SLOTS = EnumSet.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.BODY);

    public static final Predicate<ItemStack> ALL_ITEMS = o -> true;
    public static final Predicate<ItemStack> ENERGY_ITEMS = o -> o.getCapability(Capabilities.EnergyStorage.ITEM) != null;

    public static boolean canCombineStacks(ItemStack existing, ItemStack other)
    {
        boolean a = existing.isEmpty() || ItemStack.isSameItemSameComponents(other, existing);
        boolean b = existing.getCount() + other.getCount() <= existing.getMaxStackSize();
        return a && b;
    }

    public static <T> InteractionResultHolder<T> sidedFail(T stack, boolean isClientSide)
    {
        return isClientSide ? InteractionResultHolder.fail(stack) : InteractionResultHolder.consume(stack);
    }

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