package liedge.limacore.menu.slot;

import liedge.limacore.registry.game.LimaCoreTriggerTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import java.util.Optional;

public class RecipeResultSlot extends LimaItemSlot
{
    private final Player player;
    private final RecipeType<?> recipeType;

    private int removeCount;

    public RecipeResultSlot(ItemStacksResourceHandler handler, int index, int xPosition, int yPosition, Player player, RecipeType<?> recipeType)
    {
        super(handler, index, xPosition, yPosition);
        this.allowPlace = false;
        this.player = player;
        this.recipeType = recipeType;
    }

    @Override
    public boolean reverseQuickTransfer()
    {
        return true;
    }

    @Override
    public Optional<ItemStack> tryRemove(int amount, int maxAmount, Player player)
    {
        Optional<ItemStack> result = super.tryRemove(amount, maxAmount, player);
        result.ifPresent(stack -> this.removeCount += stack.getCount());
        return result;
    }

    @Override
    public void onTake(Player player, ItemStack carried)
    {
        checkTakeAchievements(carried);
        super.onTake(player, carried);
    }

    @Override
    public void onQuickCraft(ItemStack picked, ItemStack original)
    {
        int count = original.count() - picked.count();
        if (count > 0) onQuickCraft(original, count);
    }

    @Override
    protected void onQuickCraft(ItemStack picked, int count)
    {
        removeCount += count;
        checkTakeAchievements(picked);
    }

    @Override
    protected void checkTakeAchievements(ItemStack carried)
    {
        if (removeCount > 0)
        {
            if (!carried.isEmpty())
            {
                carried.onCraftedBy(player, removeCount);
                if (player instanceof ServerPlayer serverPlayer)
                {
                    LimaCoreTriggerTypes.CUSTOM_RECIPE_TYPE_USED.get().triggerCriterion(serverPlayer, recipeType, carried);
                }
            }

            removeCount = 0;
        }
    }
}