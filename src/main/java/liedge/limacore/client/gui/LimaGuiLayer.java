package liedge.limacore.client.gui;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.GuiLayer;

public abstract class LimaGuiLayer implements GuiLayer
{
    private final Identifier guiLayerId;

    protected LimaGuiLayer(Identifier guiLayerId)
    {
        this.guiLayerId = guiLayerId;
    }

    public void registerAbove(RegisterGuiLayersEvent event, Identifier otherLayer)
    {
        event.registerAbove(otherLayer, guiLayerId, this);
    }

    public void registerBelow(RegisterGuiLayersEvent event, Identifier otherLayer)
    {
        event.registerBelow(otherLayer, guiLayerId, this);
    }

    public Identifier getGuiLayerId()
    {
        return guiLayerId;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker)
    {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && isVisible(player))
        {
            renderGuiLayer(player, graphics, deltaTracker.getGameTimeDeltaPartialTick(true));
        }
    }

    protected boolean isVisible(LocalPlayer player)
    {
        return !player.isSpectator() && !Minecraft.getInstance().options.hideGui;
    }

    protected abstract void renderGuiLayer(LocalPlayer player, GuiGraphicsExtractor graphics, float partialTicks);
}