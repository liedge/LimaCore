package liedge.limacore.inventory.menu;

import liedge.limacore.lib.Translatable;
import liedge.limacore.util.LimaCoreUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public abstract class LimaMenuType<CTX, M extends LimaMenu<CTX>> extends MenuType<M> implements Translatable
{
    private final Class<CTX> contextClass;
    private final Constructor<CTX, M> constructor;
    private final String descriptionId;

    public LimaMenuType(ResourceLocation registryId, Class<CTX> contextClass, Constructor<CTX, M> constructor)
    {
        super((containerId, inv) -> {
            throw new UnsupportedOperationException("Parameterless menu creation not supported. Use createMenu or tryCreateMenu");
        }, FeatureFlags.DEFAULT_FLAGS);

        this.contextClass = contextClass;
        this.constructor = constructor;
        this.descriptionId = String.format("container.%s.%s", registryId.getNamespace(), registryId.getPath());
    }

    public Class<CTX> getContextClass()
    {
        return contextClass;
    }

    public void encodeUncheckedContext(Object uncheckedContext, FriendlyByteBuf net)
    {
        CTX menuContext = LimaCoreUtil.castOrThrow(contextClass, uncheckedContext);
        encodeContext(menuContext, net);
    }

    protected abstract void encodeContext(CTX menuContext, FriendlyByteBuf net);

    protected abstract CTX decodeContext(FriendlyByteBuf net, Inventory inventory);

    public abstract boolean canPlayerKeepUsing(CTX menuContext, Player player);

    public M createMenu(int containerId, Inventory inventory, CTX menuContext)
    {
        return constructor.newInstance(containerId, inventory, menuContext);
    }

    public M tryCreateMenu(int containerId, Inventory inventory, Object uncheckedContext)
    {
        return createMenu(containerId, inventory, LimaCoreUtil.castOrThrow(contextClass, uncheckedContext));
    }

    @Override
    public final M  create(int containerId, Inventory inventory)
    {
        throw new UnsupportedOperationException("Parameterless menu creation not supported. Use createMenu or tryCreateMenu");
    }

    @Override
    public final M create(int containerId, Inventory inventory, RegistryFriendlyByteBuf net)
    {
        CTX menuContext = decodeContext(net, inventory);
        return constructor.newInstance(containerId, inventory, menuContext);
    }

    @Override
    public String descriptionId()
    {
        return descriptionId;
    }

    @FunctionalInterface
    public interface Constructor<CTX, M extends LimaMenu<CTX>>
    {
        M newInstance(int containerId, Inventory inventory, CTX context);
    }
}