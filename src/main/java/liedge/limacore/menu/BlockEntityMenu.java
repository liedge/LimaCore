package liedge.limacore.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.slot.HandlerFluidSlot;
import liedge.limacore.menu.slot.LimaItemSlot;
import liedge.limacore.menu.slot.RecipeResultSlot;
import liedge.limacore.transfer.fluid.FluidHolderBlockEntity;
import liedge.limacore.transfer.fluid.LimaBlockEntityFluids;
import liedge.limacore.transfer.item.ItemHolderBlockEntity;
import liedge.limacore.transfer.item.LimaBlockEntityItems;
import liedge.limacore.util.LimaCoreObjects;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.UnaryOperator;

public abstract class BlockEntityMenu<CTX extends ItemHolderBlockEntity> extends LimaMenu<CTX>
{
    protected BlockEntityMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext)
    {
        super(type, containerId, inventory, menuContext);
    }

    // Slot addition helpers
    protected void addSlot(BlockContentsType contentsType, int resourceIndex, int x, int y, UnaryOperator<LimaItemSlot> modifier)
    {
        addSlot(modifier.apply(LimaItemSlot.create(getItems(contentsType), resourceIndex, x, y)));
    }

    protected void addSlot(BlockContentsType contentsType, int resourceIndex, int x, int y)
    {
        addSlot(contentsType, resourceIndex, x, y, UnaryOperator.identity());
    }

    protected void addSlotsGrid(BlockContentsType contentsType, int startIndex, int x, int y, int width, int height, UnaryOperator<LimaItemSlot> modifier)
    {
        addSlotsGrid(getItems(contentsType), startIndex, x, y, width, height, (container, resourceIndex, slotX, slotY) ->
                modifier.apply(LimaItemSlot.create(container, resourceIndex, slotX, slotY)));
    }

    protected void addSlotsGrid(BlockContentsType contentsType, int startIndex, int x, int y, int width, int height)
    {
        addSlotsGrid(contentsType, startIndex, x, y, width, height, UnaryOperator.identity());
    }

    protected void addOutputSlot(int resourceIndex, int x, int y)
    {
        addSlot(BlockContentsType.OUTPUT, resourceIndex, x, y, slot -> slot.allowPlacement(false));
    }

    protected void addOutputSlotsGrid(int startIndex, int x, int y, int columns, int rows)
    {
        addSlotsGrid(BlockContentsType.OUTPUT, startIndex, x, y, columns, rows, slot -> slot.allowPlacement(false));
    }

    protected void addRecipeOutputSlot(int resourceIndex, int x, int y, RecipeType<?> recipeType)
    {
        addSlot(new RecipeResultSlot(getItems(BlockContentsType.OUTPUT), resourceIndex, x, y, playerInventory.player, recipeType));
    }

    protected void addRecipeOutputSlot(int resourceIndex, int x, int y, Holder<RecipeType<?>> typeHolder)
    {
        addRecipeOutputSlot(resourceIndex, x, y, typeHolder.value());
    }

    protected void addRecipeOutputSlotGrid(int startIndex, int x, int y, int width, int height, RecipeType<?> recipeType)
    {
        addSlotsGrid(getItems(BlockContentsType.OUTPUT), startIndex, x, y, width, height, (container, resourceIndex, slotX, slotY) ->
                new RecipeResultSlot(container, resourceIndex, slotX, slotY, playerInventory.player, recipeType));
    }

    protected void addRecipeOutputSlotGrid(int startIndex, int x, int y, int width, int height, Holder<RecipeType<?>> typeHolder)
    {
        addRecipeOutputSlotGrid(startIndex, x, y, width, height, typeHolder.value());
    }

    // Fluid slot addition helpers
    protected void addFluidSlot(BlockContentsType contentsType, int resourceIndex, int x, int y, UnaryOperator<HandlerFluidSlot> modifier)
    {
        LimaBlockEntityFluids fluids = getFluids(contentsType);
        addFluidSlot(index -> modifier.apply(new HandlerFluidSlot(x, y, index, fluids, resourceIndex)));
    }

    protected void addFluidSlot(BlockContentsType contentsType, int resourceIndex, int x, int y)
    {
        addFluidSlot(contentsType, resourceIndex, x, y, UnaryOperator.identity());
    }

    protected void addFluidSlotsGrid(BlockContentsType contentsType, int startIndex, int x, int y, int width, int height, UnaryOperator<HandlerFluidSlot> modifier)
    {
        LimaBlockEntityFluids fluids = getFluids(contentsType);
        addFluidSlotsGrid(fluids, startIndex, x, y, width, height, (container, slotIndex, slotX, slotY, resourceIndex) ->
                modifier.apply(new HandlerFluidSlot(slotX, slotY, slotIndex, container, resourceIndex)));
    }

    protected void addFluidSlotsGrid(BlockContentsType contentsType, int startIndex, int x, int y, int width, int height)
    {
        addFluidSlotsGrid(contentsType, startIndex, x, y, width, height, UnaryOperator.identity());
    }

    private LimaBlockEntityItems getItems(BlockContentsType contentsType)
    {
        return menuContext.getItemsOrThrow(contentsType);
    }

    private LimaBlockEntityFluids getFluids(BlockContentsType contentsType)
    {
        return LimaCoreObjects.cast(FluidHolderBlockEntity.class, menuContext).getFluidsOrThrow(contentsType);
    }
}