package liedge.limacore.client.gui;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.inventory.menu.LimaMenu;
import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.network.packet.ServerboundCustomMenuButtonPacket;
import liedge.limacore.registry.LimaCoreNetworkSerializers;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.function.Supplier;

public abstract class LimaMenuScreen<M extends LimaMenu<?>> extends AbstractContainerScreen<M>
{
    public static final int DEFAULT_WIDTH = 176;
    public static final int DEFAULT_HEIGHT = 166;
    public static final int DEFAULT_LABEL_COLOR = 4210752;

    private final List<LimaRenderable> tooltipWidgets = new ObjectArrayList<>();
    protected final int labelColor;
    protected final int primaryWidth;

    protected boolean alignInventoryLabelRight;
    protected int leftPadding;
    protected int rightPadding;
    protected int bottomPos;
    protected int rightPos;

    protected LimaMenuScreen(M menu, Inventory inventory, Component title, int primaryWidth, int height, int labelColor)
    {
        super(menu, inventory, title);
        this.primaryWidth = primaryWidth;
        this.imageHeight = height;
        this.labelColor = labelColor;
    }

    @Override
    protected void init()
    {
        this.imageWidth = primaryWidth + leftPadding + rightPadding;

        this.leftPos = (this.width - this.imageWidth) / 2 + leftPadding;
        this.topPos = (this.height - this.imageHeight) / 2;

        this.rightPos = this.leftPos + this.primaryWidth;
        this.bottomPos = this.topPos + imageHeight;

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
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int x, int y)
    {
        super.renderTooltip(graphics, x, y);

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

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
    {
        graphics.blit(getBgTexture(), leftPos, topPos, 0, 0, primaryWidth, imageHeight);
    }

    protected void positionLabels()
    {
        titleLabelX = (primaryWidth - font.width(title)) / 2;
    }

    protected abstract void addWidgets();

    public abstract ResourceLocation getBgTexture();

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
}