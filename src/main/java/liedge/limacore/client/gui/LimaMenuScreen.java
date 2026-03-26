package liedge.limacore.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.LimaCore;
import liedge.limacore.lib.ModResources;
import liedge.limacore.menu.LimaMenu;
import liedge.limacore.menu.slot.LimaFluidSlot;
import liedge.limacore.network.IndexedStreamData;
import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.network.packet.ServerboundCustomMenuButtonPacket;
import liedge.limacore.network.packet.ServerboundFluidSlotClickPacket;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.transfer.LimaTransferUtil;
import liedge.limacore.util.LimaItemUtil;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class LimaMenuScreen<M extends LimaMenu<?>> extends AbstractContainerScreen<M>
{
    public static final int DEFAULT_WIDTH = 176;
    public static final int DEFAULT_HEIGHT = 166;
    public static final int DEFAULT_LABEL_COLOR = 4210752;

    private final List<LimaRenderable> tooltipWidgets = new ObjectArrayList<>();
    protected final int labelColor;
    protected final int primaryWidth;
    protected final int primaryHeight;
    protected final int leftPadding;
    protected final int rightPadding;
    protected final int topPadding;
    protected final int bottomPadding;

    protected int bottomPos;
    protected int rightPos;

    protected @Nullable LimaFluidSlot hoveredFluidSlot;

    protected LimaMenuScreen(M menu, Inventory inventory, Component title, int primaryWidth, int primaryHeight, int leftPadding, int rightPadding, int topPadding, int bottomPadding, int labelColor)
    {
        super(menu, inventory, title, primaryWidth + leftPadding + rightPadding, primaryHeight + topPadding + bottomPadding);
        this.primaryWidth = primaryWidth;
        this.primaryHeight = primaryHeight;
        this.leftPadding = leftPadding;
        this.rightPadding = rightPadding;
        this.topPadding = topPadding;
        this.bottomPadding = bottomPadding;
        this.labelColor = labelColor;
    }

    @Override
    protected void init()
    {
        this.leftPos = (this.width - this.imageWidth) / 2 + leftPadding;
        this.topPos = (this.height - this.imageHeight) / 2 + topPadding;

        this.rightPos = this.leftPos + this.primaryWidth;
        this.bottomPos = this.topPos + this.primaryHeight;

        positionLabels();
        addWidgets();

        tooltipWidgets.clear();
        for (Renderable r : renderables)
        {
            if (r instanceof LimaRenderable w) tooltipWidgets.add(w);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
    {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        extractTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int x, int y)
    {
        super.extractTooltip(graphics, x, y);

        // Render fluid slot tooltips
        if (hoveredFluidSlot != null)
        {
            FluidStack stack = hoveredFluidSlot.getFluid();
            if (!stack.isEmpty())
            {
                List<Component> lines = new ObjectArrayList<>();
                lines.add(stack.getHoverName());

                if (Minecraft.getInstance().options.advancedItemTooltips)
                {
                    String id = LimaRegistryUtil.getNonNullRegistryId(stack.typeHolder()).toString();
                    lines.add(Component.literal(id).withStyle(ChatFormatting.DARK_GRAY));
                }

                lines.add(Component.literal(LimaTransferUtil.formatStoredFluidMillibucket(stack.getAmount(), hoveredFluidSlot.getCapacity())).withStyle(ChatFormatting.GRAY));

                graphics.setTooltipForNextFrame(font, lines, Optional.empty(), x, y);
            }
        }

        // Render widget
        for (LimaRenderable widget : tooltipWidgets)
        {
            if (widget.isMouseOver(x, y) && widget.hasTooltip())
            {
                List<Either<FormattedText, TooltipComponent>> elements = new ObjectArrayList<>();
                widget.createWidgetTooltip(elements::add);
                graphics.setComponentTooltipFromElementsForNextFrame(font, elements, x, y, ItemStack.EMPTY);
            }
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY)
    {
        graphics.text(font, title, titleLabelX, titleLabelY, labelColor, false);
        graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, labelColor, false);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick)
    {
        Player player = minecraft.player;

        if (player != null && hoveredFluidSlot != null && LimaItemUtil.hasFluidHandlerCapability(ItemAccess.forPlayerCursor(player, menu)) && isHovering(hoveredFluidSlot.x(), hoveredFluidSlot.y(), 16, 16, event.x(), event.y()))
        {
            LimaFluidSlot.ClickAction action = switch (event.button())
            {
                case InputConstants.MOUSE_BUTTON_LEFT -> LimaFluidSlot.ClickAction.DRAIN;
                case InputConstants.MOUSE_BUTTON_RIGHT -> LimaFluidSlot.ClickAction.FILL;
                default -> null;
            };

            if (action != null) ClientPacketDistributor.sendToServer(new ServerboundFluidSlotClickPacket(menu.containerId, hoveredFluidSlot.slot(), action));
        }

        return super.mouseClicked(event, isDoubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double mouseX, double mouseY)
    {
        if (getFocused() != null && isDragging() && event.button() == InputConstants.MOUSE_BUTTON_LEFT)
        {
            return getFocused().mouseDragged(event, mouseX, mouseY);
        }
        else
        {
            return super.mouseDragged(event, mouseX, mouseY);
        }
    }

    protected void positionLabels()
    {
        titleLabelX = (primaryWidth - font.width(title)) / 2;
    }

    protected abstract void addWidgets();

    public @Nullable LimaFluidSlot getHoveredFluidSlot()
    {
        return hoveredFluidSlot;
    }

    public <T> void sendCustomButtonData(int buttonId, T value, NetworkSerializer<T> serializer)
    {
        ServerboundCustomMenuButtonPacket packet = new ServerboundCustomMenuButtonPacket(menu.containerId, new IndexedStreamData<>(buttonId, serializer, value));
        ClientPacketDistributor.sendToServer(packet);
    }

    public <T> void sendCustomButtonData(int buttonId, T value, Supplier<? extends NetworkSerializer<T>> supplier)
    {
        sendCustomButtonData(buttonId, value, supplier.get());
    }

    public void sendUnitButtonData(int buttonId)
    {
        sendCustomButtonData(buttonId, Unit.INSTANCE, LimaCoreNetworkSerializers.UNIT.get());
    }

    public boolean scrollFocusedElementInXYBounds(int x1, int y1, int x2, int y2, double mouseX, double mouseY, double scrollX, double scrollY)
    {
        if (LimaGuiUtil.isMouseWithinXYBounds(mouseX, mouseY, x1, y1, x2, y2) && getFocused() != null)
        {
            return getFocused().mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }
        else
        {
            return false;
        }
    }

    public boolean scrollFocusedElementInArea(int x, int y, int width, int height, double mouseX, double mouseY, double scrollX, double scrollY)
    {
        if (LimaGuiUtil.isMouseWithinArea(mouseX, mouseY, x, y, width, height) && getFocused() != null)
        {
            return getFocused().mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }
        else
        {
            return false;
        }
    }

    @EventBusSubscriber(modid = LimaCore.MODID, value = Dist.CLIENT)
    private static class FluidSlotRenderer
    {
        private FluidSlotRenderer() {}

        private static final Identifier HIGHLIGHT_BACK_SPRITE = ModResources.MC.id("container/slot_highlight_back");
        private static final Identifier HIGHLIGHT_FRONT_SPRITE = ModResources.MC.id("container/slot_highlight_front");

        @SubscribeEvent
        public static void renderFluidSlots(final ContainerScreenEvent.Render.Foreground event)
        {
            if (event.getContainerScreen() instanceof LimaMenuScreen<?> limaScreen)
            {
                GuiGraphicsExtractor graphics = event.getGuiGraphics();
                int mouseX = event.getMouseX();
                int mouseY = event.getMouseY();
                limaScreen.hoveredFluidSlot = null;

                for (int i = 0; i < limaScreen.menu.getFluidSlots().size(); i++)
                {
                    LimaFluidSlot fluidSlot = limaScreen.menu.getFluidSlots().get(i);
                    int slotX = fluidSlot.x();
                    int slotY = fluidSlot.y();

                    boolean hovering = limaScreen.isHovering(slotX, slotY, 16, 16, mouseX, mouseY);

                    if (hovering)
                    {
                        limaScreen.hoveredFluidSlot = fluidSlot;
                        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HIGHLIGHT_BACK_SPRITE, slotX - 4, slotY - 4, 24, 24);
                    }

                    FluidStack stack = fluidSlot.getFluid();
                    if (!stack.isEmpty()) LimaGuiUtil.renderFluidWithAmount(graphics, stack, slotX, slotY);

                    if (hovering)
                        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HIGHLIGHT_FRONT_SPRITE, slotX - 4, slotY - 4, 24, 24);
                }
            }
        }
    }
}