package liedge.limacore.inventory.slot;

import liedge.limacore.registry.LimaCoreTriggerTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public class RecipeResultSlot extends LimaMenuSlot
{
    private final Player player;
    private final RecipeType<?> recipeType;

    private int removeCount = 0;

    public RecipeResultSlot(Player player, RecipeType<?> recipeType, IItemHandlerModifiable handler, int slotIndex, int x, int y)
    {
        super(handler, false, slotIndex, x, y);
        this.player = player;
        this.recipeType = recipeType;
    }

    @Override
    public ItemStack remove(int amount)
    {
        if (hasItem()) removeCount += Math.min(amount, getItem().getCount());

        return super.remove(amount);
    }

    @Override
    public void onTake(Player player, ItemStack stack)
    {
        checkTakeAchievements(stack);
        super.onTake(player, stack);
    }

    @Override
    public void onQuickCraft(ItemStack oldStack, ItemStack newStack)
    {
        int i = newStack.getCount() - oldStack.getCount();
        if (i > 0) onQuickCraft(newStack, i);
    }

    @Override
    protected void onQuickCraft(ItemStack stack, int amount)
    {
        removeCount += amount;
        checkTakeAchievements(stack);
    }

    @Override
    protected void checkTakeAchievements(ItemStack stack)
    {
        if (removeCount > 0)
        {
            stack.onCraftedBy(player.level(), player, removeCount);
            if (player instanceof ServerPlayer serverPlayer)
            {
                LimaCoreTriggerTypes.CUSTOM_RECIPE_TYPE_USED.get().trigger(serverPlayer, recipeType, stack);
            }

            removeCount = 0;
        }
    }
}