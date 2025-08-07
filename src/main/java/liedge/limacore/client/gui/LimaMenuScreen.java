package liedge.limacore.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.LimaCore;
import liedge.limacore.capability.fluid.LimaFluidUtil;
import liedge.limacore.menu.LimaMenu;
import liedge.limacore.menu.slot.LimaFluidSlot;
import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.network.packet.ServerboundCustomMenuButtonPacket;
import liedge.limacore.network.packet.ServerboundFluidSlotClickPacket;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaItemUtil;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

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

    protected int leftPadding;
    protected int rightPadding;
    protected int topPadding;
    protected int bottomPadding;

    protected int bottomPos;
    protected int rightPos;

    protected @Nullable LimaFluidSlot hoveredFluidSlot;

    protected LimaMenuScreen(M menu, Inventory inventory, Component title, int primaryWidth, int primaryHeight, int labelColor)
    {
        super(menu, inventory, title);
        this.primaryWidth = primaryWidth;
        this.primaryHeight = primaryHeight;
        this.labelColor = labelColor;
    }

    @Override
    protected void init()
    {
        this.imageWidth = primaryWidth + leftPadding + rightPadding;
        this.imageHeight = primaryHeight + topPadding + bottomPadding;

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
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int x, int y)
    {
        super.renderTooltip(graphics, x, y);

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
                    String id = LimaRegistryUtil.getNonNullRegistryId(stack.getFluidHolder()).toString();
                    lines.add(Component.literal(id).withStyle(ChatFormatting.DARK_GRAY));
                }

                lines.add(Component.literal(LimaFluidUtil.formatStoredFluidMillibucket(stack.getAmount(), hoveredFluidSlot.getCapacity())).withStyle(ChatFormatting.GRAY));

                graphics.renderTooltip(font, lines, Optional.empty(), x, y);
            }
        }

        // Render widget
        for (LimaRenderable widget : tooltipWidgets)
        {
            if (widget.isMouseOver(x, y) && widget.hasTooltip())
            {
                List<Either<FormattedText, TooltipComponent>> list = new ObjectArrayList<>();
                widget.createWidgetTooltip(list::add);
                graphics.renderComponentTooltipFromElements(font, list, x, y, ItemStack.EMPTY);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
    {
        graphics.drawString(font, title, titleLabelX, titleLabelY, labelColor, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, labelColor, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (hoveredFluidSlot != null && LimaItemUtil.hasFluidHandlerCapability(menu.getCarried()) && isHovering(hoveredFluidSlot.x(), hoveredFluidSlot.y(), 16, 16, mouseX, mouseY))
        {
            LimaFluidSlot.ClickAction action = switch (button)
            {
                case GLFW.GLFW_MOUSE_BUTTON_LEFT -> LimaFluidSlot.ClickAction.DRAIN;
                case GLFW.GLFW_MOUSE_BUTTON_RIGHT -> LimaFluidSlot.ClickAction.FILL;
                default -> null;
            };

            if (action != null)
                PacketDistributor.sendToServer(new ServerboundFluidSlotClickPacket(menu.containerId, hoveredFluidSlot.index(), action));
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        if (getFocused() != null && isDragging() && button == 0)
        {
            return getFocused().mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
        else
        {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY)
    {
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
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
        PacketDistributor.sendToServer(new ServerboundCustomMenuButtonPacket<>(menu.containerId, buttonId, serializer, value));
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

        @SubscribeEvent
        public static void renderFluidSlots(final ContainerScreenEvent.Render.Foreground event)
        {
            if (event.getContainerScreen() instanceof LimaMenuScreen<?> limaScreen)
            {
                GuiGraphics graphics = event.getGuiGraphics();
                int mouseX = event.getMouseX();
                int mouseY = event.getMouseY();
                limaScreen.hoveredFluidSlot = null;

                for (int i = 0; i < limaScreen.menu.getFluidSlots().size(); i++)
                {
                    LimaFluidSlot fluidSlot = limaScreen.menu.getFluidSlots().get(i);
                    int slotX = fluidSlot.x();
                    int slotY = fluidSlot.y();
                    FluidStack stack = fluidSlot.getFluid();

                    if (!stack.isEmpty())
                    {
                        LimaGuiUtil.blitTintedFluidSprite(graphics, stack, slotX, slotY);

                        PoseStack poseStack = graphics.pose();
                        poseStack.pushPose();

                        String amountText = LimaFluidUtil.formatCompactFluidAmount(stack.getAmount());
                        int textWidth = LimaGuiUtil.halfTextWidth(amountText);
                        poseStack.translate(slotX + 16 - textWidth, slotY + 11, 5);
                        poseStack.scale(0.5f, 0.5f, 1f);

                        graphics.drawString(Minecraft.getInstance().font, amountText, 0, 0, -1, true);

                        poseStack.popPose();
                    }

                    if (limaScreen.isHovering(slotX, slotY, 16, 16, mouseX, mouseY))
                    {
                        limaScreen.hoveredFluidSlot = fluidSlot;
                        renderSlotHighlight(graphics, slotX, slotY, 0);
                    }
                }
            }
        }
    }
}