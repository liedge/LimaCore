package liedge.limacore.client.gui;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

public abstract class LimaGuiLayer implements LayeredDraw.Layer
{
    private final ResourceLocation guiLayerId;

    protected LimaGuiLayer(ResourceLocation guiLayerId)
    {
        this.guiLayerId = guiLayerId;
    }

    public void registerAbove(RegisterGuiLayersEvent event, ResourceLocation otherLayer)
    {
        event.registerAbove(otherLayer, guiLayerId, this);
    }

    public void registerBelow(RegisterGuiLayersEvent event, ResourceLocation otherLayer)
    {
        event.registerBelow(otherLayer, guiLayerId, this);
    }

    public ResourceLocation getGuiLayerId()
    {
        return guiLayerId;
    }

    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker)
    {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && shouldRender(player))
        {
            renderGuiLayer(player, graphics, deltaTracker.getGameTimeDeltaPartialTick(true));
        }
    }

    protected abstract boolean shouldRender(LocalPlayer player);

    protected abstract void renderGuiLayer(LocalPlayer player, GuiGraphics graphics, float partialTicks);
}