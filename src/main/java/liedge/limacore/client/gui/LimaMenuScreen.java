package liedge.limacore.client.gui;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.inventory.menu.LimaMenu;
import liedge.limacore.network.packet.ServerboundLimaMenuButtonPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public abstract class LimaMenuScreen<M extends LimaMenu<?>> extends AbstractContainerScreen<M>
{
    public static final int DEFAULT_WIDTH = 176;
    public static final int DEFAULT_HEIGHT = 166;
    public static final int DEFAULT_LABEL_COLOR = 4210752;

    protected final int labelColor;
    private final List<LimaRenderable> tooltipWidgets = new ObjectArrayList<>();

    protected LimaMenuScreen(M menu, Inventory inventory, Component title, int width, int height, int labelColor)
    {
        super(menu, inventory, title);
        this.imageWidth = width;
        this.imageHeight = height;

        this.titleLabelY = 7;
        this.inventoryLabelY = imageHeight - 94;

        this.labelColor = labelColor;
    }

    protected LimaMenuScreen(M menu, Inventory inventory, Component title, int width, int height)
    {
        this(menu, inventory, title, width, height, DEFAULT_LABEL_COLOR);
    }

    @Override
    protected void init()
    {
        super.init();

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
            if (widget.isMouseOver(x, y)) graphics.renderComponentTooltip(font, widget.getTooltipLines(), x, y);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
    {
        graphics.drawString(font, title, titleLabelX, titleLabelY, labelColor, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, labelColor, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
    {
        graphics.blit(getBgTexture(), leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    protected void positionLabels()
    {
        titleLabelX = (imageWidth - font.width(title)) / 2;
    }

    protected abstract void addWidgets();

    public abstract ResourceLocation getBgTexture();

    protected void sendCustomButtonClick(int buttonId, int value)
    {
        PacketDistributor.sendToServer(new ServerboundLimaMenuButtonPacket(menu.containerId, buttonId, value));
    }
}